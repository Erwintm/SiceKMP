package com.example.marsphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calif_finales")
data class CalifFinal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String = "",
    val grupo: String = "",
    val calificacion: Int = 0,
    val acreditacion: String = "",
    var fechaSincronizacion: String = ""
)

// Para el parseo
data class FinalResponse(val lstCalificacionFinal: List<FinalRaw>)
data class FinalRaw(
    val materia: String?,
    val grupo: String?,
    val calif: Int?,
    val acred: String?
)