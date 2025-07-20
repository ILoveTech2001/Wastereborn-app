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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private List<Product> fullProductList;  // Unfiltered full list
    private OnProductClickListener clickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> list) {
        this.productList = list;
        this.fullProductList = new ArrayList<>(list);  // Make a copy
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        System.out.println("🔍 ProductAdapter: onBindViewHolder() called for position: " + position);
        Product product = productList.get(position);
        System.out.println("🔍 ProductAdapter: Binding product: " + product.getName());

        // Load image from URL if available, otherwise use resource ID
        if (product.hasImageUrl()) {
            System.out.println("🖼️ Loading image from URL: " + product.getImageUrl());
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.mipmap.sample_product)
                    .error(R.mipmap.sample_product)
                    .into(holder.imageProduct);
        } else {
            System.out.println("🖼️ Using default image resource");
            holder.imageProduct.setImageResource(product.getImageResId());
        }
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

        // Add click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = productList.size();
        System.out.println("🔍 ProductAdapter: getItemCount() called, returning: " + count);
        return count;
    }

    // Used externally to apply a full filtered list
    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    // Update the full product list and refresh display
    public void updateProductList(List<Product> newProductList) {
        this.fullProductList = new ArrayList<>(newProductList);
        this.productList = new ArrayList<>(newProductList);
        notifyDataSetChanged();
    }

    // ✅ Method to filter based on category
    public void updateFilter(String category) {
        System.out.println("🔍 ProductAdapter: Filtering by category: " + category);
        System.out.println("🔍 ProductAdapter: Full product list size: " + fullProductList.size());

        if (category.equalsIgnoreCase("All")) {
            productList = new ArrayList<>(fullProductList);
            System.out.println("🔍 ProductAdapter: Showing all products: " + productList.size());
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : fullProductList) {
                System.out.println("🔍 ProductAdapter: Checking product: " + p.getName() + " | Category: " + p.getCategory());
                if (p.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(p);
                    System.out.println("🔍 ProductAdapter: Product matches filter");
                }
            }
            productList = filtered;
            System.out.println("🔍 ProductAdapter: Filtered products: " + productList.size());
        }
        notifyDataSetChanged();
        System.out.println("🔍 ProductAdapter: notifyDataSetChanged() called");
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
