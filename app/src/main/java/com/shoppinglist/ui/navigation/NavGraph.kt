package com.shoppinglist.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shoppinglist.ui.screen.AnalyticsScreen
import com.shoppinglist.ui.screen.HistoryScreen
import com.shoppinglist.ui.screen.ShoppingListScreen

/**
 * Item da barra de navegação inferior
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * Configuração da navegação do aplicativo
 */
@Composable
fun NavGraph(
    authViewModel: com.shoppinglist.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    
    // Determina o destino inicial com base no estado de autenticação
    // Nota: Em um app real, idealmente usaríamos um Splash Screen enquanto carrega
    val startDestination = if (currentUser != null) Screen.ShoppingList.route else Screen.Login.route
    
    val navItems = listOf(
        BottomNavItem(
            route = Screen.ShoppingList.route,
            icon = Icons.Filled.ShoppingCart,
            label = "Lista"
        ),
        BottomNavItem(
            route = Screen.History.route,
            icon = Icons.Filled.Schedule,
            label = "Histórico"
        ),
        BottomNavItem(
            route = Screen.Analytics.route,
            icon = Icons.Filled.Assessment,
            label = "Análises"
        )
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val currentRoute = currentDestination?.route
            
            // Só mostra a barra de navegação se estiver logado e não estiver nas telas de auth
            if (currentUser != null && 
                currentRoute != Screen.Login.route && 
                currentRoute != Screen.Register.route) {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route, // Sempre inicia configurado, mas o LaunchedEffect abaixo redireciona
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                com.shoppinglist.ui.screen.LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.ShoppingList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    viewModel = authViewModel
                )
            }

            composable(Screen.Register.route) {
                com.shoppinglist.ui.screen.RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.ShoppingList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    viewModel = authViewModel
                )
            }

            composable(Screen.ShoppingList.route) {
                // Se não tiver usuário, força volta pro login (segurança extra)
                if (currentUser == null && !isLoading) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                }
                
                ShoppingListScreen(
                    onNavigateToHistory = {
                        navController.navigate(Screen.History.route)
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route)
                    }
                )
            }
            
            composable(Screen.History.route) {
                HistoryScreen()
            }
            
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
        }
    }
}
