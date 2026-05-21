package com.example.marsphotos.database // Asegúrate de que coincida con tu paquete real

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta anotación le dice a Room/KSP que cree una tabla llamada "calificaciones"
@Entity(tableName = "calificaciones")
data class CalifFinalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val materia: String,
    val calificacion: Double,
    val periodo: String
)