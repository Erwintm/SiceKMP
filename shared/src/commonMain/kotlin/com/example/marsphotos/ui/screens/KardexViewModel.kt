package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.Kardex
import kotlinx.coroutines.launch

data class KardexUiState(
    val isLoading: Boolean = false,
    val materias: List<Kardex> = emptyList(),
    val error: String? = null
)

class KardexViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var uiState by mutableStateOf(KardexUiState())
        private set

    init {
        viewModelScope.launch {
            repository.obtenerKardexLocal().collect { lista ->
                uiState = uiState.copy(materias = lista)
            }
        }
    }

    fun cargarKardex() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val remotas = repository.fetchKardexRemote()
                if (remotas.isNotEmpty()) {
                    repository.insertarKardexLocal(remotas)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}