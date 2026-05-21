package com.example.marsphotos.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.data.DefaultAppContainer
import com.example.marsphotos.data.NetworkSNRepository
import com.example.marsphotos.ui.screens.LoginViewModel
import com.example.marsphotos.ui.screens.PerfilViewModel
import com.example.marsphotos.ui.screens.CalifFinalViewModel
import com.example.marsphotos.ui.screens.CargaViewModel
import com.example.marsphotos.ui.screens.KardexViewModel
import com.example.marsphotos.ui.screens.NotasUnidadesViewModel

object AppViewModelProvider {

    private var _container: AppContainer? = null

    // 🎯 Propiedad inteligente: Si Android no la define, Desktop/Web la crean solas
    var container: AppContainer
        get() {
            if (_container == null) {
                _container = DefaultAppContainer()
            }
            return _container!!
        }
        set(value) {
            _container = value
        }

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
            CargaViewModel(repository = container.snRepository as NetworkSNRepository)
        }

        initializer {
            KardexViewModel(repository = container.snRepository as NetworkSNRepository)
        }

        initializer {
            NotasUnidadesViewModel(repository = container.snRepository)
        }
    }
}