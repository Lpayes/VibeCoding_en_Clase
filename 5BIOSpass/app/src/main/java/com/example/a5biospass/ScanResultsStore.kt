package com.example.a5biospass

// Resultado de escaneo
data class ScanResult(
    val country: String,
    val success: Boolean,
    val scanTime: Long,
    val manualReason: String? = null // Motivo del proceso manual, si aplica
)

object ScanResultsStore {
    val results = mutableListOf<ScanResult>()
}
