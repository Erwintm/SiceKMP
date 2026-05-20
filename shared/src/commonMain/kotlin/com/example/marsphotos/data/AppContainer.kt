package com.example.marsphotos.data

import com.example.marsphotos.network.SICENETWService
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*

interface AppContainer {
    val siceService: SICENETWService
    val snRepository: SNRepository
}

class DefaultAppContainer : AppContainer {

    // Cliente HTTP unificado con persistencia de cookies en memoria para todas las plataformas
    private val client = HttpClient {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    override val siceService: SICENETWService by lazy {
        SICENETWService(client)
    }

    override val snRepository: SNRepository by lazy {
        // Le pasamos el servicio de red a tu repositorio para que parsee los datos
        NetworkSNRepository(siceService)
    }
}