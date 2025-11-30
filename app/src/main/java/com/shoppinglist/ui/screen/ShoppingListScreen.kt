package com.shoppinglist.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoppinglist.data.model.ShoppingItem
import com.shoppinglist.ui.components.AddEditItemDialog
import com.shoppinglist.ui.components.BentoGrid
import com.shoppinglist.ui.components.FinishShoppingDialog
import com.shoppinglist.ui.components.GlassTopBar
import com.shoppinglist.ui.components.ShoppingItemCard
import com.shoppinglist.viewmodel.ShoppingListViewModel

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
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            GlassTopBar(
                title = {
                    OutlinedTextField(
                        value = currentList?.name ?: "",
                        onValueChange = { newName ->
                            viewModel.updateListName(newName.ifBlank { null })
                        },
                        placeholder = { Text("Nome da lista (ex: Churrasco)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val items = currentList?.items ?: emptyList()
            val totalEstimated = currentList?.calculateTotal() ?: 0.0

            // Dashboard (Bento Grid)
            if (items.isNotEmpty()) {
                BentoGrid(
                    items = items,
                    totalEstimated = totalEstimated,
                    onAddItem = { showAddDialog = true },
                    onViewAnalytics = { showFinishDialog = true } // Temporarily mapped to finish dialog as quick action
                )
            }

            // Shopping List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else if (items.isEmpty()) {
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
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        ShoppingItemCard(
                            item = item,
                            onCheckedChange = { _ ->
                                viewModel.toggleItemChecked(item.id)
                            },
                            onEditClick = {
                                itemToEdit = item
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteItem(item.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddEditItemDialog(
            onDismiss = { showAddDialog = false },
            onSave = { item -> viewModel.addItem(item) }
        )
    }

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
