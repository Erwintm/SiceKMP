package com.example.marsphotos.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.workers.CargaAcademicaWorker
import com.example.marsphotos.workers.AlmacenarCargaWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CargaViewModel(
    private val repository: SNRepository,
    application: Application
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)


    val materias: StateFlow<List<CargaAcademica>> = repository.obtenerCarga()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    val syncWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkLiveData("sync_carga_unica")

    fun sincronizarCarga() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<CargaAcademicaWorker>()
            .setConstraints(constraints)
            .build()

        val storeRequest = OneTimeWorkRequestBuilder<AlmacenarCargaWorker>()
            .build()


        workManager.beginUniqueWork(
            "sync_carga_unica",
            ExistingWorkPolicy.REPLACE,
            request
        ).then(storeRequest).enqueue()
    }
}