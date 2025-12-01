package com.shoppinglist

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application do app para inicialização do Firebase
 */
@HiltAndroidApp
class ShoppingListApp : Application()

