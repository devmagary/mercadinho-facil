package com.shoppinglist.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    // Estado do tema. Null significa que ainda não foi carregado ou não definido.
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data
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
            dataStore.edit { preferences ->
                preferences[DARK_THEME_KEY] = !currentValue
            }
        }
    }
    
    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[DARK_THEME_KEY] = isDark
            }
        }
    }
}
