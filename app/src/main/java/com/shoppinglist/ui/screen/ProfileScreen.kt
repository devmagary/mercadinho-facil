package com.shoppinglist.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoppinglist.viewmodel.ProfileViewModel
import com.shoppinglist.viewmodel.ThemeViewModel
import com.shoppinglist.ui.components.GlassTopBar
import com.shoppinglist.ui.components.GlassCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: (String) -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    themeViewModel: ThemeViewModel
) {
    val user by profileViewModel.user.collectAsState()
    val family by profileViewModel.family.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    
    val clipboardManager = LocalClipboardManager.current
    var showCopyMessage by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var deletePassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    // Password confirmation dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                deletePassword = ""
                passwordError = false
            },
            title = { Text("Confirmar Exclusão") },
            text = {
                Column {
                    Text("Por segurança, digite sua senha para confirmar a exclusão da conta:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = deletePassword,
                        onValueChange = { 
                            deletePassword = it
                            passwordError = false
                        },
                        label = { Text("Senha") },
                        singleLine = true,
                        isError = passwordError,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError) {
                        Text(
                            text = "Senha incorreta",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deletePassword.isNotBlank()) {
                            showPasswordDialog = false
                            onDeleteAccount(deletePassword)
                            deletePassword = ""
                        } else {
                            passwordError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmar Exclusão")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPasswordDialog = false
                    deletePassword = ""
                    passwordError = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apagar Conta") },
            text = { Text("Tem certeza que deseja apagar sua conta? Esta ação é irreversível e seus dados serão perdidos.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        showPasswordDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Apagar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            GlassTopBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // User Info Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Usuário",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user?.name ?: "Carregando...",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Family Info Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Família",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = family?.name ?: "Carregando...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Código de Convite",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = family?.inviteCode ?: "...",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = {
                                    family?.inviteCode?.let { code ->
                                        clipboardManager.setText(AnnotatedString(code))
                                        showCopyMessage = true
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Filled.ContentCopy, 
                                    contentDescription = "Copiar Código",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                if (showCopyMessage) {
                    Text(
                        text = "Código copiado!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    LaunchedEffect(Unit) {
                        delay(2000)
                        showCopyMessage = false
                    }
                }

                // Settings Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Configurações",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tema Escuro")
                            Switch(
                                checked = isDarkTheme ?: false, // Default to false if null
                                onCheckedChange = { themeViewModel.toggleTheme(isDarkTheme ?: false) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                OutlinedButton(
                    onClick = {
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair")
                }

                // Delete Account Button
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Apagar Conta")
                }
            }
        }
    }
}
