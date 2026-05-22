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

    // 🚦 Estado reactivo que consumirá tu pantalla de Compose
    var uiState by mutableStateOf(FinalesUiState())
        private set

    init {
        // 1. Al despertar el ViewModel, nos amarramos DE INMEDIATO a la base de datos local
        observarDatosLocales()

        // 2. Intentamos traer datos nuevos del SICE en segundo plano
        cargarFinales()
    }

    /**
     * Se conecta al Flow de SQLDelight. Cualquier cambio en la base de datos
     * se reflejará instantáneamente en la pantalla sin recargar.
     */
    private fun observarDatosLocales() {
        viewModelScope.launch {
            repository.obtenerFinalesLocal().collect { listaLocal ->
                println("💾 [Finales Locales] Datos leídos desde SQLite: ${listaLocal.size} materias.")
                uiState = uiState.copy(
                    listaFinal = listaLocal
                )
            }
        }
    }

    /**
     * Intenta actualizar la base de datos local conectándose al SICE.
     * Si no hay internet, falla silenciosamente manteniendo el estado local intacto.
     */
    fun cargarFinales() {
        viewModelScope.launch {
            // Solo mostramos loading si la lista está vacía (primer inicio)
            if (uiState.listaFinal.isEmpty()) {
                uiState = uiState.copy(isLoading = true)
            }

            try {
                val remotas = repository.fetchCalifFinalesRemote()
                // Si la red respondió, actualizamos todo
                uiState = uiState.copy(listaFinal = remotas, error = null)
            } catch (e: Exception) {
                // 💡 Aquí está el secreto:
                // Si hubo error pero ya teníamos datos en listaFinal, los mantenemos intactos.
                if (uiState.listaFinal.isEmpty()) {
                    uiState = uiState.copy(error = e.message)
                }
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}