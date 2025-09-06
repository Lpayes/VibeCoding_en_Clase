package com.example.lista1_de_compras;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lista1_de_compras.model.DBHelper;
import com.example.lista1_de_compras.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MainActivity: muestra la lista de productos, permite agregar, editar, eliminar, filtrar y ver reportes.
 * Todas las operaciones de BD se ejecutan en background y la UI se actualiza en el hilo principal.
 */
public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();
    private DBHelper dbHelper;
    private ExecutorService executorService;
    private View mainView;
    private Product deletedProduct;
    private int deletedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainView = findViewById(R.id.main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);
        dbHelper = new DBHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
        setupSwipeToDelete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    // Cargar productos desde la BD en background
    private void loadProducts() {
        executorService.execute(() -> {
            List<Product> products = dbHelper.getAllProducts();
            runOnUiThread(() -> {
                productList.clear();
                productList.addAll(products);
                adapter.notifyDataSetChanged();
            });
        });
    }

    // Implementación de clic en ítem para editar
    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    // Implementación de swipe-to-delete con Snackbar para deshacer
    @Override
    public void onItemDelete(Product product, int position) {
        deletedProduct = product;
        deletedPosition = position;
        adapter.removeItem(position);
        executorService.execute(() -> dbHelper.deleteProduct(product.getId()));
        Snackbar.make(mainView, "Producto eliminado", Snackbar.LENGTH_LONG)
                .setAction("Deshacer", v -> {
                    adapter.restoreItem(deletedProduct, deletedPosition);
                    executorService.execute(() -> dbHelper.addProduct(deletedProduct));
                }).show();
    }

    // Configuración de swipe-to-delete
    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition(); // Usar método recomendado
                Product product = productList.get(position);
                onItemDelete(product, position);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    // Menú de filtros y reporte
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            // Mostrar diálogo de filtros (implementación en AddEditActivity)
            Intent intent = new Intent(this, AddEditActivity.class);
            intent.putExtra("filter_mode", true);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_report) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
