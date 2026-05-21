package com.example.marsphotos.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.data.NetworkSNRepository
import com.example.marsphotos.ui.screens.LoginViewModel
import com.example.marsphotos.ui.screens.PerfilViewModel // 👈 Asegúrate de importar el del perfil
import com.example.marsphotos.ui.screens.CalifFinalViewModel
import com.example.marsphotos.ui.screens.CargaViewModel

object AppViewModelProvider {

    // Una variable global temporal para guardar nuestro contenedor en KMP
    // Se inicializará cuando la app arranque
    lateinit var container: AppContainer

    val Factory = viewModelFactory {
        // 🔐 Inicializador para tu Login
        initializer {
            LoginViewModel(snRepository = container.snRepository)
        }

        // 👤 ¡Agrega este bloque para el Perfil de Alumno!
        initializer {
            PerfilViewModel(snRepository = container.snRepository)
        }

        // 🏁 Inicializador para Calificaciones Finales
        initializer {
            CalifFinalViewModel(container.snRepository)
        }

        initializer {
            // Cambiamos 'snRepository =' por 'repository ='
            CargaViewModel(repository = container.snRepository as NetworkSNRepository)
        }
    }
}