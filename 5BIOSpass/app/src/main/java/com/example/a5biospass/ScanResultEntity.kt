package com.example.a5biospass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val country: String,
    val status: String, // "Éxito", "Fallo", "Manual"
    val scanTime: Long,
    val manualReason: String? = null
)

