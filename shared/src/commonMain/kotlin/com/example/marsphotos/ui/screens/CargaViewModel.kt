package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.NetworkSNRepository
import com.example.marsphotos.model.CargaAcademica
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CargaViewModel(
    private val repository: NetworkSNRepository // Usamos tu repositorio real conectado a Ktor
) : ViewModel() {

    var estaSincronizando: Boolean by androidx.compose.runtime.mutableStateOf(false)
        private set

    private val _materias = MutableStateFlow<List<CargaAcademica>>(emptyList())
    val materias: StateFlow<List<CargaAcademica>> = _materias.asStateFlow()

    fun sincronizarCarga() {
        viewModelScope.launch {
            estaSincronizando = true
            try {
                println("📡 Intentando traer carga académica desde el repositorio...")
                val remotas = repository.traerCargaAcademica()

             

                if (remotas.isNotEmpty()) {
                    _materias.value = remotas

                } else {

                }
            } catch (e: Exception) {

                e.printStackTrace()
            } finally {
                estaSincronizando = false
            }
        }
    }
}