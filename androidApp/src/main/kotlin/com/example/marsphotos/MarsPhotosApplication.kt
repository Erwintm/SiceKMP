package com.example.marsphotos

import android.app.Application
import androidx.room.Room
import com.example.marsphotos.data.AppContainer
import com.example.marsphotos.data.DefaultAppContainer
import com.example.marsphotos.database.BaseDeDatosApp

class MarsPhotosApplication : Application() {

    // Aquí vive nuestro contenedor de dependencias
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        // Inicializamos Room con el contexto de la aplicación
        val db = Room.databaseBuilder(
            applicationContext,
            BaseDeDatosApp::class.java,
            "sice_database.db"
        ).build()

        // Creamos el contenedor pasándole la instancia de la BD
        container = DefaultAppContainer(db)
    }
}