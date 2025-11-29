package com.shoppinglist.data.model

import com.google.firebase.firestore.DocumentId

/**
 * Modelo de dados para um usuário
 */
data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val familyId: String? = null
) {
    /**
     * Converte para Map para salvar no Firestore
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "name" to name,
            "familyId" to familyId
        )
    }
    
    companion object {
        /**
         * Cria um User a partir de um Map do Firestore
         */
        fun fromMap(id: String, map: Map<String, Any?>): User {
            return User(
                id = id,
                email = map["email"] as? String ?: "",
                name = map["name"] as? String ?: "",
                familyId = map["familyId"] as? String
            )
        }
    }
}
