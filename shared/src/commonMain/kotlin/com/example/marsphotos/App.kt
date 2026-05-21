package com.example.marsphotos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.AppViewModelProvider
import com.example.marsphotos.ui.screens.CargaAcademicaScreen
import com.example.marsphotos.ui.screens.KardexScreen
import com.example.marsphotos.ui.screens.LoginPantalla
import com.example.marsphotos.ui.screens.MenuScreen
import com.example.marsphotos.ui.screens.PerfilPantalla
import com.example.marsphotos.ui.screens.PerfilViewModel

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
                val perfilViewModel: PerfilViewModel =
                    viewModel(factory = AppViewModelProvider.Factory)

                PerfilPantalla(
                    matricula = matriculaUsuario,
                    viewModel = perfilViewModel,
                    onNavigateToMenu = {
                        currentScreen = "MENU" // ➡️ Al dar "Ir al menú", avanza al mosaico
                    }
                )
            }

            "MENU" -> {
                // 🚪 Invocamos tu menú en cuadrícula real
                MenuScreen(
                    onNavigate = { rutaDestino ->
                        when (rutaDestino) {
                            "carga"   -> currentScreen = "CARGA"
                            "kardex"  -> currentScreen = "KARDEX"
                            "notas"   -> currentScreen = "NOTAS"
                            "finales" -> currentScreen = "FINALES"
                            else      -> currentScreen = "MENU"
                        }
                    }
                )
            }

            // 📚 Estados provisionales para el resto de tus pantallas del SICE
            "CARGA" -> {
                val cargaViewModel: com.example.marsphotos.ui.screens.CargaViewModel =
                    viewModel(factory = AppViewModelProvider.Factory)

                CargaAcademicaScreen(
                    viewModel = cargaViewModel,
                    onVolver = { currentScreen = "MENU" }
                )
            }
            "KARDEX" -> {
                val kardexViewModel: com.example.marsphotos.ui.screens.KardexViewModel =
                    viewModel(factory = AppViewModelProvider.Factory)

                KardexScreen(
                    viewModel = kardexViewModel,
                    onVolver = { currentScreen = "MENU" }
                )
            }
            "NOTAS" -> {
                PantallaProvisional(titulo = "Calificaciones por Unidad") { currentScreen = "MENU" }
            }
            "FINALES" -> {
                PantallaProvisional(titulo = "Calificaciones Finales") { currentScreen = "MENU" }
            }
        }
    }
}

/**
 * Contenedor genérico temporal para simular las pantallas restantes
 */
@Composable
fun PantallaProvisional(titulo: String, onVolver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVolver) {
            Text("Volver al Menú Integral")
        }
    }
}