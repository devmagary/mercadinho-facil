package com.shoppinglist.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.shoppinglist.data.model.ListStatus
import com.shoppinglist.data.model.ShoppingItem
import com.shoppinglist.data.model.ShoppingList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerenciar listas de compras no Firestore
 */
@Singleton
class ShoppingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    /**
     * Observa a lista de compras ativa da família em tempo real
     */
    fun observeCurrentList(familyId: String): Flow<ShoppingList?> = callbackFlow {
        val listener = firestore.collection("currentLists")
            .document(familyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val list = ShoppingList.fromMap(snapshot.id, snapshot.data ?: emptyMap())
                    trySend(list)
                } else {
                    // Lista não existe, criar uma vazia
                    trySend(ShoppingList(id = familyId, familyId = familyId))
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Adiciona um item à lista atual de forma atômica
     */
    suspend fun addItem(familyId: String, item: ShoppingItem): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            // Usa arrayUnion para adicionar o item de forma atômica.
            // O toMap() converte o data object em um mapa para o Firestore.
            docRef.update("items", com.google.firebase.firestore.FieldValue.arrayUnion(item.toMap())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Se o documento não existir, o update falha.
            // Então, criamos o documento com o primeiro item.
            if (e is com.google.firebase.firestore.FirebaseFirestoreException && e.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.NOT_FOUND) {
                try {
                    val docRef = firestore.collection("currentLists").document(familyId)
                    val newList = ShoppingList(id = familyId, familyId = familyId, items = listOf(item))
                    docRef.set(newList.toMap()).await()
                    Result.success(Unit)
                } catch (e2: Exception) {
                    Result.failure(e2)
                }
            } else {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Atualiza um item existente usando uma transação para segurança
     */
    suspend fun updateItem(familyId: String, itemId: String, updatedItem: ShoppingItem): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw FirebaseFirestoreException("Lista não encontrada", FirebaseFirestoreException.Code.NOT_FOUND)
                }

                val currentList = ShoppingList.fromMap(snapshot.id, snapshot.data ?: emptyMap())
                val updatedItems = currentList.items.map { 
                    if (it.id == itemId) updatedItem else it 
                }
                
                transaction.update(docRef, "items", updatedItems.map { it.toMap() })
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove um item da lista usando uma transação
     */
    suspend fun deleteItem(familyId: String, itemId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw FirebaseFirestoreException("Lista não encontrada", FirebaseFirestoreException.Code.NOT_FOUND)
                }
                
                val currentList = ShoppingList.fromMap(snapshot.id, snapshot.data ?: emptyMap())
                val itemToRemove = currentList.items.find { it.id == itemId }
                
                if (itemToRemove != null) {
                    transaction.update(docRef, "items", com.google.firebase.firestore.FieldValue.arrayRemove(itemToRemove.toMap()))
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Marca/desmarca um item como comprado usando uma transação
     */
    suspend fun toggleItemChecked(familyId: String, itemId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                     throw FirebaseFirestoreException("Lista não encontrada", FirebaseFirestoreException.Code.NOT_FOUND)
                }

                val currentList = ShoppingList.fromMap(snapshot.id, snapshot.data ?: emptyMap())
                val updatedItems = currentList.items.map { item ->
                    if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
                }
                transaction.update(docRef, "items", updatedItems.map { it.toMap() })
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finaliza a compra atual e move para o histórico
     * @param familyId ID da família
     * @param listName Nome personalizado da lista (opcional)
     * @param manualTotalValue Valor total manual da lista (opcional - se null, usa o valor calculado dos itens)
     */
    suspend fun finishShopping(familyId: String, listName: String?, manualTotalValue: Double? = null): Result<Unit> {
        return try {
            val currentDocRef = firestore.collection("currentLists").document(familyId)
            val doc = currentDocRef.get().await()
            
            if (!doc.exists()) {
                return Result.failure(Exception("Lista não encontrada"))
            }
            
            val currentList = ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            
            if (currentList.items.isEmpty()) {
                return Result.failure(Exception("Lista está vazia"))
            }
            
            // Usar valor manual se fornecido, senão calcular dos itens
            val totalValue = manualTotalValue ?: currentList.calculateTotal()
            
            // Criar entrada no histórico com data de conclusão
            val completedAt = Date()
            val completedList = currentList.copy(
                familyId = familyId, // Garantir familyId correto
                name = listName, // Nome fornecido pelo usuário
                status = ListStatus.COMPLETED,
                completedAt = completedAt
            )
            
            // Debug: Verificar valores antes de salvar
            android.util.Log.d("ShoppingRepository", "Salvando histórico:")
            android.util.Log.d("ShoppingRepository", "- Nome: $listName")
            android.util.Log.d("ShoppingRepository", "- CompletedAt: $completedAt")
            android.util.Log.d("ShoppingRepository", "- Total: $totalValue (manual: ${manualTotalValue != null})")
            
            // Salvar no histórico com o valor total (manual ou calculado)
            val historyData = completedList.toMap().toMutableMap()
            historyData["totalValue"] = totalValue
            
            android.util.Log.d("ShoppingRepository", "HistoryData map: $historyData")
            
            firestore.collection("history")
                .add(historyData)
                .await()
            
            // Limpar lista atual
            val emptyList = ShoppingList(id = familyId, familyId = familyId)
            currentDocRef.set(emptyList.toMap()).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Atualiza o nome da lista atual
     */
    suspend fun updateListName(familyId: String, name: String?): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            val doc = docRef.get().await()
            
            if (!doc.exists()) {
                return Result.failure(Exception("Lista não encontrada"))
            }
            
            val currentList = ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            val updatedList = currentList.copy(
                familyId = familyId,
                name = name
            )
            
            docRef.set(updatedList.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Busca o histórico de compras da família
     */
    suspend fun getHistory(familyId: String): Result<List<ShoppingList>> {
        return try {
            val snapshot = firestore.collection("history")
                .whereEqualTo("familyId", familyId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val list = snapshot.documents.map { doc ->
                ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clona uma lista do histórico para a lista atual
     */
    suspend fun cloneHistoryList(familyId: String, historyListId: String): Result<Unit> {
        return try {
            // Buscar a lista do histórico
            val historyDoc = firestore.collection("history")
                .document(historyListId)
                .get()
                .await()
            
            if (!historyDoc.exists()) {
                return Result.failure(Exception("Lista do histórico não encontrada"))
            }
            
            val historyList = ShoppingList.fromMap(historyDoc.id, historyDoc.data ?: emptyMap())
            
            // Resetar os itens (desmarcar todos)
            val resetItems = historyList.items.map { it.copy(isChecked = false, createdAt = Date()) }
            
            // Adicionar à lista atual
            val currentDocRef = firestore.collection("currentLists").document(familyId)
            val currentList = ShoppingList(
                id = familyId,
                familyId = familyId,
                items = resetItems,
                status = ListStatus.ACTIVE
            )
            
            currentDocRef.set(currentList.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
