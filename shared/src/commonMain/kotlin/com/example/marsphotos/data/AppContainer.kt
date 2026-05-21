package com.example.marsphotos.data

import com.example.marsphotos.database.BaseDeDatosApp
import com.example.marsphotos.network.SICENETWService
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*

interface AppContainer {
    val siceService: SICENETWService
    val snRepository: SNRepository
}

class DefaultAppContainer(
    private val baseDeDatos: BaseDeDatosApp // <- Pasamos la BD para proveer sus DAOs
) : AppContainer {

    // Cliente HTTP unificado con persistencia de cookies en memoria
    private val client = HttpClient {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    override val siceService: SICENETWService by lazy {
        SICENETWService(client)
    }

    override val snRepository: SNRepository by lazy {
        // Le inyectamos el servicio remoto y el DAO local al repositorio
        NetworkSNRepository(
            snApiService = siceService,
            califFinalDao = baseDeDatos.califFinalDao()
        )
    }
}