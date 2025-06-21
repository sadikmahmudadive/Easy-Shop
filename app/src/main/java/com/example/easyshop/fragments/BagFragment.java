package com.example.easyshop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.R;
import com.example.easyshop.adapters.CartAdapter;
import com.example.easyshop.models.CartItem;
import com.example.easyshop.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BagFragment extends Fragment implements CartAdapter.CartItemListener {

    private static final String TAG = "BagFragment";

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView tvTotalPrice, tvEmptyBag;
    private Button btnCheckout;
    private Group groupCartContent;

    private DatabaseReference cartRef;
    private DatabaseReference ordersRef;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bag, container, false);

        rvCartItems = view.findViewById(R.id.rv_cart_items);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        tvEmptyBag = view.findViewById(R.id.tv_empty_bag);
        groupCartContent = view.findViewById(R.id.group_cart_content);

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItems, this);
        rvCartItems.setAdapter(cartAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            cartRef = FirebaseDatabase.getInstance().getReference("carts").child(currentUser.getUid());
            ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(currentUser.getUid());
            fetchCartItems();
        }

        btnCheckout.setOnClickListener(v -> placeOrder());

        return view;
    }

    private void fetchCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CartItem item = itemSnapshot.getValue(CartItem.class);
                    if (item != null) {
                        cartItems.add(item);
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch cart items.", error.toException());
            }
        });
    }

    private void updateUI() {
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        if (cartItems.isEmpty()) {
            groupCartContent.setVisibility(View.GONE);
            tvEmptyBag.setVisibility(View.VISIBLE);
        } else {
            groupCartContent.setVisibility(View.VISIBLE);
            tvEmptyBag.setVisibility(View.GONE);
        }
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            try {
                double price = Double.parseDouble(item.getProduct().getPrice().replace("$", ""));
                total += price * item.getQuantity();
            } catch (NumberFormatException e) {
                Log.e(TAG, "Price format error", e);
            }
        }
        tvTotalPrice.setText(String.format(Locale.US, "%.2f$", total));
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Your bag is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = ordersRef.push().getKey();
        if (orderId == null) {
            Toast.makeText(getContext(), "Could not create order.", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (CartItem item : cartItems) {
            try {
                double price = Double.parseDouble(item.getProduct().getPrice().replace("$", ""));
                total += price * item.getQuantity();
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderDate(System.currentTimeMillis());
        order.setItems(new ArrayList<>(cartItems));
        order.setTotalPrice(total);

        ordersRef.child(orderId).setValue(order).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear the cart
                cartRef.removeValue().addOnCompleteListener(clearTask -> {
                    if (clearTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Failed to place order.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQuantityChanged() {
        updateTotalPrice();
        // Persist changes back to Firebase
        for (CartItem item : cartItems) {
            cartRef.child(item.getProduct().getProductId()).setValue(item);
        }
    }

    @Override
    public void onItemRemoved(String productId) {
        cartRef.child(productId).removeValue();
    }
}