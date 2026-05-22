package com.example.marsphotos.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // Guarda la base de datos de forma segura en el home del usuario (ej: C:\Users\nombre\sicenet_db.db)
        val databasePath = File(System.getProperty("user.home"), "sicenet_db.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")

        // 🛠️ ¡Crucial para Desktop!: Si el archivo .db es nuevo, crea las tablas desde cero
        if (!databasePath.exists()) {
            SNDatabase.Schema.create(driver)
        }

        return driver
    }
}