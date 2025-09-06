package com.example.lista1_de_compras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lista1_de_compras.model.Product;
import java.util.List;

/**
 * Adapter para mostrar productos en RecyclerView.
 * Implementa ViewHolder y gestiona clics y swipe-to-delete.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onItemDelete(Product product, int position);
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeItem(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Product product, int position) {
        productList.add(position, product);
        notifyItemInserted(position);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvDate;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
        public void bind(final Product product, final OnItemClickListener listener) {
            tvName.setText(product.getName());
            tvStatus.setText(product.getStatus());
            tvDate.setText(product.getDate());
            itemView.setOnClickListener(v -> listener.onItemClick(product));
            itemView.setOnLongClickListener(v -> {
                listener.onItemDelete(product, getAdapterPosition());
                return true;
            });
        }
    }
}

