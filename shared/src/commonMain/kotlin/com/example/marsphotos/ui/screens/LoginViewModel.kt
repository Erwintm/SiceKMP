package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.data.SNRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val snRepository: SNRepository) : ViewModel() {

    var usuario by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun login(onLoginSuccess: (String) -> Unit) {
        if (usuario.isBlank() || password.isBlank()) {
            mensajeError = "Campos obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            val result = snRepository.acceso(usuario, password)

            if (result == "success") {
                onLoginSuccess(usuario)
            } else {
                mensajeError = "Matrícula o NIP incorrectos"
            }
            isLoading = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                LoginViewModel(snRepository = application.container.snRepository)
            }
        }
    }
}