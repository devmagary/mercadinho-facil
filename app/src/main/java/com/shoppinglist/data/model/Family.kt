package com.shoppinglist.data.model

import com.google.firebase.firestore.DocumentId

/**
 * Modelo de dados para um grupo familiar
 */
data class Family(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val memberIds: List<String> = emptyList(),
    val inviteCode: String = "",
    val ownerId: String = ""
) {
    /**
     * Converte para Map para salvar no Firestore
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "memberIds" to memberIds,
            "inviteCode" to inviteCode,
            "ownerId" to ownerId
        )
    }
    
    companion object {
        /**
         * Cria uma Family a partir de um Map do Firestore
         */
        fun fromMap(id: String, map: Map<String, Any?>): Family {
            return Family(
                id = id,
                name = map["name"] as? String ?: "",
                memberIds = map["memberIds"] as? List<String> ?: emptyList(),
                inviteCode = map["inviteCode"] as? String ?: "",
                ownerId = map["ownerId"] as? String ?: ""
            )
        }
        
        /**
         * Gera um código de convite aleatório
         */
        fun generateInviteCode(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return (1..6)
                .map { chars.random() }
                .joinToString("")
        }
    }
}
