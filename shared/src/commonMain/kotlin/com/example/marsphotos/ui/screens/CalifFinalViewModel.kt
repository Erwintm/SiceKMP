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

    // El repositorio retorna Flow, convertimos a StateFlow para que la UI se pinte sola
    val uiState: StateFlow<List<CalifFinal>> = repository.obtenerFinalesLocal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun fetchAndSave() {
        viewModelScope.launch {
            // El repositorio ya contiene la lógica de llamar al servicio,
            // convertir el JSON, mapear a CalifFinal e insertar en Room.
            repository.fetchCalifFinalesRemote()
        }
    }
}