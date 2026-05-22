package com.example.marsphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.marsphotos.ui.AppViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Recuperamos el contenedor ya inicializado desde la aplicación
        val application = application as MarsPhotosApplication
        val container = application.container
        AppViewModelProvider.container = container

        setContent {
            App() // Tu pantalla raíz compartida de KMP
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}