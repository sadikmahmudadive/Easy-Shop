package com.example.easyshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyshop.models.Product;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easyshop.R;

import java.util.List;

/**
 * ProductAdapter supporting:
 * - Product click
 * - Favorite button click (with size selection flow)
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Product> productList;
    private final boolean showDiscountBadge;
    private final OnProductClickListener onProductClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;

    // Interface for product item click
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Interface for favorite button click (for size selection flow)
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product, int position);
    }

    public ProductAdapter(List<Product> products, boolean showDiscountBadge, OnProductClickListener listener) {
        this.productList = products;
        this.showDiscountBadge = showDiscountBadge;
        this.onProductClickListener = listener;
    }

    // Setter for favorite click listener
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);

        // Load image
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imgProduct);
        } else if (p.getImageResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(p.getImageResId())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_image);
        }

        // Discount badge or NEW badge
        if (showDiscountBadge && p.getDiscountText() != null) {
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscount.setText(p.getDiscountText());
            holder.tvNewBadge.setVisibility(View.GONE);
        } else if (!showDiscountBadge && p.isNew()) {
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvNewBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvNewBadge.setVisibility(View.GONE);
        }

        // Stars and reviews
        holder.tvReviews.setText(String.format("(%d)", p.getReviewCount()));

        // Brand and title
        holder.tvBrand.setText(p.getBrand());
        holder.tvTitle.setText(p.getTitle());

        // Price
        holder.tvOldPrice.setText(String.format("%sTk", p.getOldPrice()));
        holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvNewPrice.setText(String.format("%sTk", p.getPrice()));

        // Product click (for details)
        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(p);
            }
        });

        // Favorite button (with size selector flow)
        holder.btnFavorite.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteClick(p, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiscount, tvNewBadge, tvReviews, tvBrand, tvTitle, tvOldPrice, tvNewPrice;
        ImageView imgProduct;
        ImageButton btnFavorite;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvNewBadge = itemView.findViewById(R.id.tv_new_badge);
            tvReviews = itemView.findViewById(R.id.tv_reviews);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvOldPrice = itemView.findViewById(R.id.tv_old_price);
            tvNewPrice = itemView.findViewById(R.id.tv_new_price);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}