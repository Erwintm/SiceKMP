package com.example.marsphotos.model

import kotlinx.serialization.Serializable

@Serializable
data class CalifFinal(
    val id: Int = 0,
    val materia: String = "",
    val grupo: String = "",
    val calificacion: Int = 0,
    val accreditation: String = "",
    var fechaSincronizacion: String = ""
)

@Serializable
data class FinalResponse(val lstCalificacionFinal: List<FinalRaw>)

@Serializable
data class FinalRaw(
    val materia: String?,
    val grupo: String?,
    val calif: Int?,
    val acred: String?
)