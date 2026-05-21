package com.example.marsphotos.database // Ajusta esto a tu paquete real

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.marsphotos.database.CalifFinalDao
import com.example.marsphotos.database.CalifFinalEntity

// Si agregas más tablas en el futuro, las pones en el arreglo 'entities'
@Database(entities = [CalifFinalEntity::class], version = 1, exportSchema = false)
abstract class BaseDeDatosApp : RoomDatabase() {

    abstract fun califFinalDao(): CalifFinalDao

}