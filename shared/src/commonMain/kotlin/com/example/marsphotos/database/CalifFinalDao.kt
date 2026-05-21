package com.example.marsphotos.database // Ajusta esto a tu paquete real

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marsphotos.database.CalifFinalEntity

@Dao
interface CalifFinalDao {

    // Inserta una nueva calificación. Si el ID ya existe, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCalificacion(calificacion: CalifFinalEntity)

    // Trae todo el historial
    @Query("SELECT * FROM calificaciones")
    suspend fun obtenerTodasLasCalificaciones(): List<CalifFinalEntity>

    // Filtra por un periodo específico (ej. "Ene-Jun 2026")
    @Query("SELECT * FROM calificaciones WHERE periodo = :periodo")
    suspend fun obtenerCalificacionesPorPeriodo(periodo: String): List<CalifFinalEntity>

    // Limpia la tabla si es necesario
    @Query("DELETE FROM calificaciones")
    suspend fun borrarTodas()
}