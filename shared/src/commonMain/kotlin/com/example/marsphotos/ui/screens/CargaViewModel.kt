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
    private val repository: SNRepository // 🔄 Cambiado a la interfaz abstracta
) : ViewModel() {

    var estaSincronizando: Boolean by androidx.compose.runtime.mutableStateOf(false)
        private set

    private val _materias = MutableStateFlow<List<CargaAcademica>>(emptyList())
    val materias: StateFlow<List<CargaAcademica>> = _materias.asStateFlow()

    init {
        // 🔋 Al inicializar el ViewModel, empezamos a observar la BD local automáticamente
        observarCargaLocal()
    }

    /**
     * Escucha los cambios de la base de datos de SQLDelight.
     * Si la BD tiene datos, se pintan en la UI inmediatamente.
     */
    private fun observarCargaLocal() {
        viewModelScope.launch {
            repository.obtenerCarga().collect { listaLocal ->
                println("💾 [Carga Local] Datos leídos desde SQLite de SQLDelight: ${listaLocal.size} materias.")
                _materias.value = listaLocal
            }
        }
    }

    /**
     * Trae los datos desde el servidor de la escuela (Ktor)
     * e internamente actualiza la base de datos local.
     */
    fun sincronizarCarga() {
        viewModelScope.launch {
            estaSincronizando = true
            try {
                println("📡 Intentando traer carga académica desde el servidor remoto...")

                // traerCargaAcademica() descarga los datos y corre la transacción para guardarlos localmente
                val remotas = repository.traerCargaAcademica()

                if (remotas.isEmpty()) {
                    println("⚠️ El servidor remoto devolvió una lista vacía o hubo un problema de parseo.")
                }
            } catch (e: Exception) {
                println("❌ Error al sincronizar Carga Académica: ${e.message}")
                e.printStackTrace()
            } finally {
                estaSincronizando = false
            }
        }
    }
}