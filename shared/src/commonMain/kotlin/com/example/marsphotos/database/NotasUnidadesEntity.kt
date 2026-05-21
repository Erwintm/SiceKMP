package com.example.marsphotos.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas_unidades")
data class NotasUnidadesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val unidades: String,
    val fechaSincronizacion: String
)