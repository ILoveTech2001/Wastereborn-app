package com.example.wastereborn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private List<Product> fullProductList;  // Unfiltered full list

    public ProductAdapter(List<Product> list) {
        this.productList = list;
        this.fullProductList = new ArrayList<>(list);  // Make a copy
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.imageProduct.setImageResource(product.getImageResId());
        holder.textProductName.setText(product.getName());
        holder.textPrice.setText("FCFA " + product.getPrice());

        holder.btnLike.setImageResource(product.isLiked() ? R.drawable.like : R.drawable.ic_like);
        holder.btnLike.setOnClickListener(v -> {
            product.setLiked(!product.isLiked());
            notifyItemChanged(position);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(product);
            Toast.makeText(v.getContext(), product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Used externally to apply a full filtered list
    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    // ✅ Method to filter based on category
    public void updateFilter(String category) {
        if (category.equalsIgnoreCase("All")) {
            productList = new ArrayList<>(fullProductList);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : fullProductList) {
                if (p.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(p);
                }
            }
            productList = filtered;
        }
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textPrice;
        ImageButton btnLike;
        Button btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_price);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
