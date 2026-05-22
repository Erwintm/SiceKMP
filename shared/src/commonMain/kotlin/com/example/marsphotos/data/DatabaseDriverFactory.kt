package com.example.marsphotos.data

import app.cash.sqldelight.db.SqlDriver


// 1. Le decimos a Kotlin que cada plataforma debe implementar su propio Driver
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// 2. Función utilitaria global para inicializar la base de datos limpia en tus repositorios
fun createDatabase(driverFactory: DatabaseDriverFactory): SNDatabase {
    val driver = driverFactory.createDriver()
    return SNDatabase(driver)
}