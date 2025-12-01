package com.shoppinglist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog para finalizar compras com opção de nomear a lista e definir valor total
 */
@Composable
fun FinishShoppingDialog(
    currentName: String?,
    calculatedTotal: Double,
    onDismiss: () -> Unit,
    onConfirm: (name: String?, totalValue: Double?) -> Unit
) {
    var listName by remember(currentName) { mutableStateOf(currentName ?: "") }
    var totalValueText by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Finalizar Compra")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo de nome
                Text(
                    "Dê um nome para esta lista (opcional):",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("Nome da lista") },
                    placeholder = { Text("Ex: Compra do Mês") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (listName.isBlank()) {
                            TextButton(
                                onClick = {
                                    listName = dateFormat.format(Date())
                                },
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text("Usar Data", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                )
                if (listName.isBlank()) {
                    Text(
                        "Se deixar em branco, será salvo sem nome (data será usada para exibição).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                
                // Campo de valor total
                Text(
                    "Valor total da compra (opcional):",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = totalValueText,
                    onValueChange = { newValue ->
                        // Permitir apenas números e vírgula/ponto
                        val filtered = newValue.filter { it.isDigit() || it == ',' || it == '.' }
                        totalValueText = filtered
                    },
                    label = { Text("Valor total") },
                    placeholder = { Text("Ex: 150,00") },
                    prefix = { Text("R$ ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                // Mostrar valor calculado automaticamente como referência
                if (calculatedTotal > 0) {
                    Text(
                        "Valor calculado dos itens: R$ %.2f".format(calculatedTotal),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    "Se deixar em branco, será usado o valor calculado dos itens (se houver).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Converter valor de texto para Double
                    val totalValue = if (totalValueText.isNotBlank()) {
                        totalValueText.replace(",", ".").toDoubleOrNull()
                    } else null
                    
                    // Enviar null se nome estiver vazio
                    onConfirm(listName.ifBlank { null }, totalValue)
                }
            ) {
                Text("Finalizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
