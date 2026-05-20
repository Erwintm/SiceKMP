package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.ProfileStudent
import kotlinx.coroutines.launch

class PerfilViewModel(private val snRepository: SNRepository) : ViewModel() {

    var perfilUiState by mutableStateOf<ProfileStudent?>(null)
    var isRefreshing by mutableStateOf(false)

    fun obtenerDatosPerfil(matricula: String) {
        viewModelScope.launch {
            isRefreshing = true
            try {
                perfilUiState = snRepository.profile(matricula)
            } catch (e: Exception) {
                // Aquí manejas los errores de red de forma segura
            } finally {
                isRefreshing = false
            }
        }
    }
}