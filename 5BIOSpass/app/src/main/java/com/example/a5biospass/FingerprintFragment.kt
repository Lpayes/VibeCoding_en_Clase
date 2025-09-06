package com.example.a5biospass

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FingerprintFragment : Fragment() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var scanStartTime: Long = 0L
    private var lastScanSuccess: Boolean = false
    private var lastScanTime: Long = 0L
    private var lastCountry: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fingerprint, container, false)
        val tvScanStatus = view.findViewById<TextView>(R.id.tvScanStatus)
        val btnStartScan = view.findViewById<Button>(R.id.btnStartScan)
        val btnManualRegister = view.findViewById<Button>(R.id.btnManualRegister)
        val tvNationality = view.findViewById<TextView>(R.id.tvNationality)
        val tvScanTime = view.findViewById<TextView>(R.id.tvScanTime)

        val executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(requireActivity(), executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    lastScanSuccess = true
                    lastScanTime = System.currentTimeMillis() - scanStartTime
                    tvScanStatus.text = "Huella detectada correctamente."
                    tvScanTime.text = "Tiempo: ${lastScanTime} ms"
                    showNationalityDialog(tvNationality, true, tvScanTime)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    lastScanSuccess = false
                    lastScanTime = System.currentTimeMillis() - scanStartTime
                    tvScanStatus.text = "No se pudo detectar la huella."
                    tvScanTime.text = "Tiempo: ${lastScanTime} ms"
                    showNationalityDialog(tvNationality, false, tvScanTime)
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    tvScanStatus.text = "Huella no reconocida. Intenta de nuevo."
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Escaneo de huella")
            .setSubtitle("Coloca tu dedo en el sensor")
            .setNegativeButtonText("Cancelar")
            .build()

        btnStartScan.setOnClickListener {
            tvScanStatus.text = "Escaneando..."
            scanStartTime = System.currentTimeMillis()
            Handler(Looper.getMainLooper()).postDelayed({
                biometricPrompt.authenticate(promptInfo)
            }, 2000) // Simula 2 segundos de escaneo
        }

        btnManualRegister.setOnClickListener {
            showNationalityDialog(tvNationality, true, tvScanTime, manual = true)
        }

        return view
    }

    private fun showNationalityDialog(tvNationality: TextView, success: Boolean = true, tvScanTime: TextView? = null, manual: Boolean = false) {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Registrar nacionalidad")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val country = editText.text.toString().trim()
                val nacionalidadLower = country.lowercase()
                val esExento = nacionalidadLower == "estados unidos" || nacionalidadLower == "estadounidense" || nacionalidadLower == "guatemala" || nacionalidadLower == "guatemalteca"
                if (esExento) {
                    tvNationality.text = "Nacionalidad: $country"
                    if (tvScanTime != null) tvScanTime.text = "Tiempo: 0 ms"
                    Toast.makeText(requireContext(), "La nacionalidad '$country' está exenta del proceso de registro.", Toast.LENGTH_LONG).show()
                    // Guardar registro exento en la base de datos
                    val db = AppDatabase.getInstance(requireContext())
                    lifecycleScope.launch {
                        db.scanResultDao().insert(
                            ScanResultEntity(
                                country = country,
                                status = "Exento",
                                scanTime = 0L,
                                manualReason = null
                            )
                        )
                    }
                    return@setPositiveButton
                }
                tvNationality.text = "Nacionalidad: $country"
                lastCountry = country
                val scanTime = if (manual) 0L else lastScanTime
                val db = AppDatabase.getInstance(requireContext())
                lifecycleScope.launch {
                    db.scanResultDao().insert(
                        ScanResultEntity(
                            country = country,
                            status = if (manual) "Manual" else if (success) "Éxito" else "Fallo",
                            scanTime = scanTime,
                            manualReason = null
                        )
                    )
                }
                if (tvScanTime != null && manual) tvScanTime.text = "Tiempo: 0 ms"
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
