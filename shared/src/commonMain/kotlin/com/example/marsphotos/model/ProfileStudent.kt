package com.example.marsphotos.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileStudent(
    val matricula: String,
    val nombre: String,
    val carrera: String,
    val promedio: String,
    val semestre: String,
    val creditos: String,
    val fechaReins: String,
    var fechaSincronizacion: String = ""
)