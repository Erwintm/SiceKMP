package com.example.marsphotos.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.data.DefaultAppContainer
import com.example.marsphotos.ui.screens.LoginViewModel
import com.example.marsphotos.ui.screens.PerfilViewModel
import com.example.marsphotos.ui.screens.CalifFinalViewModel
import com.example.marsphotos.ui.screens.CargaViewModel
import com.example.marsphotos.ui.screens.KardexViewModel
import com.example.marsphotos.ui.screens.NotasUnidadesViewModel

object AppViewModelProvider {

    private var _container: AppContainer? = null

    // 🎯 Propiedad inteligente: Se configura desde la plataforma nativa (Android, iOS, Desktop)
    var container: AppContainer
        get() {
            if (_container == null) {
                // 🚨 Alerta: Lanza una excepción clara si intentas usar la app sin inicializar el Driver
                error("AppContainer no ha sido inicializado. Asegúrate de pasar el container con su respectivo SqlDriver desde el entry point de la plataforma nativa.")
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

        // 👤 Perfil de Alumno
        initializer {
            PerfilViewModel(snRepository = container.snRepository)
        }

        // 🏁 Calificaciones Finales
        initializer {
            CalifFinalViewModel(repository = container.snRepository)
        }

        // 📅 Carga Académica (Limpio: removido el casteo 'as NetworkSNRepository')
        initializer {
            CargaViewModel(repository = container.snRepository)
        }

        // 📜 Kardex (Limpio: removido el casteo 'as NetworkSNRepository')
        initializer {
            KardexViewModel(repository = container.snRepository)
        }

        // 📝 Notas por Unidad
        initializer {
            NotasUnidadesViewModel(repository = container.snRepository)
        }
    }
}