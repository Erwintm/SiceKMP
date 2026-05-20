package com.example.marsphotos.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.ui.screens.KardexViewModel

class KardexViewModelFactory(
    private val application: Application,
    private val repository: SNRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KardexViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KardexViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}