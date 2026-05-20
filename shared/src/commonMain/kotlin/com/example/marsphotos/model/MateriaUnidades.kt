package com.example.marsphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas_unidades")
data class MateriaUnidades(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String = "",
    val unidades: String = "",
    var fechaSincronizacion: String = ""
)


data class UnidadesResponse(val lstCalificacionUnidades: List<UnidadesRaw>)
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