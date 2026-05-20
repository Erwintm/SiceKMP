package com.example.marsphotos.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.data.SNRepository

class CargaViewModelFactory(
    private val repository: SNRepository,
    private val app: MarsPhotosApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CargaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            return CargaViewModel(repository, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}