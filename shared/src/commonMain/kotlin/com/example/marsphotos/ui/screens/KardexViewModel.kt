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
    private val repository: SNRepository // 🔄 Ahora acepta la interfaz genérica
) : ViewModel() {

    var uiState by mutableStateOf(KardexUiState())
        private set

    init {
        // 🔋 Empezamos a escuchar la base de datos local desde que nace el ViewModel
        observarKardexLocal()
    }

    /**
     * Escucha activamente SQLite a través de SQLDelight.
     * Cualquier inserción o borrado actualizará la UI automáticamente.
     */
    private fun observarKardexLocal() {
        viewModelScope.launch {
            repository.obtenerKardexLocal().collect { listaLocal ->
                println("💾 [Kardex Local] Datos cargados desde SQLite: ${listaLocal.size} registros.")
                uiState = uiState.copy(materias = listaLocal)
            }
        }
    }

    /**
     * Lanza la petición remota a través de Ktor para refrescar los datos.
     * Internamente guarda los cambios en la base de datos local.
     */
    fun cargarKardex() {
        viewModelScope.launch {
            // Solo activamos el loading si la lista local está vacía para no parpadear la UI de golpe
            if (uiState.materias.isEmpty()) {
                uiState = uiState.copy(isLoading = true, error = null)
            }
            try {
                // 📡 Trae los datos de internet y limpia/reinserta la tabla en SQLite
                val remotas = repository.fetchKardexRemote()
                if (remotas.isEmpty()) {
                    println("⚠️ El servidor de la escuela no regresó registros de Kardex.")
                }
            } catch (e: Exception) {
                // Solo pintamos error si de plano no tenemos nada que mostrar en pantalla
                if (uiState.materias.isEmpty()) {
                    uiState = uiState.copy(error = e.message)
                }
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}