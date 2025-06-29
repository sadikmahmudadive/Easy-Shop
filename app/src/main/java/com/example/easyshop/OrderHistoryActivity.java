package com.example.easyshop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.adapters.OrderHistoryAdapter;
import com.example.easyshop.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderHistoryAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredOrders = new ArrayList<>();
    private DatabaseReference ordersRef;

    private AppCompatButton btnDelivered, btnProcessing, btnCancelled;
    private TextView tvNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Toolbar buttons
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        ImageButton btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
        });

        // Tabs
        btnDelivered = findViewById(R.id.btn_delivered);
        btnProcessing = findViewById(R.id.btn_processing);
        btnCancelled = findViewById(R.id.btn_cancelled);

        btnDelivered.setOnClickListener(v -> setOrderFilter("Delivered"));
        btnProcessing.setOnClickListener(v -> setOrderFilter("Processing"));
        btnCancelled.setOnClickListener(v -> setOrderFilter("Cancelled"));

        // RecyclerView & No Orders Text
        rvOrders = findViewById(R.id.rv_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(this, filteredOrders);
        rvOrders.setAdapter(adapter);

        tvNoOrders = findViewById(R.id.tv_no_orders);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(currentUser.getUid());
            fetchOrderHistory();
        } else {
            Toast.makeText(this, "You need to be logged in to see your orders.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchOrderHistory() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrders.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        allOrders.add(order);
                    }
                }
                Collections.reverse(allOrders);
                setOrderFilter(getSelectedStatusTab()); // Default filter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load order history.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOrderFilter(String status) {
        updateTabStyles(status);

        filteredOrders.clear();
        for (Order order : allOrders) {
            if (order.getStatus() != null && order.getStatus().equalsIgnoreCase(status)) {
                filteredOrders.add(order);
            }
        }
        adapter.notifyDataSetChanged();
        if (filteredOrders.isEmpty()) {
            tvNoOrders.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvNoOrders.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
        }
    }

    private void updateTabStyles(String selectedStatus) {
        btnDelivered.setBackgroundResource(selectedStatus.equals("Delivered") ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        btnDelivered.setTextColor(getResources().getColor(selectedStatus.equals("Delivered") ? android.R.color.white : android.R.color.black));
        btnProcessing.setBackgroundResource(selectedStatus.equals("Processing") ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        btnProcessing.setTextColor(getResources().getColor(selectedStatus.equals("Processing") ? android.R.color.white : android.R.color.black));
        btnCancelled.setBackgroundResource(selectedStatus.equals("Cancelled") ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        btnCancelled.setTextColor(getResources().getColor(selectedStatus.equals("Cancelled") ? android.R.color.white : android.R.color.black));
    }

    private String getSelectedStatusTab() {
        // Default to Delivered on launch
        return "Delivered";
    }
}