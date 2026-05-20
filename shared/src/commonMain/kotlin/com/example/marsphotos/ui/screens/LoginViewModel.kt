package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            try {
                val result = snRepository.acceso(usuario, password)

                if (result == "success") {
                    onLoginSuccess(usuario)
                } else {
                    mensajeError = "Matrícula o NIP incorrectos"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión con el SICE"
            } finally {
                isLoading = false
            }
        }
    }
}