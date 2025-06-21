package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyshop.models.CartItem;
import com.example.easyshop.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvFinalTotalPrice;
    private Button btnPlaceOrder;
    private List<CartItem> cartItems;
    private double totalPrice;

    private DatabaseReference ordersRef;
    private DatabaseReference cartRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvFinalTotalPrice = findViewById(R.id.tv_final_total_price);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Should not happen, but as a safeguard
            Toast.makeText(this, "You must be logged in to checkout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(currentUser.getUid());
        cartRef = FirebaseDatabase.getInstance().getReference("carts").child(currentUser.getUid());

        Intent intent = getIntent();
        totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0);
        cartItems = (List<CartItem>) intent.getSerializableExtra("CART_ITEMS");

        tvFinalTotalPrice.setText(String.format("%.2f$", totalPrice));

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String orderId = ordersRef.push().getKey();
        long orderDate = System.currentTimeMillis();

        if (orderId != null && cartItems != null && !cartItems.isEmpty()) {
            Order order = new Order(orderId, currentUser.getUid(), cartItems, totalPrice, orderDate);

            ordersRef.child(orderId).setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Clear the cart after placing the order
                    cartRef.removeValue();
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                    // Navigate back to the main activity (home screen)
                    Intent mainIntent = new Intent(CheckoutActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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