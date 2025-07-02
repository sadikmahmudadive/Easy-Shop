package com.example.easyshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyshop.R;
import com.example.easyshop.models.Order;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onOrderActionsClicked(Order order, View anchorView);
    }

    private List<Order> orderList;
    private OnOrderActionListener actionListener;

    public OrderAdapter(List<Order> orderList, OnOrderActionListener listener) {
        this.orderList = orderList;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_row, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textOrderId.setText(order.getOrderId());
        holder.textCustomer.setText(order.getCustomerName() != null ? order.getCustomerName() : "");
        holder.textOrderDate.setText(formatDate(order.getOrderDate()));
        holder.textOrderStatus.setText(order.getStatus());
        holder.textOrderTotal.setText(String.format("Tk%.2f", order.getTotalPrice()));
        holder.btnOrderActions.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onOrderActionsClicked(order, v);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textCustomer, textOrderDate, textOrderStatus, textOrderTotal;
        ImageButton btnOrderActions;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textCustomer = itemView.findViewById(R.id.text_customer);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            textOrderTotal = itemView.findViewById(R.id.text_order_total);
            btnOrderActions = itemView.findViewById(R.id.btn_order_actions);
        }
    }

    private String formatDate(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}