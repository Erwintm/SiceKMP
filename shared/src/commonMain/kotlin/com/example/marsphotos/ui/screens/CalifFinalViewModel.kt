package com.example.marsphotos.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CalifFinal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalifFinalViewModel(
    private val repository: SNRepository
) : ViewModel() {

    // El repositorio retorna Flow<List<CalifFinal>> y lo convertimos a StateFlow
    val uiState: StateFlow<List<CalifFinal>> = repository.obtenerFinalesLocal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun fetchAndSave() {
        viewModelScope.launch {
            // 1. Traemos de la red
            val lista = repository.fetchCalifFinalesRemote()
            // 2. Insertamos en local (el repositorio ya debería tener la lógica de insertar)
            repository.insertarFinalesLocal(lista)
        }
    }
}