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

    init {
        // 1. Iniciar la observación de la base de datos local desde el inicio
        observarNotasLocales()

        // 2. Intentar actualizar desde el servidor en segundo plano
        cargarNotas()
    }

    private fun observarNotasLocales() {
        viewModelScope.launch {
            repository.obtenerNotasLocal().collect { notasLocales ->
                uiState = uiState.copy(materias = notasLocales)
            }
        }
    }

    fun cargarNotas() {
        viewModelScope.launch {
            // Solo mostramos loading si no tenemos nada que mostrar (primer acceso)
            if (uiState.materias.isEmpty()) {
                uiState = uiState.copy(isLoading = true)
            }

            try {
                repository.fetchNotasUnidadesRemote()
                uiState = uiState.copy(error = null)
            } catch (e: Exception) {
                // Si ya tenemos datos, no sobrescribimos con el error
                if (uiState.materias.isEmpty()) {
                    uiState = uiState.copy(error = "No se pudieron cargar las notas: ${e.message}")
                }
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}