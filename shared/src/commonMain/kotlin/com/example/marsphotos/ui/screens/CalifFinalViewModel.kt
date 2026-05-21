package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CalifFinal
import kotlinx.coroutines.launch

data class FinalesUiState(
    val isLoading: Boolean = false,
    val listaFinal: List<CalifFinal> = emptyList(),
    val error: String? = null
)

class CalifFinalViewModel(
    private val repository: SNRepository
) : ViewModel() {

    // 🚦 Estado reactivo directo en memoria
    var uiState by mutableStateOf(FinalesUiState())
        private set

    fun cargarFinales() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // Llamamos directo a la API remota del SICE
                val remotas = repository.fetchCalifFinalesRemote()
                uiState = uiState.copy(listaFinal = remotas, error = null)
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}