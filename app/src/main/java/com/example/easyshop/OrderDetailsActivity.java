package com.example.easyshop;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.adapters.OrderDetailAdapter;
import com.example.easyshop.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderDate, tvOrderTotal;
    private RecyclerView rvOrderItems;
    private OrderDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar_order_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        rvOrderItems = findViewById(R.id.rv_order_items);

        Order order = (Order) getIntent().getSerializableExtra("ORDER_DETAILS");

        if (order != null) {
            populateOrderDetails(order);
        } else {
            Toast.makeText(this, "Could not load order details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void populateOrderDetails(Order order) {
        tvOrderId.setText(order.getOrderId());
        tvOrderTotal.setText(String.format(Locale.US, "%.2f$", order.getTotalPrice()));

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String dateString = sdf.format(new Date(order.getOrderDate()));
        tvOrderDate.setText(dateString);

        adapter = new OrderDetailAdapter(this, order.getItems());
        rvOrderItems.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}