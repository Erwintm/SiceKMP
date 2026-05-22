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

        var uiState by mutableStateOf(FinalesUiState())
            private set

        init {

            observarDatosLocales()
            cargarFinales()
        }

        private fun observarDatosLocales() {
            viewModelScope.launch {
                repository.obtenerFinalesLocal().collect { listaLocal ->
                    println("Datos leídos desde SQLite: ${listaLocal.size} materias.")

                    uiState = uiState.copy(
                        listaFinal = listaLocal
                    )
                }
            }
        }

        fun cargarFinales() {
            viewModelScope.launch {
                if (uiState.listaFinal.isEmpty()) {
                    uiState = uiState.copy(isLoading = true)
                }

                try {

                    repository.fetchCalifFinalesRemote()


                    uiState = uiState.copy(error = null)
                } catch (e: Exception) {
                    println(" Falló la red, manteniendo datos locales: ${e.message}")


                    if (uiState.listaFinal.isEmpty()) {
                        uiState = uiState.copy(error = "No hay conexión a internet y no tienes datos guardados.")
                    }
                } finally {
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }
