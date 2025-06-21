package com.example.easyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyshop.ProductDetailsActivity;
import com.example.easyshop.R;
import com.example.easyshop.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<Product> productList;

    public FavoriteAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reusing item_cart.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) return;

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());

        // Hide the quantity controls as they are not needed for favorites
        holder.quantityLayout.setVisibility(View.GONE);

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivImage);

        // Handle item click to go to product details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            // Corrected: Pass the product ID, not the whole object
            intent.putExtra("product_id", product.getProductId());
            context.startActivity(intent);
        });

        // Handle delete button click to remove from favorites
        holder.btnRemove.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && product.getProductId() != null) {
                DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("favorites")
                        .child(currentUser.getUid())
                        .child(product.getProductId());
                favRef.removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Could not remove favorite", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice;
        ImageButton btnRemove;
        // Reference to the quantity layout to hide it
        View quantityLayout;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Using the correct IDs from item_cart.xml
            ivImage = itemView.findViewById(R.id.iv_product_image_cart);
            tvName = itemView.findViewById(R.id.tv_product_name_cart);
            tvPrice = itemView.findViewById(R.id.tv_product_price_cart);
            btnRemove = itemView.findViewById(R.id.btn_remove);

            // There is no category TextView in item_cart.xml
            // We get a reference to the LinearLayout containing the quantity buttons to hide it
            quantityLayout = itemView.findViewById(R.id.btn_decrease).getParent() instanceof View ? (View) itemView.findViewById(R.id.btn_decrease).getParent() : null;
        }
    }
}