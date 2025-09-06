package com.example.a5biospass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a5biospass.ScanResultsStore
import com.example.a5biospass.ScanResult
import kotlinx.coroutines.launch

class ControlFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_control, container, false)
        val etFilterCountry = view.findViewById<EditText>(R.id.etFilterCountry)
        val btnFilter = view.findViewById<View>(R.id.btnFilter)
        val tvMetrics = view.findViewById<TextView>(R.id.tvMetrics)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerTourists)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fun updateMetricsAndList(country: String) {
            val db = AppDatabase.getInstance(requireContext())
            lifecycleScope.launch {
                val filtered = if (country.isEmpty()) db.scanResultDao().getAll() else db.scanResultDao().getByCountry(country)
                val total = filtered.size
                val exitosos = filtered.count { it.status == "Éxito" }
                val fallidos = filtered.count { it.status == "Fallo" }
                val errores = fallidos // Cada fallo cuenta como error
                val manuales = filtered.count { it.status == "Manual" }
                val promedioTiempo = if (filtered.isNotEmpty()) filtered.map { it.scanTime }.average().toLong() else 0L
                val porcentajeExito = if (total > 0) (exitosos * 100 / total) else 0
                val paisExento = country.equals("Estados Unidos", true) || country.equals("Guatemala", true)
                if (paisExento && country.isNotEmpty()) {
                    tvMetrics.text = "El país '$country' está exento del proceso de control."
                    recyclerView.adapter = ScanResultAdapter(emptyList())
                    return@launch
                }
                tvMetrics.text = "Historial de escaneos para '$country':\nTotal: $total\nExitosos: $exitosos\nFallidos: $fallidos\nErrores: $errores\nProcesos manuales: $manuales\nPromedio tiempo: $promedioTiempo ms\n% Éxito: $porcentajeExito%"
                recyclerView.adapter = ScanResultAdapter(filtered)
            }
        }

        btnFilter.setOnClickListener {
            val country = etFilterCountry.text.toString().trim()
            updateMetricsAndList(country)
        }

        // Carga inicial (todos)
        updateMetricsAndList("")
        return view
    }
}

class ScanResultAdapter(private val results: List<ScanResultEntity>) : RecyclerView.Adapter<ScanResultAdapter.ScanResultViewHolder>() {
    class ScanResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInfo: TextView = view.findViewById(android.R.id.text1)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ScanResultViewHolder(view)
    }
    override fun onBindViewHolder(holder: ScanResultViewHolder, position: Int) {
        val r = results[position]
        val isManual = r.status == "Manual"
        holder.tvInfo.text = if (isManual) {
            "${r.country} | Manual | ${r.scanTime} ms"
        } else {
            "${r.country} | ${r.status} | ${r.scanTime} ms"
        }
        holder.itemView.setOnClickListener {
            if (isManual) {
                android.app.AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Motivo del registro manual")
                    .setMessage(r.manualReason)
                    .setPositiveButton("Cerrar", null)
                    .show()
            }
        }
    }
    override fun getItemCount(): Int = results.size
}
