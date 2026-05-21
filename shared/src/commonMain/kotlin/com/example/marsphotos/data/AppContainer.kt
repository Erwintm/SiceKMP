package com.example.marsphotos.data

import com.example.marsphotos.database.CalifFinalDao
import com.example.marsphotos.database.CalifFinalEntity
import com.example.marsphotos.network.SICENETWService
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    // 🎭 ENCAJAMOS LAS FUNCIONES REALES DE TU DAO
    private val mockCalifFinalDao = object : CalifFinalDao {
        override suspend fun guardarCalificacion(calificacion: CalifFinalEntity) {
            // No hace nada por ahora
        }

        override fun obtenerTodasLasCalificacionesAsFlow(): Flow<List<CalifFinalEntity>> = flow {
            emit(emptyList()) // Devuelve una lista vacía simulada
        }

        override suspend fun borrarTodas() {
            // No hace nada por ahora
        }
    }

    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(
            snApiService = siceService,
            califFinalDao = mockCalifFinalDao
        )
    }
}