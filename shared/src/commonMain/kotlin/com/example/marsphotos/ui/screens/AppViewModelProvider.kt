package com.example.marsphotos.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.MarsPhotosApplication // Ajusta al paquete de tu clase Application
import com.example.marsphotos.ui.screens.CalifFinalViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Inicializador para CalifFinalViewModel
        initializer {
            // Nota: Aquí estamos asumiendo que tu clase Application
            // expone el contenedor de dependencias.
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MarsPhotosApplication)
            val container = application.container
            CalifFinalViewModel(container.snRepository)
        }

        // Aquí podrás agregar fácilmente los otros ViewModels (Kardex, Carga, etc.)
        // initializer { KardexViewModel(container.snRepository) }
    }
}