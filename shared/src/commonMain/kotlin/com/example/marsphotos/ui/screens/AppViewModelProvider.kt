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


    var container: AppContainer
        get() {
            if (_container == null) {
                error("AppContainer no ha sido inicializado. Asegúrate de pasar el container con su respectivo SqlDriver desde el entry point de la plataforma nativa.")
            }
            return _container!!
        }
        set(value) {
            _container = value
        }

    val Factory = viewModelFactory {

        initializer {
            LoginViewModel(snRepository = container.snRepository)
        }


        initializer {
            PerfilViewModel(snRepository = container.snRepository)
        }


        initializer {
            CalifFinalViewModel(repository = container.snRepository)
        }


        initializer {
            CargaViewModel(repository = container.snRepository)
        }


        initializer {
            KardexViewModel(repository = container.snRepository)
        }


        initializer {
            NotasUnidadesViewModel(repository = container.snRepository)
        }
    }
}