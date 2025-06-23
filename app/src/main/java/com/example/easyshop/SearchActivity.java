package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.adapters.ProductGridAdapter;
import com.example.easyshop.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnBack, btnClear, btnVisualSearch;
    private RecyclerView rvResults;
    private ProgressBar progressBar;
    private ProductGridAdapter productGridAdapter;
    private List<Product> productList = new ArrayList<>();
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.et_search);
        btnBack = findViewById(R.id.btn_back);
        btnClear = findViewById(R.id.btn_clear);
        btnVisualSearch = findViewById(R.id.btn_visual_search);
        rvResults = findViewById(R.id.rv_search_results);
        progressBar = findViewById(R.id.progress_bar_search);

        rvResults.setLayoutManager(new GridLayoutManager(this, 2));
        productGridAdapter = new ProductGridAdapter(this, productList, true);
        rvResults.setAdapter(productGridAdapter);

        productsRef = FirebaseDatabase.getInstance().getReference("products");

        btnBack.setOnClickListener(v -> onBackPressed());
        btnClear.setOnClickListener(v -> etSearch.setText(""));

        btnVisualSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, VisualSearchActivity.class));
        });

        // Show/hide clear button based on input
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Listen for search action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Also search as you type (optional: you can comment this out if you want search only on submit)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) performSearch();
                else {
                    productList.clear();
                    productGridAdapter.notifyDataSetChanged();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            productList.clear();
            productGridAdapter.notifyDataSetChanged();
            return;
        }
        searchProducts(query);
    }

    private void searchProducts(String query) {
        progressBar.setVisibility(View.VISIBLE);
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                String q = query.toLowerCase();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        String name = product.getName() != null ? product.getName().toLowerCase() : "";
                        String brand = product.getBrand() != null ? product.getBrand().toLowerCase() : "";
                        String category = product.getCategory() != null ? product.getCategory().toLowerCase() : "";
                        String description = product.getDescription() != null ? product.getDescription().toLowerCase() : "";
                        if (
                                name.contains(q) ||
                                        brand.contains(q) ||
                                        category.contains(q) ||
                                        description.contains(q)
                        ) {
                            productList.add(product);
                        }
                    }
                }
                productGridAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if (productList.isEmpty()) {
                    Toast.makeText(SearchActivity.this, R.string.no_results_found, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Search failed.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}