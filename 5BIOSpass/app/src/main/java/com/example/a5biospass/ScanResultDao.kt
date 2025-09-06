package com.example.a5biospass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScanResultDao {
    @Insert
    suspend fun insert(result: ScanResultEntity)

    @Query("SELECT * FROM scan_results ORDER BY id DESC")
    suspend fun getAll(): List<ScanResultEntity>

    @Query("SELECT * FROM scan_results WHERE country = :country ORDER BY id DESC")
    suspend fun getByCountry(country: String): List<ScanResultEntity>
}

