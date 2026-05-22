package com.example.marsphotos.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class DatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SNDatabase.Schema,
            context = context,
            name = "sicenet_db.db"
        )
    }
}