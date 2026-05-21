package com.example.marsphotos.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificaciones")
data class CalifFinalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val materia: String,
    val grupo: String,
    val calificacion: Int, // Cambiado a Int para que coincida con tu modelo
    val accreditation: String
)