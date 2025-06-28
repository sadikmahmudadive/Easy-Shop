package com.example.easyshop.adapters;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapterAdmin extends RecyclerView.Adapter<ProductAdapterAdmin.ViewHolder> {
    private final List<Product> productList;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(Product product, int position);
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(Product product, int position);
    }

    public ProductAdapterAdmin(List<Product> products) {
        // Always store a new list to avoid side effects
        this.productList = new ArrayList<>(products != null ? products : new ArrayList<>());
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    /**
     * Update the adapter's data. Always use a fresh copy to avoid ConcurrentModificationException.
     */
    public void updateData(List<Product> products) {
        productList.clear();
        if (products != null)
            productList.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);

        holder.tvTitle.setText(p.getTitle());
        holder.tvBrand.setText(p.getBrand());
        // Show old price with strikethrough if not empty
        if (p.getOldPrice() != null && !p.getOldPrice().isEmpty()) {
            holder.tvOldPrice.setVisibility(View.VISIBLE);
            holder.tvOldPrice.setText(String.format("%s$", p.getOldPrice()));
            holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvOldPrice.setVisibility(View.GONE);
        }

        holder.tvNewPrice.setText(String.format("%s$", p.getPrice() != null && !p.getPrice().isEmpty() ? p.getPrice() : "0"));
        holder.tvReviews.setText(String.format("(%d)", p.getReviewCount() != null ? p.getReviewCount() : 0));

        Glide.with(holder.itemView.getContext())
                .load(p.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgProduct);

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.btnOptions);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_product_card, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                int pos = holder.getAdapterPosition();
                if (id == R.id.action_edit && onEditClickListener != null && pos != RecyclerView.NO_POSITION) {
                    onEditClickListener.onEditClick(p, pos);
                    return true;
                } else if (id == R.id.action_delete && onDeleteClickListener != null && pos != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(p, pos);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBrand, tvOldPrice, tvNewPrice, tvReviews;
        ImageView imgProduct;
        ImageButton btnOptions;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvOldPrice = itemView.findViewById(R.id.tv_old_price);
            tvNewPrice = itemView.findViewById(R.id.tv_new_price);
            tvReviews = itemView.findViewById(R.id.tv_reviews);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnOptions = itemView.findViewById(R.id.btn_product_options);
        }
    }
}