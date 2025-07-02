package com.example.easyshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyshop.R;
import com.example.easyshop.models.CartItem;
import java.util.List;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.ViewHolder> {
    private final List<CartItem> items;

    public OrderDetailProductAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order_detail_product, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvName.setText(item.getProduct().getName());
        holder.tvQty.setText("x" + item.getQuantity());
        holder.tvPrice.setText(item.getProduct().getPrice() + " Tk");
    }

    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvPrice;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_product_name);
            tvQty = v.findViewById(R.id.tv_product_qty);
            tvPrice = v.findViewById(R.id.tv_product_price);
        }
    }
}