package com.example.a5biospass

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScanResultEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biopass_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
