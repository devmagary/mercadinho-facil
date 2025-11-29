package com.shoppinglist.ui.navigation

/**
 * Rotas de navegação do aplicativo
 */
sealed class Screen(val route: String) {
    object ShoppingList : Screen("shopping_list")
    object History : Screen("history")
    object Analytics : Screen("analytics")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
}
