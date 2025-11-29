package com.shoppinglist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.data.model.User
import com.shoppinglist.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // Começa carregando para verificar sessão
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = repository.getCurrentUser()
            _currentUser.value = user
            _isLoading.value = false
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.signIn(email, pass)
                if (result.isSuccess) {
                    // Após login, busca dados completos do usuário
                    val user = repository.getCurrentUser()
                    if (user != null) {
                        _currentUser.value = user
                        _authSuccess.value = true
                    } else {
                        _error.value = "Erro ao recuperar dados do usuário."
                    }
                } else {
                    _error.value = "Falha no login: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro desconhecido ao fazer login"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, pass: String, familyName: String?, inviteCode: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 1. Criar conta (SignUp)
                val signUpResult = repository.signUp(email, pass, name)
                
                if (signUpResult.isSuccess) {
                    // 2. Criar ou Entrar em Família
                    val familyResult = if (!inviteCode.isNullOrBlank()) {
                        repository.joinFamily(inviteCode)
                    } else {
                        repository.createFamily(familyName ?: "Família de $name")
                    }

                    if (familyResult.isSuccess) {
                        // 3. Atualizar usuário local com dados finais
                        val user = repository.getCurrentUser()
                        _currentUser.value = user
                        _authSuccess.value = true
                    } else {
                        _error.value = "Conta criada, mas erro na família: ${familyResult.exceptionOrNull()?.message}"
                        // Mesmo com erro na família, o usuário foi criado. 
                        // Poderíamos tentar recuperar ou deixar o usuário sem família (mas o app exige).
                        // Por enquanto, vamos considerar sucesso parcial mas mostrar erro.
                        val user = repository.getCurrentUser()
                        _currentUser.value = user
                    }
                } else {
                    _error.value = "Falha ao criar conta: ${signUpResult.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao registrar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        repository.signOut()
        _currentUser.value = null
        _authSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
    
    fun resetAuthSuccess() {
        _authSuccess.value = false
    }
}
