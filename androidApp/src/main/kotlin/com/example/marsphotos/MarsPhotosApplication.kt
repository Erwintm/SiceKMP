package com.example.marsphotos

import android.app.Application
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.data.DefaultAppContainer

class MarsPhotosApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        // 🚧 Saltamos Room temporalmente para enfocarnos en la red y el Login
        // Creamos el contenedor limpio sin pasarle la base de datos
        container = DefaultAppContainer()
    }
}