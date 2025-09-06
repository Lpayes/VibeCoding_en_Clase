package com.example.lista1_de_compras.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * DBHelper gestiona la base de datos local SQLite para productos.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopping_list.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_QUANTITY = "quantity";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_STATUS + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public long addProduct(Product product) {
        long result = -1;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, product.getName());
            values.put(COLUMN_STATUS, product.getStatus());
            values.put(COLUMN_DATE, product.getDate());
            values.put(COLUMN_QUANTITY, product.getQuantity());
            result = db.insert(TABLE_PRODUCTS, null, values);
        } catch (Exception e) {
            Log.e("DBHelper", "Error al agregar producto", e);
        }
        return result;
    }

    public int updateProduct(Product product) {
        int rows = 0;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, product.getName());
            values.put(COLUMN_STATUS, product.getStatus());
            values.put(COLUMN_DATE, product.getDate());
            values.put(COLUMN_QUANTITY, product.getQuantity());
            rows = db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=?", new String[]{String.valueOf(product.getId())});
        } catch (Exception e) {
            Log.e("DBHelper", "Error al editar producto", e);
        }
        return rows;
    }

    public int deleteProduct(int id) {
        int rows = 0;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            rows = db.delete(TABLE_PRODUCTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("DBHelper", "Error al eliminar producto", e);
        }
        return rows;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, COLUMN_DATE + " DESC")) {
            while (cursor.moveToNext()) {
                products.add(cursorToProduct(cursor));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error al obtener productos", e);
        }
        return products;
    }

    public List<Product> getProductsFiltered(String status, String startDate, String endDate, Integer minQty, Integer maxQty) {
        List<Product> products = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        List<String> args = new ArrayList<>();
        if (status != null && Product.isValidStatus(status)) {
            selection.append(COLUMN_STATUS).append("=?");
            args.add(status);
        }
        if (startDate != null && endDate != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COLUMN_DATE).append(" BETWEEN ? AND ?");
            args.add(startDate);
            args.add(endDate);
        }
        if (minQty != null && maxQty != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COLUMN_QUANTITY).append(" BETWEEN ? AND ?");
            args.add(String.valueOf(minQty));
            args.add(String.valueOf(maxQty));
        } else if (minQty != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COLUMN_QUANTITY).append(">=?");
            args.add(String.valueOf(minQty));
        } else if (maxQty != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COLUMN_QUANTITY).append("<=?");
            args.add(String.valueOf(maxQty));
        }
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(TABLE_PRODUCTS, null,
                     selection.length() == 0 ? null : selection.toString(),
                     args.isEmpty() ? null : args.toArray(new String[0]),
                     null, null, COLUMN_DATE + " DESC")) {
            while (cursor.moveToNext()) {
                products.add(cursorToProduct(cursor));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error al filtrar productos", e);
        }
        return products;
    }

    // Reporte: conteo total y por estado
    public Report getReport(String status, String startDate, String endDate) {
        Report report = new Report();
        String selection = "";
        List<String> args = new ArrayList<>();
        if (status != null && Product.isValidStatus(status)) {
            selection += COLUMN_STATUS + "=?";
            args.add(status);
        }
        if (startDate != null && endDate != null) {
            if (!selection.isEmpty()) selection += " AND ";
            selection += COLUMN_DATE + " BETWEEN ? AND ?";
            args.add(startDate);
            args.add(endDate);
        }
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            // Total
            Cursor totalCursor = db.query(TABLE_PRODUCTS, new String[]{"COUNT(*)"},
                    selection.isEmpty() ? null : selection,
                    args.isEmpty() ? null : args.toArray(new String[0]),
                    null, null, null);
            if (totalCursor.moveToFirst()) {
                report.total = totalCursor.getInt(0);
            }
            totalCursor.close();
            // Por estado
            for (String s : new String[]{Product.STATUS_PENDING, Product.STATUS_BOUGHT}) {
                String sel = selection.isEmpty() ? COLUMN_STATUS + "=?" : selection + " AND " + COLUMN_STATUS + "=?";
                List<String> selArgs = new ArrayList<>(args);
                selArgs.add(s);
                Cursor stateCursor = db.query(TABLE_PRODUCTS, new String[]{"COUNT(*)"},
                        sel,
                        selArgs.toArray(new String[0]),
                        null, null, null);
                if (stateCursor.moveToFirst()) {
                    if (s.equals(Product.STATUS_PENDING)) report.pending = stateCursor.getInt(0);
                    else report.bought = stateCursor.getInt(0);
                }
                stateCursor.close();
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error al generar reporte", e);
        }
        return report;
    }

    // Clase interna para reporte
    public static class Report {
        public int total = 0;
        public int pending = 0;
        public int bought = 0;
    }

    private Product cursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        return new Product(id, name, status, date, quantity);
    }
}
