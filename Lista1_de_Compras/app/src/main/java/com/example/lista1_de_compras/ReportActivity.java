package com.example.lista1_de_compras;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lista1_de_compras.model.DBHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ReportActivity: muestra el conteo total y por estado según los filtros.
 * Obtiene los datos de la BD en background y actualiza la UI en el hilo principal.
 */
public class ReportActivity extends AppCompatActivity {
    private TextView tvTotal, tvPending, tvBought;
    private DBHelper dbHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        tvTotal = findViewById(R.id.tvTotal);
        tvPending = findViewById(R.id.tvPending);
        tvBought = findViewById(R.id.tvBought);
        dbHelper = new DBHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        loadReport();
    }

    // Cargar reporte desde la BD en background
    private void loadReport() {
        executorService.execute(() -> {
            DBHelper.Report report = dbHelper.getReport(null, null, null); // Sin filtros
            runOnUiThread(() -> {
                tvTotal.setText("Total: " + report.total);
                tvPending.setText("Pendientes: " + report.pending);
                tvBought.setText("Comprados: " + report.bought);
            });
        });
    }
}

