package com.example.marsphotos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.AppViewModelProvider
import com.example.marsphotos.ui.screens.LoginPantalla

@Composable
fun App() {
    MaterialTheme {
        // Estado para controlar en qué pantalla estamos
        var currentScreen by remember { mutableStateOf("LOGIN") }
        var matriculaUsuario by remember { mutableStateOf("") }

        when (currentScreen) {
            "LOGIN" -> {
                // Instanciamos el ViewModel usando nuestra Factory corregida
                val loginViewModel: com.example.marsphotos.ui.screens.LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)

                LoginPantalla(
                    viewModel = loginViewModel,
                    onLoginSuccess = { matricula ->
                        matriculaUsuario = matricula
                        currentScreen = "HOME" // Cambia de pantalla cuando el SICE dé "success"
                    }
                )
            }
            "HOME" -> {
                // Por ahora, un texto simple para saber que entramos.
                // En la Etapa 3 crearemos el menú completo aquí.
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text("¡Lograste entrar! Matrícula: $matriculaUsuario")
                    androidx.compose.material3.Button(onClick = { currentScreen = "LOGIN" }) {
                        androidx.compose.material3.Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}