package com.shoppinglist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog para finalizar compras com opção de nomear a lista
 */
@Composable
fun FinishShoppingDialog(
    currentName: String?,
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    var listName by remember(currentName) { mutableStateOf(currentName ?: "") }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Finalizar Compra")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Enviar null se nome estiver vazio
                    onConfirm(listName.ifBlank { null })
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
