package com.shoppinglist.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoppinglist.data.model.ShoppingItem
import com.shoppinglist.ui.components.FinishShoppingDialog
import com.shoppinglist.ui.components.ShoppingItemCard
import com.shoppinglist.viewmodel.ShoppingListViewModel

/**
 * Tela principal da lista de compras
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = viewModel(),
    onNavigateToProfile: () -> Unit
) {
    val currentList by viewModel.currentList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ShoppingItem?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    // Mostrar mensagens
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // Aqui você pode mostrar um Snackbar
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            // Aqui você pode mostrar um Snackbar
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Compras") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo para nomear a lista
            var listName by remember { mutableStateOf(currentList?.name ?: "") }
            
            OutlinedTextField(
                value = listName,
                onValueChange = { 
                    listName = it
                    viewModel.updateListName(it.ifBlank { null })
                },
                label = { Text("Nome da Lista") },
                placeholder = { Text("Minha Lista") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
            
            // Atualizar quando a lista mudar
            LaunchedEffect(currentList?.name) {
                listName = currentList?.name ?: ""
            }
            
            // Botão para finalizar compra
            if (currentList?.items?.isNotEmpty() == true) {
                Button(
                    onClick = { showFinishDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finalizar Compra")
                }

                // Total
                val total = currentList?.calculateTotal() ?: 0.0
                if (total > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "Total: R$ %.2f".format(total),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Lista de itens
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (currentList?.items?.isEmpty() != false) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sua lista está vazia\nToque no + para adicionar itens",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = currentList?.items ?: emptyList(),
                        key = { it.id }
                    ) { item ->
                        ShoppingItemCard(
                            item = item,
                            onCheckedChange = {
                                viewModel.toggleItemChecked(item.id)
                            },
                            onEdit = {
                                itemToEdit = item
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.deleteItem(item.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog para adicionar item
    if (showAddDialog) {
        AddEditItemDialog(
            onDismiss = { showAddDialog = false },
            onSave = { item ->
                viewModel.addItem(item)
            }
        )
    }

    // Dialog para editar item
    if (showEditDialog && itemToEdit != null) {
        AddEditItemDialog(
            item = itemToEdit,
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onSave = { updatedItem ->
                viewModel.updateItem(itemToEdit!!.id, updatedItem)
                itemToEdit = null
            }
        )
    }

    // Dialog para finalizar compra com opção de nomear
    if (showFinishDialog) {
        FinishShoppingDialog(
            currentName = currentList?.name,
            onDismiss = { showFinishDialog = false },
            onConfirm = { listName ->
                viewModel.finishShopping(listName)
                showFinishDialog = false
            }
        )
    }
}
