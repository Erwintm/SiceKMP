package com.example.marsphotos

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.marsphotos.data.DefaultAppContainer
import com.example.marsphotos.data.SNDatabase
import com.example.marsphotos.ui.AppViewModelProvider
import java.io.File

fun main() {
    // 1. Definimos la ruta del archivo de la base de datos en la PC del usuario
    val databaseFile = File(System.getProperty("user.home"), "sicenet2.db")

    // 2. Creamos el Driver de JDBC para SQLite en Desktop
    val desktopDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

    // 3. CRÍTICO: SQLDelight en Desktop no crea las tablas automáticamente.
    // Si el archivo no existe, obligamos a la base de datos a crear su esquema estructural.
    if (!databaseFile.exists()) {
        SNDatabase.Schema.create(desktopDriver)
    }

    // 4. Inicializamos el contenedor global compartiendo el Driver de la PC
    AppViewModelProvider.container = DefaultAppContainer(databaseDriver = desktopDriver)

    // 5. Arrancamos la aplicación de Compose Desktop de forma normal
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SiceKMP",
        ) {
            App() // Tu vista principal común (shared)
        }
    }
}