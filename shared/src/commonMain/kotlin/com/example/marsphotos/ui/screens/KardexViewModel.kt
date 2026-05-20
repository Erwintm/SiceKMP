package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.Kardex

import com.example.marsphotos.workers.AlmacenarKardexWorker
import kotlinx.coroutines.launch
import com.example.marsphotos.data.KardexWorker

data class KardexUiState(
    val isLoading: Boolean = false,
    val materias: List<Kardex> = emptyList(),
    val error: String? = null
)


class KardexViewModel(
    application: Application,
    private val repository: SNRepository
) : AndroidViewModel(application) {
    var uiState by mutableStateOf(KardexUiState())
        private set
    private val workManager = WorkManager.getInstance(application)
    val syncWorkInfo = workManager.getWorkInfosForUniqueWorkLiveData("sync_kardex")

    init {
        viewModelScope.launch {
            repository.obtenerKardexLocal().collect { lista ->
                uiState = uiState.copy(materias = lista)
            }
        }
    }

    fun cargarKardex(isOnline: Boolean) {
        if (isOnline) {
            sincronizarConWorkers()
        }
    }

    private fun sincronizarConWorkers() {
        val fetchKardex = OneTimeWorkRequestBuilder<KardexWorker>().build()
        val storeKardex = OneTimeWorkRequestBuilder<AlmacenarKardexWorker>().build()

        workManager.beginUniqueWork(
            "sync_kardex",
            ExistingWorkPolicy.REPLACE,
            fetchKardex
        ).then(storeKardex).enqueue()
    }
}