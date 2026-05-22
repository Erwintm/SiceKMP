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

        observarKardexLocal()
    }


    private fun observarKardexLocal() {
        viewModelScope.launch {
            repository.obtenerKardexLocal().collect { listaLocal ->
                println(" Datos cargados desde SQLite: ${listaLocal.size} registros.")
                uiState = uiState.copy(materias = listaLocal)
            }
        }
    }


    fun cargarKardex() {
        viewModelScope.launch {

            if (uiState.materias.isEmpty()) {
                uiState = uiState.copy(isLoading = true, error = null)
            }
            try {
                val remotas = repository.fetchKardexRemote()
                if (remotas.isEmpty()) {
                    println("El servidor de la escuela no regresó registros de Kardex.")
                }
            } catch (e: Exception) {
                if (uiState.materias.isEmpty()) {
                    uiState = uiState.copy(error = e.message)
                }
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}