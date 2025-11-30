package com.shoppinglist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shoppinglist.data.model.MeasureUnit
import com.shoppinglist.data.model.ShoppingItem

@Composable
fun AddEditItemDialog(
    item: ShoppingItem? = null,
    onDismiss: () -> Unit,
    onSave: (ShoppingItem) -> Unit
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var quantity by remember(item) { mutableStateOf(item?.quantity?.toString() ?: "1") }
    var unit by remember(item) { mutableStateOf(item?.unit ?: MeasureUnit.UN) }
    var price by remember(item) { mutableStateOf(item?.price?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (item == null) "Adicionar Item" else "Editar Item",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Item") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Qtd") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Placeholder for Unit selector if needed
                    }

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Preço (Opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("R$ ") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val newItem = item?.copy(
                                    name = name,
                                    quantity = quantity.toDoubleOrNull() ?: 1.0,
                                    unit = unit,
                                    price = price.toDoubleOrNull()
                                ) ?: ShoppingItem(
                                    name = name,
                                    quantity = quantity.toDoubleOrNull() ?: 1.0,
                                    unit = unit,
                                    price = price.toDoubleOrNull()
                                )
                                onSave(newItem)
                            }
                        }
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
