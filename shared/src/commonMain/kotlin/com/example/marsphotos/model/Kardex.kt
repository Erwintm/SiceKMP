package com.example.marsphotos.model

import kotlinx.serialization.Serializable

@Serializable
data class Kardex(
    val id: Int = 0,
    val clvMateria: String = "",
    val materia: String = "",
    val calificacion: Int = 0,
    val acreditacion: String = "",
    val periodo: String = "",
    var fechaSincronizacion: String = ""
)