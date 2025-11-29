package com.shoppinglist.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun NavGraph() {
    val navController = rememberNavController()
    
    val navItems = listOf(
        BottomNavItem(
            route = Screen.ShoppingList.route,
            icon = Icons.Filled.ShoppingCart,
            label = "Lista"
        ),
        BottomNavItem(
            route = Screen.History.route,
            icon = Icons.Filled.History,
            label = "Histórico"
        ),
        BottomNavItem(
            route = Screen.Analytics.route,
            icon = Icons.Filled.Analytics,
            label = "Análises"
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ShoppingList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ShoppingList.route) {
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
                HistoryScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
        }
    }
}
