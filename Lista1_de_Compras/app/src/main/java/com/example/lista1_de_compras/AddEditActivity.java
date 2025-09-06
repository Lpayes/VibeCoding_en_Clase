package com.example.lista1_de_compras;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lista1_de_compras.model.DBHelper;
import com.example.lista1_de_compras.model.Product;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AddEditActivity: permite agregar o editar productos.
 * Incluye validaciones, DatePickerDialog y operaciones en background.
 */
public class AddEditActivity extends AppCompatActivity {
    private EditText etName, etDate, etQuantity;
    private Spinner spStatus;
    private Button btnSave, btnDelete;
    private DBHelper dbHelper;
    private ExecutorService executorService;
    private int productId = -1;
    private Product loadedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        etName = findViewById(R.id.etName);
        etDate = findViewById(R.id.etDate);
        spStatus = findViewById(R.id.spStatus);
        etQuantity = findViewById(R.id.etQuantity);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        dbHelper = new DBHelper(this);
        executorService = Executors.newSingleThreadExecutor();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{Product.STATUS_PENDING, Product.STATUS_BOUGHT});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        etDate.setOnClickListener(v -> showDatePicker());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product_id")) {
            productId = intent.getIntExtra("product_id", -1);
            loadProduct(productId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveProduct());
        btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    // Mostrar DatePickerDialog
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Cargar producto para edición
    private void loadProduct(int id) {
        executorService.execute(() -> {
            Product product = null;
            for (Product p : dbHelper.getAllProducts()) {
                if (p.getId() == id) product = p;
            }
            loadedProduct = product;
            if (product != null) {
                final Product finalProduct = product;
                runOnUiThread(() -> {
                    etName.setText(finalProduct.getName());
                    etDate.setText(finalProduct.getDate());
                    spStatus.setSelection(finalProduct.getStatus().equals(Product.STATUS_PENDING) ? 0 : 1);
                    etQuantity.setText(String.valueOf(finalProduct.getQuantity()));
                });
            }
        });
    }

    // Guardar producto (agregar o editar)
    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString();
        String quantityStr = etQuantity.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("El nombre no puede estar vacío");
            return;
        }
        if (date.isEmpty()) {
            etDate.setError("La fecha no puede estar vacía");
            return;
        }
        if (!Product.isValidStatus(status)) {
            Toast.makeText(this, "Estado inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            etQuantity.setError("Cantidad inválida");
            return;
        }
        Product product = new Product(name, status, date, quantity);
        executorService.execute(() -> {
            boolean success;
            if (productId == -1) {
                success = dbHelper.addProduct(product) != -1;
            } else {
                product.setId(productId);
                success = dbHelper.updateProduct(product) > 0;
            }
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDeleteDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Seguro que deseas eliminar este producto?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteProduct())
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteProduct() {
        if (productId == -1) return;
        executorService.execute(() -> {
            dbHelper.deleteProduct(productId);
            runOnUiThread(() -> {
                Intent data = new Intent();
                data.putExtra("deleted_product_name", loadedProduct != null ? loadedProduct.getName() : "");
                setResult(RESULT_OK, data);
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
