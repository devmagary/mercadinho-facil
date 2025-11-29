package com.shoppinglist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.data.model.ShoppingList
import com.shoppinglist.data.repository.AuthRepository
import com.shoppinglist.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar o histórico de compras
 */
class HistoryViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val shoppingRepository: ShoppingRepository = ShoppingRepository(authRepository)
) : ViewModel() {

    private val _historyList = MutableStateFlow<List<ShoppingList>>(emptyList())
    val historyList: StateFlow<List<ShoppingList>> = _historyList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * Carrega o histórico de compras
     */
    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.getHistory(familyId)
                result.onSuccess { history ->
                    _historyList.value = history
                }
                result.onFailure { e ->
                    _errorMessage.value = "Erro ao carregar histórico: ${e.message}"
                }
            } else {
                _errorMessage.value = "Família não encontrada"
            }
            _isLoading.value = false
        }
    }

    /**
     * Clona uma lista do histórico para a lista atual
     */
    fun repeatShopping(historyListId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.cloneHistoryList(familyId, historyListId)
                result.onSuccess {
                    _successMessage.value = "Lista clonada para a lista atual!"
                }
                result.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao clonar lista"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Limpa mensagens de erro/sucesso
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
