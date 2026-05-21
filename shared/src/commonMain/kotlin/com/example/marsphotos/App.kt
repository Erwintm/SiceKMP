package com.example.marsphotos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.AppViewModelProvider
import com.example.marsphotos.ui.screens.LoginPantalla
import com.example.marsphotos.ui.screens.PerfilPantalla
import com.example.marsphotos.ui.screens.PerfilViewModel
// import com.example.marsphotos.ui.screens.MenuScreen // 👈 Descomenta esto cuando migremos tu Menú

@Composable
fun App() {
    MaterialTheme {
        // 🚦 Control de navegación nativo de KMP basado en estados
        var currentScreen by remember { mutableStateOf("LOGIN") }
        var matriculaUsuario by remember { mutableStateOf("") }

        when (currentScreen) {
            "LOGIN" -> {
                val loginViewModel: com.example.marsphotos.ui.screens.LoginViewModel =
                    viewModel(factory = AppViewModelProvider.Factory)

                LoginPantalla(
                    viewModel = loginViewModel,
                    onLoginSuccess = { matricula ->
                        matriculaUsuario = matricula
                        currentScreen = "PERFIL" // ➡️ Brinca directo al Perfil al loguearse
                    }
                )
            }

            "PERFIL" -> {
                // Instanciamos el PerfilViewModel usando el mismo Provider centralizado
                val perfilViewModel: PerfilViewModel =
                    viewModel(factory = AppViewModelProvider.Factory)

                PerfilPantalla(
                    matricula = matriculaUsuario,
                    viewModel = perfilViewModel,
                    onNavigateToMenu = {
                        currentScreen = "MENU" // ➡️ Al dar "Ir al menú", avanza al Portal Integral
                    }
                )
            }

            "MENU" -> {
                // 🚪 Menú provisional corregido
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally // Corregido aquí
                ) {
                    androidx.compose.material3.Text(
                        text = "Portal Integral (Menú)",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                    androidx.compose.material3.Button(onClick = { currentScreen = "LOGIN" }) {
                        androidx.compose.material3.Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}