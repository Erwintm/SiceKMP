package com.example.marsphotos.data

import com.example.marsphotos.network.SICENETWService
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*

interface AppContainer {
    val siceService: SICENETWService
    val snRepository: SNRepository
}

class DefaultAppContainer : AppContainer {

    private val client = HttpClient {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    override val siceService: SICENETWService by lazy {
        SICENETWService(client)
    }

    // 🎯 Repositorio limpio: Ya no le inyectamos ningún Dao obsoleto
    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(snApiService = siceService)
    }
}