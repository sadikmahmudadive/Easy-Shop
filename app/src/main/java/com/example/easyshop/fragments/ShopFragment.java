package com.example.easyshop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.R;
import com.example.easyshop.adapters.ProductGridAdapter;
import com.example.easyshop.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private static final String TAG = "ShopFragment";

    private RecyclerView rvShopProducts;
    private ProductGridAdapter productGridAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Initialize UI elements
        progressBar = view.findViewById(R.id.progress_bar_shop);
        rvShopProducts = view.findViewById(R.id.rv_shop_products);
        // Use a GridLayoutManager for a 2-column grid
        rvShopProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize product list and the specific grid adapter
        productList = new ArrayList<>();
        productGridAdapter = new ProductGridAdapter(getContext(), productList);
        rvShopProducts.setAdapter(productGridAdapter);

        // Get reference to the "products" node in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Fetch the data from Firebase
        fetchAllProducts();

        return view;
    }

    private void fetchAllProducts() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        // Important: Store the Firebase key as the product ID
                        product.setProductId(snapshot.getKey());
                        productList.add(product);
                    }
                }
                productGridAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}