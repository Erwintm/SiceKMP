package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CalifFinal
import com.example.marsphotos.workers.CalFinalesWorker
import com.example.marsphotos.workers.AlmacenarFinalesWorker
import kotlinx.coroutines.launch

data class FinalUiState(
    val isLoading: Boolean = false,
    val listaFinal: List<CalifFinal> = emptyList(),
    val error: String? = null
)

class CalifFinalViewModel(
    application: Application,
    private val repository: SNRepository
) : AndroidViewModel(application) {

    var uiState by mutableStateOf(FinalUiState())
        private set
    private val workManager = WorkManager.getInstance(application)
    val syncWorkInfo = workManager.getWorkInfosForUniqueWorkLiveData("sync_finales")

    init {
        viewModelScope.launch {
            repository.obtenerFinalesLocal().collect { lista ->
                uiState = uiState.copy(listaFinal = lista)
            }
        }
    }

    fun cargarFinales(isOnline: Boolean) {
        if (isOnline) {
            sincronizarConWorkers()
        }
    }

    private fun sincronizarConWorkers() {
        val fetch = OneTimeWorkRequestBuilder<CalFinalesWorker>().build()
        val store = OneTimeWorkRequestBuilder<AlmacenarFinalesWorker>().build()

        workManager.beginUniqueWork(
            "sync_finales",
            ExistingWorkPolicy.REPLACE,
            fetch
        ).then(store).enqueue()
    }
}