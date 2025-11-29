package com.shoppinglist.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Modelo de dados para um item da lista de compras
 */
data class ShoppingItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val quantity: Double = 1.0,
    val unit: MeasureUnit = MeasureUnit.UN,
    val price: Double? = null,
    val imageUrl: String? = null,
    val isChecked: Boolean = false,
    @ServerTimestamp
    val createdAt: Date? = null
) {
    /**
     * Calcula o subtotal do item (quantidade * preço)
     */
    fun getSubtotal(): Double {
        return if (price != null) quantity * price else 0.0
    }
    
    /**
     * Converte para Map para salvar no Firestore
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "quantity" to quantity,
            "unit" to unit.name,
            "price" to price,
            "imageUrl" to imageUrl,
            "isChecked" to isChecked,
            "createdAt" to createdAt
        )
    }
    
    companion object {
        /**
         * Cria um ShoppingItem a partir de um Map do Firestore
         */
        fun fromMap(id: String, map: Map<String, Any?>): ShoppingItem {
            return ShoppingItem(
                id = id,
                name = map["name"] as? String ?: "",
                quantity = (map["quantity"] as? Number)?.toDouble() ?: 1.0,
                unit = MeasureUnit.valueOf(map["unit"] as? String ?: "UN"),
                price = (map["price"] as? Number)?.toDouble(),
                imageUrl = map["imageUrl"] as? String,
                isChecked = map["isChecked"] as? Boolean ?: false,
                createdAt = map["createdAt"] as? Date
            )
        }
    }
}

/**
 * Enum para unidades de medida
 */
enum class MeasureUnit(val displayName: String) {
    UN("Un"),
    KG("Kg"),
    G("g"),
    L("L");
    
    companion object {
        fun getAll(): List<MeasureUnit> = values().toList()
    }
}
