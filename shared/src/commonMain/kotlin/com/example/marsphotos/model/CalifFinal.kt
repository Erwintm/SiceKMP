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

// ❌ Quitamos FinalResponse porque el JSON real empieza con [ y no con {

@Serializable
data class FinalRaw(
    val materia: String? = null,
    val grupo: String? = null,
    val calif: Int? = null,
    val acred: String? = null
)