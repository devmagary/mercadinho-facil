package com.shoppinglist.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shoppinglist.data.model.MeasureUnit
import com.shoppinglist.data.model.ShoppingItem

/**
 * Dialog para adicionar ou editar um item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    item: ShoppingItem? = null,
    onDismiss: () -> Unit,
    onSave: (ShoppingItem) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "1") }
    var selectedUnit by remember { mutableStateOf(item?.unit ?: MeasureUnit.UN) }
    var price by remember { mutableStateOf(item?.price?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(item?.imageUrl ?: "") }
    var expanded by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (item == null) "Adicionar Item" else "Editar Item",
                    style = MaterialTheme.typography.titleLarge
                )

                // Nome
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = { Text("Nome do Item") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Nome não pode ser vazio") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quantidade
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = {
                            quantity = it
                            quantityError = it.toDoubleOrNull() == null || it.toDoubleOrNull()!! <= 0
                        },
                        label = { Text("Quantidade") },
                        isError = quantityError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Unidade de Medida (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedUnit.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidade") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            MeasureUnit.getAll().forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.displayName) },
                                    onClick = {
                                        selectedUnit = unit
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Preço (opcional)
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Preço (opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("R$ ") }
                )

                // URL da Imagem (opcional)
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL da Imagem (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Botões
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                                return@Button
                            }
                            
                            val quantityValue = quantity.toDoubleOrNull()
                            if (quantityValue == null || quantityValue <= 0) {
                                quantityError = true
                                return@Button
                            }

                            val newItem = ShoppingItem(
                                id = item?.id ?: "",
                                name = name,
                                quantity = quantityValue,
                                unit = selectedUnit,
                                price = price.toDoubleOrNull(),
                                imageUrl = imageUrl.ifBlank { null },
                                isChecked = item?.isChecked ?: false
                            )
                            
                            onSave(newItem)
                            onDismiss()
                        }
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
