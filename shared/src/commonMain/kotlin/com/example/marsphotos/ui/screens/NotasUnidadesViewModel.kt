package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.MateriaUnidades
import kotlinx.coroutines.launch

data class NotasUiState(
    val isLoading: Boolean = false,
    val materias: List<MateriaUnidades> = emptyList(),
    val error: String? = null
)

class NotasUnidadesViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var uiState by mutableStateOf(NotasUiState())
        private set

    // Quitamos la recolección del flujo local del init para que no interfiera
    init { }

    fun cargarNotas() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // 1. Traemos los datos directamente de la API remota
                val remotas = repository.fetchNotasUnidadesRemote()

                // 2. Los asignamos directo al estado de la UI de forma inmediata
                uiState = uiState.copy(materias = remotas, error = null)
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}