package com.shoppinglist.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.shoppinglist.data.model.Family
import com.shoppinglist.data.model.User
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Repository para gerenciar autenticação e dados de usuários
 */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Retorna o usuário atualmente autenticado
     */
    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser
    
    /**
     * Retorna o ID do usuário atual
     */
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    /**
     * Realiza login com email e senha
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Usuário não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Cria uma nova conta
     */
    suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Criar documento do usuário no Firestore
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    name = name
                )
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user.toMap())
                    .await()
                
                Result.success(firebaseUser)
            } ?: Result.failure(Exception("Erro ao criar usuário"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Faz logout
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Busca dados do usuário atual do Firestore
     */
    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            if (doc.exists()) {
                User.fromMap(doc.id, doc.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Cria uma nova família
     */
    suspend fun createFamily(familyName: String): Result<Family> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuário não autenticado"))
        
        return try {
            val inviteCode = Family.generateInviteCode()
            val family = Family(
                name = familyName,
                memberIds = listOf(userId),
                inviteCode = inviteCode,
                ownerId = userId
            )
            
            val docRef = firestore.collection("families")
                .add(family.toMap())
                .await()
            
            // Atualizar o usuário com o familyId
            firestore.collection("users")
                .document(userId)
                .update("familyId", docRef.id)
                .await()
            
            Result.success(family.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Entra em uma família usando o código de convite
     */
    suspend fun joinFamily(inviteCode: String): Result<Family> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuário não autenticado"))
        
        return try {
            val querySnapshot = firestore.collection("families")
                .whereEqualTo("inviteCode", inviteCode)
                .get()
                .await()
            
            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("Código de convite inválido"))
            }
            
            val familyDoc = querySnapshot.documents.first()
            val familyId = familyDoc.id
            
            // Adicionar usuário à família
            firestore.collection("families")
                .document(familyId)
                .update("memberIds", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .await()
            
            // Atualizar usuário
            firestore.collection("users")
                .document(userId)
                .update("familyId", familyId)
                .await()
            
            val family = Family.fromMap(familyId, familyDoc.data ?: emptyMap())
            Result.success(family)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /**
     * Busca dados da família pelo ID
     */
    suspend fun getFamily(familyId: String): Family? {
        return try {
            val doc = firestore.collection("families")
                .document(familyId)
                .get()
                .await()
            
            if (doc.exists()) {
                Family.fromMap(doc.id, doc.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
