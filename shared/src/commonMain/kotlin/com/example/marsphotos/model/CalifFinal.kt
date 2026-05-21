package com.example.marsphotos.model

import kotlinx.serialization.Serializable

// Tu modelo de UI (el que usas en la pantalla)
@Serializable
data class CalifFinal(
    val id: Int = 0,
    val materia: String = "",
    val grupo: String = "",
    val calificacion: Int = 0,
    val accreditation: String = "",
    var fechaSincronizacion: String = ""
)

// Modelos para recibir el JSON del SICE
@Serializable
data class FinalResponse(val lstCalificacionFinal: List<FinalRaw>)

@Serializable
data class FinalRaw(
    val materia: String? = null,
    val grupo: String? = null,
    val calif: Int? = null,
    val acred: String? = null
)