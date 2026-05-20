package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.MateriaUnidades
import com.example.marsphotos.workers.NotasWorker
import com.example.marsphotos.workers.AlmacenarNotasWorker
import kotlinx.coroutines.launch

data class NotasUiState(
    val isLoading: Boolean = false,
    val materias: List<MateriaUnidades> = emptyList(),
    val error: String? = null
)

class NotasUnidadesViewModel(
    application: Application,
    private val repository: SNRepository
) : AndroidViewModel(application) {

    var uiState by mutableStateOf(NotasUiState())
        private set

    private val workManager = WorkManager.getInstance(application)

    val syncWorkInfo = workManager.getWorkInfosForUniqueWorkLiveData("sync_notas")

    init {
        viewModelScope.launch {
            repository.obtenerNotasLocal().collect { lista ->
                uiState = uiState.copy(materias = lista)
            }
        }
    }

    fun cargarNotas(isOnline: Boolean) {
        if (isOnline) {
            sincronizarConWorkers()
        }
    }

    private fun sincronizarConWorkers() {
        val fetch = OneTimeWorkRequestBuilder<NotasWorker>().build()
        val store = OneTimeWorkRequestBuilder<AlmacenarNotasWorker>().build()

        workManager.beginUniqueWork(
            "sync_notas",
            ExistingWorkPolicy.REPLACE,
            fetch
        ).then(store).enqueue()
    }
}