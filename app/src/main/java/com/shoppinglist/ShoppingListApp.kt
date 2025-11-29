package com.shoppinglist

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Classe Application do app para inicialização do Firebase
 */
class ShoppingListApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
    }
}
