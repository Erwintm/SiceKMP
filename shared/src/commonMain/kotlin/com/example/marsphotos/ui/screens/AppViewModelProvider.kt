package com.example.marsphotos.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.ui.screens.LoginViewModel
import com.example.marsphotos.ui.screens.CalifFinalViewModel

object AppViewModelProvider {

    // Una variable global temporal para guardar nuestro contenedor en KMP
    // Se inicializará cuando la app arranque
    lateinit var container: AppContainer

    val Factory = viewModelFactory {
        // 🔐 Inicializador para tu Login
        initializer {
            LoginViewModel(snRepository = container.snRepository)
        }

        // 🏁 Inicializador para Calificaciones Finales (lo usaremos después)
        initializer {
            CalifFinalViewModel(container.snRepository)
        }
    }
}