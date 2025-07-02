package com.example.easyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.OrderDetailsActivity;
import com.example.easyshop.R;
import com.example.easyshop.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        holder.tvOrderNum.setText("Order №" + order.getOrderId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        holder.tvOrderDate.setText(sdf.format(new Date(order.getOrderDate())));
        holder.tvTrackingNumber.setText("Tracking number: " + (order.getTrackingNumber() != null ? order.getTrackingNumber() : ""));
        holder.tvQuantity.setText("Quantity: " + (order.getItems() != null ? order.getItems().size() : 0));
        holder.tvTotalAmount.setText("Total Amount: " + String.format(Locale.US, "%.0fTk", order.getTotalPrice()));
        holder.tvStatus.setText(order.getStatus() != null ? order.getStatus() : "Delivered");

        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("ORDER_DETAILS", order);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateData(List<Order> orders) {
        orderList.clear();
        orderList.addAll(orders);
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNum, tvOrderDate, tvTrackingNumber, tvQuantity, tvTotalAmount, tvStatus;
        Button btnDetails;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNum = itemView.findViewById(R.id.tv_order_num);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTrackingNumber = itemView.findViewById(R.id.tv_tracking_number);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}