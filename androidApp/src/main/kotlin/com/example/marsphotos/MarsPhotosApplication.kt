package com.example.marsphotos

import android.app.Application
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.ui.AppViewModelProvider

class MarsPhotosApplication : Application() {

    // Apuntamos directo al contenedor inteligente global
    val container: AppContainer
        get() = AppViewModelProvider.container

    override fun onCreate() {
        super.onCreate()
        // Ya no hace falta inicializar nada aquí, se crea solo bajo demanda
    }
}