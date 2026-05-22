package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CargaAcademica
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CargaViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var estaSincronizando: Boolean by androidx.compose.runtime.mutableStateOf(false)
        private set

    private val _materias = MutableStateFlow<List<CargaAcademica>>(emptyList())
    val materias: StateFlow<List<CargaAcademica>> = _materias.asStateFlow()

    init {

        observarCargaLocal()
    }


    private fun observarCargaLocal() {
        viewModelScope.launch {
            repository.obtenerCarga().collect { listaLocal ->
                println("Datos leídos desde SQLite de SQLDelight: ${listaLocal.size} materias.")
                _materias.value = listaLocal
            }
        }
    }


    fun sincronizarCarga() {
        viewModelScope.launch {
            estaSincronizando = true
            try {
                println("Intentando traer carga académica desde el servidor remoto...")


                val remotas = repository.traerCargaAcademica()

                if (remotas.isEmpty()) {
                    println("El servidor remoto devolvió una lista vacía o hubo un problema de parseo.")
                }
            } catch (e: Exception) {
                println("Error al sincronizar Carga Académica: ${e.message}")
                e.printStackTrace()
            } finally {
                estaSincronizando = false
            }
        }
    }
}