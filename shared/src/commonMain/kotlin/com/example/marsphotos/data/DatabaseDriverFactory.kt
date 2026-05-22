package com.example.marsphotos.data

import app.cash.sqldelight.db.SqlDriver


fun createDatabase(driver: SqlDriver): SNDatabase {
    return SNDatabase(driver)
}