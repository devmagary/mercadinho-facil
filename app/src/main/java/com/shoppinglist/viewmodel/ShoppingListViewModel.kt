package com.shoppinglist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.data.model.ShoppingItem
import com.shoppinglist.data.model.ShoppingList
import com.shoppinglist.data.repository.AuthRepository
import com.shoppinglist.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel principal para gerenciar a lista de compras atual
 */
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _currentList = MutableStateFlow<ShoppingList?>(null)
    val currentList: StateFlow<ShoppingList?> = _currentList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        observeCurrentList()
    }

    /**
     * Observa a lista atual em tempo real
     */
    private fun observeCurrentList() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                shoppingRepository.observeCurrentList(familyId).collect { list ->
                    _currentList.value = list
                }
            } else {
                _errorMessage.value = "Usuário não está em uma família"
            }
        }
    }

    /**
     * Adiciona um novo item à lista
     */
    fun addItem(item: ShoppingItem) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                // Gerar ID único para o item
                val itemWithId = item.copy(id = System.currentTimeMillis().toString())
                
                val result = shoppingRepository.addItem(familyId, itemWithId)
                result.onSuccess {
                    _successMessage.value = "Item adicionado"
                }
                result.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao adicionar item"
                }
            } else {
                _errorMessage.value = "Família não encontrada"
            }
            _isLoading.value = false
        }
    }

    /**
     * Atualiza um item existente
     */
    fun updateItem(itemId: String, updatedItem: ShoppingItem) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.updateItem(familyId, itemId, updatedItem)
                result.onSuccess {
                    _successMessage.value = "Item atualizado"
                }
                result.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao atualizar item"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Remove um item da lista
     */
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.deleteItem(familyId, itemId)
                result.onSuccess {
                    _successMessage.value = "Item removido"
                }
                result.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao remover item"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Marca/desmarca um item como comprado
     */
    fun toggleItemChecked(itemId: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                shoppingRepository.toggleItemChecked(familyId, itemId)
            }
        }
    }

    /**
     * Finaliza a compra atual
     * @param listName Nome personalizado da lista (opcional)
     * @param totalValue Valor total manual da lista (opcional - se null, usa o valor calculado)
     */
    fun finishShopping(listName: String?, totalValue: Double? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.finishShopping(familyId, listName, totalValue)
                result.onSuccess {
                    _successMessage.value = "Compra finalizada com sucesso!"
                }
                result.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao finalizar compra"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Atualiza o nome da lista atual
     */
    fun updateListName(name: String?) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.updateListName(familyId, name)
                result.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao atualizar nome da lista"
                }
            }
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
