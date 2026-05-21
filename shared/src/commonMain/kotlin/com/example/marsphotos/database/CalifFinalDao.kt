package com.example.marsphotos.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalifFinalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCalificacion(calificacion: CalifFinalEntity)

    @Query("SELECT * FROM calificaciones")
    fun obtenerTodasLasCalificacionesAsFlow(): Flow<List<CalifFinalEntity>>

    @Query("DELETE FROM calificaciones")
    suspend fun borrarTodas()
}