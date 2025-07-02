package com.example.easyshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.DetailViewHolder> {

    private final Context context;
    private final List<CartItem> items;

    public OrderDetailAdapter(Context context, List<CartItem> items) {
        this.context = context;
        // Defensive: avoid null pointer exceptions
        this.items = (items != null) ? items : new ArrayList<>();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail_product, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        CartItem item = items.get(position);
        if (item == null || item.getProduct() == null) return;

        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvQuantity.setText(String.format(Locale.US, "Quantity: %d", item.getQuantity()));

        try {
            double price = Double.parseDouble(item.getProduct().getPrice().replace("Tk", ""));
            double lineItemTotal = price * item.getQuantity();
            holder.tvLineItemPrice.setText(String.format(Locale.US, "%.2fTk", lineItemTotal));
        } catch (NumberFormatException e) {
            holder.tvLineItemPrice.setText("N/A");
        }

        Glide.with(context)
                .load(item.getProduct().getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        // Defensive: avoid null pointer exceptions
        return (items != null) ? items.size() : 0;
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvQuantity, tvLineItemPrice;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvLineItemPrice = itemView.findViewById(R.id.tv_line_item_price);
        }
    }
}