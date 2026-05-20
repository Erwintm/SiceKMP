package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CargaAcademica
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CargaViewModel(
    private val repository: SNRepository
) : ViewModel() {

    // Cambiamos a un estado simple para controlar la carga visual de sincronización
    var estaSincronizando: Boolean by androidx.compose.runtime.mutableStateOf(false)
        private set

    val materias: StateFlow<List<CargaAcademica>> = repository.obtenerCarga()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun sincronizarCarga() {
        viewModelScope.launch {
            estaSincronizando = true
            try {
                // Llama directo a la red a través del repositorio multiplataforma
                val remotas = repository.traerCargaAcademica()
                if (remotas.isNotEmpty()) {
                    repository.insertLocalCarga(remotas)
                }
            } catch (e: Exception) {
                // Manejo de errores silencioso o de red
            } finally {
                estaSincronizando = false
            }
        }
    }
}