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

/**
 * Repository para gerenciar listas de compras no Firestore
 */
class ShoppingRepository(
    private val authRepository: AuthRepository
) {
    private val firestore = FirebaseFirestore.getInstance()
    
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
     * Adiciona um item à lista atual
     */
    suspend fun addItem(familyId: String, item: ShoppingItem): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            val doc = docRef.get().await()
            
            val currentList = if (doc.exists()) {
                ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            } else {
                ShoppingList(id = familyId, familyId = familyId)
            }
            
            val updatedItems = currentList.items + item
            val updatedList = currentList.copy(
                familyId = familyId, // Garantir familyId correto
                items = updatedItems
            )
            
            docRef.set(updatedList.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Atualiza um item existente
     */
    suspend fun updateItem(familyId: String, itemId: String, updatedItem: ShoppingItem): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            val doc = docRef.get().await()
            
            if (!doc.exists()) {
                return Result.failure(Exception("Lista não encontrada"))
            }
            
            val currentList = ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            val updatedItems = currentList.items.map { 
                if (it.id == itemId) updatedItem else it 
            }
            val updatedList = currentList.copy(
                familyId = familyId, // Garantir familyId correto
                items = updatedItems
            )
            
            docRef.set(updatedList.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove um item da lista
     */
    suspend fun deleteItem(familyId: String, itemId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            val doc = docRef.get().await()
            
            if (!doc.exists()) {
                return Result.failure(Exception("Lista não encontrada"))
            }
            
            val currentList = ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            val updatedItems = currentList.items.filter { it.id != itemId }
            val updatedList = currentList.copy(
                familyId = familyId, // Garantir familyId correto
                items = updatedItems
            )
            
            docRef.set(updatedList.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Marca/desmarca um item como comprado
     */
    suspend fun toggleItemChecked(familyId: String, itemId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("currentLists").document(familyId)
            val doc = docRef.get().await()
            
            if (!doc.exists()) {
                return Result.failure(Exception("Lista não encontrada"))
            }
            
            val currentList = ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            val updatedItems = currentList.items.map { item ->
                if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
            }
            val updatedList = currentList.copy(
                familyId = familyId, // Garantir familyId correto
                items = updatedItems
            )
            
            docRef.set(updatedList.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finaliza a compra atual e move para o histórico
     */
    suspend fun finishShopping(familyId: String): Result<Unit> {
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
            
            // Criar entrada no histórico
            val completedList = currentList.copy(
                familyId = familyId, // Garantir familyId correto
                status = ListStatus.COMPLETED,
                completedAt = Date()
            )
            
            firestore.collection("history")
                .add(completedList.toMap())
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
     * Busca o histórico de compras da família
     */
    suspend fun getHistory(familyId: String): List<ShoppingList> {
        return try {
            val snapshot = firestore.collection("history")
                .whereEqualTo("familyId", familyId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.map { doc ->
                ShoppingList.fromMap(doc.id, doc.data ?: emptyMap())
            }
        } catch (e: Exception) {
            emptyList()
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
