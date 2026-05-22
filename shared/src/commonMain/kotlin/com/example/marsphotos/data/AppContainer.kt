package com.example.marsphotos.data

import com.example.marsphotos.network.SICENETWService
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import app.cash.sqldelight.db.SqlDriver // 👈 Importamos el tipo de driver genérico de SQLDelight

interface AppContainer {
    val siceService: SICENETWService
    val snRepository: SNRepository
}

class DefaultAppContainer(
    private val databaseDriver: SqlDriver
) : AppContainer {

    private val client = HttpClient {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    override val siceService: SICENETWService by lazy {
        SICENETWService(client)
    }

    // base de datos de SQLDelight
    private val database: SNDatabase by lazy {
        SNDatabase(driver = databaseDriver)
    }

    //Inyectamos el servicio de red y la base de datos local
    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(
            snApiService = siceService,
            database = database
        )
    }
}