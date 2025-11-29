package com.shoppinglist.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Modelo de dados para uma lista de compras (atual ou do histórico)
 */
data class ShoppingList(
    @DocumentId
    val id: String = "",
    val familyId: String = "",
    val name: String? = null, // Nome personalizado da lista
    val items: List<ShoppingItem> = emptyList(),
    val status: ListStatus = ListStatus.ACTIVE,
    @ServerTimestamp
    val createdAt: Date? = null,
    val completedAt: Date? = null,
    val totalValue: Double = 0.0
) {
    /**
     * Calcula o valor total da lista
     */
    fun calculateTotal(): Double {
        return items.sumOf { it.getSubtotal() }
    }
    
    /**
     * Converte para Map para salvar no Firestore
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "familyId" to familyId,
            "name" to name,
            "items" to items.map { it.toMap() },
            "status" to status.name,
            "createdAt" to createdAt,
            "completedAt" to completedAt,
            "totalValue" to calculateTotal()
        )
    }
    
    companion object {
        /**
         * Cria uma ShoppingList a partir de um Map do Firestore
         */
        fun fromMap(id: String, map: Map<String, Any?>): ShoppingList {
            val itemsList = (map["items"] as? List<Map<String, Any?>>)?.mapIndexed { index, itemMap ->
                ShoppingItem.fromMap(index.toString(), itemMap)
            } ?: emptyList()
            
            val createdAtDate = (map["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: map["createdAt"] as? Date
            val completedAtDate = (map["completedAt"] as? com.google.firebase.Timestamp)?.toDate() ?: map["completedAt"] as? Date
            
            return ShoppingList(
                id = id,
                familyId = map["familyId"] as? String ?: "",
                name = map["name"] as? String,
                items = itemsList,
                status = ListStatus.valueOf(map["status"] as? String ?: "ACTIVE"),
                createdAt = createdAtDate,
                // Se completedAt for null mas status é COMPLETED, usar createdAt como fallback
                completedAt = completedAtDate ?: (if (map["status"] == "COMPLETED") createdAtDate else null),
                totalValue = (map["totalValue"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
}

/**
 * Enum para status da lista
 */
enum class ListStatus {
    ACTIVE,      // Lista ativa atual
    COMPLETED    // Lista finalizada (no histórico)
}
