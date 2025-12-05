package com.shoppinglist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.data.model.ShoppingList
import com.shoppinglist.data.repository.AuthRepository
import com.shoppinglist.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Data class para representar análises por período
 */
data class PeriodAnalytics(
    val name: String?, // Nome da lista (se disponível)
    val date: String,
    val totalValue: Double,
    val itemCount: Int
)

/**
 * ViewModel para análises e estatísticas
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _analytics = MutableStateFlow<List<PeriodAnalytics>>(emptyList())
    val analytics: StateFlow<List<PeriodAnalytics>> = _analytics.asStateFlow()

    private val _totalSpent = MutableStateFlow(0.0)
    val totalSpent: StateFlow<Double> = _totalSpent.asStateFlow()

    private val _averagePerShopping = MutableStateFlow(0.0)
    val averagePerShopping: StateFlow<Double> = _averagePerShopping.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAnalytics()
    }

    /**
     * Carrega as análises do histórico
     */
    fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val familyId = user?.familyId

            if (familyId != null) {
                val result = shoppingRepository.getHistory(familyId)
                result.onSuccess { history ->
                    calculateAnalytics(history)
                }
                result.onFailure {
                    // Em caso de erro, lista vazia
                    calculateAnalytics(emptyList())
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Calcula estatísticas do histórico
     */
    private fun calculateAnalytics(history: List<ShoppingList>) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        // Criar lista de análises por data
        val analyticsList = history.map { list ->
            PeriodAnalytics(
                name = list.name,
                date = list.completedAt?.let { dateFormat.format(it) } ?: "Data desconhecida",
                totalValue = list.totalValue,
                itemCount = list.items.size
            )
        }
        
        _analytics.value = analyticsList
        
        // Calcular total gasto
        val total = history.sumOf { it.totalValue }
        _totalSpent.value = total
        
        // Calcular média por compra
        val average = if (history.isNotEmpty()) {
            total / history.size
        } else {
            0.0
        }
        _averagePerShopping.value = average
    }

    /**
     * Recarrega as análises
     */
    fun refresh() {
        loadAnalytics()
    }
}
