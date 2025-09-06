package com.example.lista1_de_compras.model;

/**
 * Modelo para representar un producto en la lista de compras.
 */
public class Product {
    public static final String STATUS_PENDING = "PENDIENTE";
    public static final String STATUS_BOUGHT = "COMPRADO";

    private int id;
    private String name;
    private String status;
    private String date; // yyyy-MM-dd
    private int quantity;

    public Product(int id, String name, String status, String date, int quantity) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
        this.quantity = quantity;
    }

    public Product(String name, String status, String date, int quantity) {
        this(-1, name, status, date, quantity);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public static boolean isValidStatus(String status) {
        return STATUS_PENDING.equals(status) || STATUS_BOUGHT.equals(status);
    }
}
