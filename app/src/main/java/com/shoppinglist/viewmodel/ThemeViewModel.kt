package com.shoppinglist.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Extensão para criar o DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    // Estado do tema. Null significa que ainda não foi carregado ou não definido.
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences ->
                    preferences[DARK_THEME_KEY]
                }
                .collect { isDark ->
                    _isDarkTheme.value = isDark
                }
        }
    }

    fun toggleTheme(currentValue: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[DARK_THEME_KEY] = !currentValue
            }
        }
    }
    
    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[DARK_THEME_KEY] = isDark
            }
        }
    }
}
