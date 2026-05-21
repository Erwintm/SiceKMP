package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.NetworkSNRepository
import com.example.marsphotos.model.Kardex
import kotlinx.coroutines.launch

data class KardexUiState(
    val isLoading: Boolean = false,
    val materias: List<Kardex> = emptyList(),
    val error: String? = null
)

class KardexViewModel(
    private val repository: NetworkSNRepository
) : ViewModel() {

    var uiState by mutableStateOf(KardexUiState())
        private set

    // 🔄 Regresamos la función a su estado original sin parámetros
    fun cargarKardex() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                // 📡 Llamada limpia al repositorio KMP sin matrícula
                val remotas = repository.fetchKardexRemote()
                if (remotas.isNotEmpty()) {
                    uiState = uiState.copy(materias = remotas, error = null)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}