package com.example.marsphotos.model

import kotlinx.serialization.Serializable

@Serializable
data class MateriaUnidades(
    val id: Int = 0,
    val materia: String = "",
    val unidades: String = "",
    var fechaSincronizacion: String = ""
)

@Serializable
data class UnidadesResponse(val lstCalificacionUnidades: List<UnidadesRaw>)

@Serializable
data class UnidadesRaw(
    val Materia: String?,
    val C1: String?,
    val C2: String?,
    val C3: String?,
    val C4: String?,
    val C5: String?,
    val C6: String?,
    val C7: String?
)