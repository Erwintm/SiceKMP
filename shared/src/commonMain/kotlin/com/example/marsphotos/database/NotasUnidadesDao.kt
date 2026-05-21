package com.example.marsphotos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotasUnidadesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(notas: List<NotasUnidadesEntity>)

    @Query("SELECT * FROM notas_unidades")
    fun obtenerTodasLasNotasAsFlow(): Flow<List<NotasUnidadesEntity>>

    @Query("DELETE FROM notas_unidades")
    suspend fun borrarTodas()
}