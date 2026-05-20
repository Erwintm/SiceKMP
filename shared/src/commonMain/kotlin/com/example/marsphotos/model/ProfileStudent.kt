package com.example.marsphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Data class lista para recibir los datos del perfil
@Entity(tableName = "perfil_estudiante")
data class ProfileStudent(
    @PrimaryKey val matricula: String,
    val nombre: String,
    val carrera: String,
    val promedio: String,
    val semestre: String,
    val creditos: String,
    val fechaReins: String,
    var fechaSincronizacion: String = ""
)