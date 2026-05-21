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
data class UnidadesRaw(
    val Materia: String? = null,
    val Grupo: String? = null,
    val UnidadesActivas: String? = null,
    val C1: String? = null,
    val C2: String? = null,
    val C3: String? = null,
    val C4: String? = null,
    val C5: String? = null,
    val C6: String? = null,
    val C7: String? = null,
    val Observaciones: String? = null
)