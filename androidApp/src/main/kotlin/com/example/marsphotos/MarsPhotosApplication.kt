package com.example.marsphotos

import android.app.Application
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.data.DatabaseDriverFactory
import com.example.marsphotos.data.DefaultAppContainer
import com.example.marsphotos.ui.AppViewModelProvider

class MarsPhotosApplication : Application() {

    // Retorna el contenedor global de dependencias
    val container: AppContainer
        get() = AppViewModelProvider.container

    override fun onCreate() {
        super.onCreate()

        // Inicializamos el driver de SQLite y el contenedor común al arrancar la app
        val driverFactory = DatabaseDriverFactory(applicationContext)
        val androidDriver = driverFactory.createDriver()

        AppViewModelProvider.container = DefaultAppContainer(databaseDriver = androidDriver)
    }
}