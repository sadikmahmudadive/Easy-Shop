package com.example.easyshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.Product;

import java.util.List;

public class SimilarProductsAdapter extends RecyclerView.Adapter<SimilarProductsAdapter.SimilarProductViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final List<Product> productList;
    private final OnItemClickListener listener;

    // Updated constructor: requires click listener
    public SimilarProductsAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_similar_product, parent, false);
        return new SimilarProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.title.setText(product.getName());
        holder.brand.setText(product.getBrand());
        holder.price.setText("$" + product.getPrice());
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.image.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class SimilarProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, brand, price;
        SimilarProductViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.similar_product_image);
            title = itemView.findViewById(R.id.similar_product_title);
            brand = itemView.findViewById(R.id.similar_product_brand);
            price = itemView.findViewById(R.id.similar_product_price);
        }
    }
}