package com.example.easyshop.adapters;

import android.content.Context;
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
import com.example.easyshop.models.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged();
        void onItemRemoved(String productId);
    }

    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        if (item == null || item.getProduct() == null) return;

        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        try {
            double price = Double.parseDouble(item.getProduct().getPrice().replace("Tk", ""));
            holder.tvProductPrice.setText(String.format(Locale.US, "%.2f Tk", price));
        } catch (NumberFormatException e) {
            holder.tvProductPrice.setText("N/A");
        }

        Glide.with(context)
                .load(item.getProduct().getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivProductImage);

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            listener.onQuantityChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                listener.onQuantityChanged();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            listener.onItemRemoved(item.getProduct().getProductId());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvQuantity;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image_cart);
            tvProductName = itemView.findViewById(R.id.tv_product_name_cart);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price_cart);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}