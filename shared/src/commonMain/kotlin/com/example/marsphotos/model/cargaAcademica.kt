package com.example.marsphotos.model

import kotlinx.serialization.Serializable

@Serializable
data class CargaAcademica(
    val id: Long = 0,
    val Semipresencial: String = "",
    val Observaciones: String = "",
    val Docente: String = "",
    val clvOficial: String = "",
    val Sabado: String = "",
    val Viernes: String = "",
    val Jueves: String = "",
    val Miercoles: String = "",
    val Martes: String = "",
    val Lunes: String = "",
    val EstadoMateria: String = "",
    val CreditosMateria: Int = 0,
    val Materia: String = "",
    val Grupo: String = "",
    var fechaSincronizacion: String = ""
)