package com.example.wastereborn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartList;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onRemove(Product product);
    }

    public CartAdapter(List<Product> cartList, OnCartItemChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.textName.setText(product.getName());
        holder.textPrice.setText("FCFA " + product.getPrice());
        holder.textQuantity.setText("x1"); // Placeholder, for now

        holder.btnRemove.setOnClickListener(v -> {
            cartList.remove(position);
            notifyItemRemoved(position);
            listener.onRemove(product);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice, textQuantity;
        ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_cart_product_name);
            textPrice = itemView.findViewById(R.id.text_cart_product_price);
            textQuantity = itemView.findViewById(R.id.text_cart_quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove_from_cart);
        }
    }
}

