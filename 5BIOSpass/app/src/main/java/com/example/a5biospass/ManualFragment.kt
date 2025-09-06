package com.example.a5biospass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ManualFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manual, container, false)
        val etNationality = view.findViewById<EditText>(R.id.etNationality)
        val etManualReason = view.findViewById<EditText>(R.id.etManualReason)
        val btnRegisterManual = view.findViewById<Button>(R.id.btnRegisterManual)
        val tvManualStatus = view.findViewById<TextView>(R.id.tvManualStatus)

        btnRegisterManual.setOnClickListener {
            val nationality = etNationality.text.toString().trim()
            val motivoManual = etManualReason.text.toString().trim()
            val nacionalidadLower = nationality.lowercase()
            val esExento = nacionalidadLower == "estados unidos" || nacionalidadLower == "estadounidense" || nacionalidadLower == "guatemala" || nacionalidadLower == "guatemalteca"
            if (nationality.isEmpty()) {
                tvManualStatus.text = getString(R.string.enter_nationality)
                return@setOnClickListener
            }
            if (esExento) {
                tvManualStatus.text = "La nacionalidad '$nationality' está exenta del proceso de registro."
                // Guardar registro exento en la base de datos
                val db = AppDatabase.getInstance(requireContext())
                lifecycleScope.launch {
                    db.scanResultDao().insert(
                        ScanResultEntity(
                            country = nationality,
                            status = "Exento",
                            scanTime = 0L,
                            manualReason = motivoManual.ifEmpty { null }
                        )
                    )
                }
                return@setOnClickListener
            }
            val motivoDesc = if (motivoManual.isNotEmpty()) "Motivo: $motivoManual" else "Motivo: No especificado"
            // Guardar el registro manual en la base de datos
            val db = AppDatabase.getInstance(requireContext())
            lifecycleScope.launch {
                db.scanResultDao().insert(
                    ScanResultEntity(
                        country = nationality,
                        status = "Manual",
                        scanTime = 0L,
                        manualReason = motivoManual.ifEmpty { "No especificado" }
                    )
                )
            }
            tvManualStatus.text = "Registro manual exitoso. Nacionalidad: $nationality. $motivoDesc"
        }
        return view
    }
}
