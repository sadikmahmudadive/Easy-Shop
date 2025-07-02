package com.example.easyshop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyshop.adapters.OrderDetailProductAdapter;
import com.example.easyshop.models.Order;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminOrderDetails extends AppCompatActivity {

    private TextView tvOrderNum, tvOrderDate, tvTrackingNumber, tvOrderStatus, tvItemsCount,
            tvShippingAddress, tvPaymentMethod, tvDeliveryMethod, tvDiscount, tvTotalAmount;
    private ImageView ivPaymentIcon;
    private RecyclerView rvOrderItems;
    private Button btnCancelOrder, btnCompleteOrder;

    private String userId, orderId;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getStringExtra("userId");
        orderId = getIntent().getStringExtra("orderId");
        if (userId == null || orderId == null) {
            Toast.makeText(this, "Order not specified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Bind views
        tvOrderNum = findViewById(R.id.tv_order_num);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvTrackingNumber = findViewById(R.id.tv_tracking_number);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvItemsCount = findViewById(R.id.tv_items_count);
        tvShippingAddress = findViewById(R.id.tv_shipping_address);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        ivPaymentIcon = findViewById(R.id.iv_payment_icon);
        tvDeliveryMethod = findViewById(R.id.tv_delivery_method);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rvOrderItems = findViewById(R.id.rv_order_items);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);
        btnCompleteOrder = findViewById(R.id.btn_complete_order);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        fetchOrderDetails();

        btnCancelOrder.setOnClickListener(v -> updateOrderStatus("Cancelled"));
        btnCompleteOrder.setOnClickListener(v -> updateOrderStatus("Completed"));
    }

    private void fetchOrderDetails() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders")
                .child(userId).child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                order = snapshot.getValue(Order.class);
                if (order != null) {
                    populateUI(order);
                } else {
                    Toast.makeText(AdminOrderDetails.this, "Order not found.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminOrderDetails.this, "Failed to load order.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateUI(Order order) {
        tvOrderNum.setText("Order №" + order.getOrderId());
        tvOrderDate.setText(formatDate(order.getOrderDate()));
        tvTrackingNumber.setText("Tracking number: " + order.getTrackingNumber());
        tvOrderStatus.setText(order.getStatus());
        tvShippingAddress.setText("Shipping Address: " + order.getShippingAddress());
        tvDeliveryMethod.setText("Delivery method: " + order.getDeliveryMethod());
        tvDiscount.setText("Discount: " + order.getDiscountText());
        tvTotalAmount.setText("Total Amount: " + order.getTotalPrice() + " Tk");

        // Payment method and icon logic (update as needed)
        tvPaymentMethod.setText(order.getPaymentMethod());
        // You may want to set a payment icon based on method string:
        // ivPaymentIcon.setImageResource(R.drawable.ic_cod); etc

        // Order items
        if (order.getItems() != null) {
            tvItemsCount.setText(order.getItems().size() + " items");
            rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
            rvOrderItems.setAdapter(new OrderDetailProductAdapter(order.getItems()));
        } else {
            tvItemsCount.setText("0 items");
        }

        // Status color (green for completed/delivered, red for cancelled, etc)
        // You can adjust tvOrderStatus.setTextColor() based on order.getStatus()
    }

    private void updateOrderStatus(String newStatus) {
        if (order == null) return;
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders")
                .child(userId).child(orderId).child("status");
        orderRef.setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    tvOrderStatus.setText(newStatus);
                    Toast.makeText(this, "Order status updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String formatDate(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}