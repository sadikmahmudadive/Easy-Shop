package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyshop.R;
import com.example.easyshop.adapters.OrderAdapter;
import com.example.easyshop.models.Order;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.*;
import java.util.*;

public class ManageOrdersActivity extends AppCompatActivity {

    private SearchView searchView;
    private Spinner spinnerStatusFilter;
    private TextView textDateRange, textPageInfo;
    private MaterialButton btnExport, btnPrevPage, btnNextPage;
    private RecyclerView recyclerOrders;

    private OrderAdapter orderAdapter;
    private List<Order> allOrders = new ArrayList<>();
    private int currentPage = 1, pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        searchView = findViewById(R.id.searchview_order);
        spinnerStatusFilter = findViewById(R.id.spinner_status_filter);
        textDateRange = findViewById(R.id.text_date_range);
        textPageInfo = findViewById(R.id.text_page_info);
        btnExport = findViewById(R.id.btn_export_orders);
        btnPrevPage = findViewById(R.id.btn_prev_page);
        btnNextPage = findViewById(R.id.btn_next_page);
        recyclerOrders = findViewById(R.id.recycler_orders);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(getPagedOrders(), (order, anchor) -> {
            // On order tap, open details activity
            Intent intent = new Intent(this, AdminOrderDetails.class);
            intent.putExtra("userId", order.getUserId());
            intent.putExtra("orderId", order.getOrderId());
            startActivity(intent);
        });
        recyclerOrders.setAdapter(orderAdapter);

        fetchAllOrdersFromFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { filterOrders(query); return true; }
            @Override public boolean onQueryTextChange(String newText) { filterOrders(newText); return true; }
        });

        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updateOrderList();
            }
        });
        btnNextPage.setOnClickListener(v -> {
            if ((currentPage * pageSize) < allOrders.size()) {
                currentPage++;
                updateOrderList();
            }
        });
        btnExport.setOnClickListener(v -> {
            Toast.makeText(this, "Exporting orders...", Toast.LENGTH_SHORT).show();
        });
    }

    // Fetch all orders for all users for admin
    private void fetchAllOrdersFromFirebase() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // 1. Create a new temporary list
                List<Order> tempOrders = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String userId = userSnap.getKey();
                    for (DataSnapshot orderSnap : userSnap.getChildren()) {
                        Order order = orderSnap.getValue(Order.class);
                        if (order != null) {
                            order.setUserId(userId);
                            tempOrders.add(order);
                        }
                    }
                }
                // 2. Only now replace allOrders and update the UI
                allOrders.clear();
                allOrders.addAll(tempOrders);
                currentPage = 1;
                updateOrderList();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ManageOrdersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Order> getPagedOrders() {
        // Always return a new copy!
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allOrders.size());
        if (fromIndex > allOrders.size()) return new ArrayList<>();
        // Defensive copy for subList
        return new ArrayList<>(allOrders.subList(fromIndex, toIndex));
    }

    private void updateOrderList() {
        orderAdapter = new OrderAdapter(getPagedOrders(), (order, anchor) -> {
            Intent intent = new Intent(this, AdminOrderDetails.class);
            intent.putExtra("userId", order.getUserId());
            intent.putExtra("orderId", order.getOrderId());
            startActivity(intent);
        });
        recyclerOrders.setAdapter(orderAdapter);
        updatePageInfo(allOrders.size());
    }

    private void updatePageInfo(int total) {
        int pageCount = (int)Math.ceil((double)total/pageSize);
        textPageInfo.setText("Page " + currentPage + " of " + (pageCount==0?1:pageCount));
    }

    private void filterOrders(String query) {
        List<Order> filtered = new ArrayList<>();
        for (Order o : allOrders) {
            if ((o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(query.toLowerCase())) ||
                    (o.getOrderId() != null && o.getOrderId().toLowerCase().contains(query.toLowerCase()))) {
                filtered.add(o);
            }
        }
        currentPage = 1;
        orderAdapter = new OrderAdapter(filtered, (order, anchor) -> {
            Intent intent = new Intent(this, AdminOrderDetails.class);
            intent.putExtra("userId", order.getUserId());
            intent.putExtra("orderId", order.getOrderId());
            startActivity(intent);
        });
        recyclerOrders.setAdapter(orderAdapter);
        updatePageInfo(filtered.size());
    }
}