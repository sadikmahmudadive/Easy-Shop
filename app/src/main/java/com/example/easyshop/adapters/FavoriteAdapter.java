package com.example.easyshop.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.Product;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavouriteViewHolder> {

    public interface OnRemoveClickListener {
        void onRemoveClick(Product product);
    }

    private final List<Product> products;
    private final OnRemoveClickListener removeClickListener;

    public FavoriteAdapter(List<Product> products, OnRemoveClickListener removeClickListener) {
        this.products = products;
        this.removeClickListener = removeClickListener;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite_product, parent, false);
        return new FavouriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Product p = products.get(position);
        Log.d("FAV_ADAPTER_DEBUG", "Binding: " + p.getProductId() + " | " + p.getName());
        holder.brand.setText(p.getBrand());
        holder.title.setText(p.getName());
        holder.color.setText("Color: " + (p.getCategory() != null ? p.getCategory() : "-"));
        holder.size.setText("Size: " + (p.getSelectedSize() != null ? p.getSelectedSize() : "-"));
        holder.price.setText(p.getPrice() + "$");

        Glide.with(holder.image.getContext())
                .load(p.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);

        holder.btnRemove.setOnClickListener(v -> removeClickListener.onRemoveClick(p));
    }

    @Override
    public int getItemCount() {
        Log.d("FAV_ADAPTER_DEBUG", "getItemCount: " + (products != null ? products.size() : 0));
        return products.size();
    }

    static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView brand, title, color, size, price;
        ImageButton btnRemove;
        FavouriteViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_favourite_product);
            brand = itemView.findViewById(R.id.tv_brand);
            title = itemView.findViewById(R.id.tv_product_title);
            color = itemView.findViewById(R.id.tv_product_color);
            size = itemView.findViewById(R.id.tv_product_size);
            price = itemView.findViewById(R.id.tv_product_price);
            btnRemove = itemView.findViewById(R.id.btn_remove_favourite);
        }
    }
}