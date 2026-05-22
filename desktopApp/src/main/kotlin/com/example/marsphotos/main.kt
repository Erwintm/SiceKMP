package com.example.marsphotos

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.marsphotos.data.DefaultAppContainer
import com.example.marsphotos.data.SNDatabase
import com.example.marsphotos.ui.AppViewModelProvider
import java.io.File

fun main() {
    val databaseFile = File(System.getProperty("user.home"), "sicenet2.db")
    val desktopDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

    if (!databaseFile.exists()) {
        SNDatabase.Schema.create(desktopDriver)
    }

    AppViewModelProvider.container = DefaultAppContainer(databaseDriver = desktopDriver)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SiceKMP",
        ) {
            App()
        }
    }
}