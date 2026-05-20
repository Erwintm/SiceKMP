package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsphotos.data.SNRepository

class CalifFinalViewModelFactory(
    private val application: Application,
    private val repository: SNRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalifFinalViewModel::class.java)) {
            return CalifFinalViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}