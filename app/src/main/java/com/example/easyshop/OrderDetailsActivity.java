package com.example.easyshop;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.adapters.OrderDetailAdapter;
import com.example.easyshop.models.Order;

public class OrderDetailsActivity extends AppCompatActivity {
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        order = (Order) getIntent().getSerializableExtra("ORDER_DETAILS");

        TextView tvOrderNum = findViewById(R.id.tv_order_num);
        TextView tvOrderDate = findViewById(R.id.tv_order_date);
        TextView tvTrackingNumber = findViewById(R.id.tv_tracking_number);
        TextView tvStatus = findViewById(R.id.tv_order_status);
        TextView tvItemsCount = findViewById(R.id.tv_items_count);
        RecyclerView rvOrderItems = findViewById(R.id.rv_order_items);
        TextView tvShippingAddress = findViewById(R.id.tv_shipping_address);
        TextView tvPaymentMethod = findViewById(R.id.tv_payment_method);
        TextView tvDeliveryMethod = findViewById(R.id.tv_delivery_method);
        TextView tvDiscount = findViewById(R.id.tv_discount);
        TextView tvTotalAmount = findViewById(R.id.tv_total_amount);

        if (order == null) return;

        tvOrderNum.setText("Order No: " + order.getOrderId());
        tvOrderDate.setText(new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date(order.getOrderDate())));
        tvTrackingNumber.setText("Tracking number: " + (order.getTrackingNumber() != null ? order.getTrackingNumber() : ""));
        tvStatus.setText(order.getStatus() != null ? order.getStatus() : "Delivered");
        tvItemsCount.setText(order.getItems() != null ? order.getItems().size() + " items" : "0 items");
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(new OrderDetailAdapter(this, order.getItems()));

        tvShippingAddress.setText("Shipping Address: " + (order.getShippingAddress() != null ? order.getShippingAddress() : ""));
        tvPaymentMethod.setText("Payment method: " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : ""));
        tvDeliveryMethod.setText("Delivery method: " + (order.getDeliveryMethod() != null ? order.getDeliveryMethod() : ""));
        tvDiscount.setText("Discount: " + (order.getDiscountText() != null ? order.getDiscountText() : ""));
        tvTotalAmount.setText("Total Amount: " + String.format("%.0f$", order.getTotalPrice()));
    }
}