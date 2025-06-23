package com.example.easyshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.R;
import com.example.easyshop.adapters.CategoryAdapter;
import com.example.easyshop.adapters.ProductGridAdapter;
import com.example.easyshop.models.Category;
import com.example.easyshop.models.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Make sure your Category and Product model classes have the required fields and setters/getters for Firebase mapping.

public class ShopFragment extends Fragment {

    private RecyclerView rvShopProducts, rvCategoryCards;
    private ProductGridAdapter productGridAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;

    private TabLayout categoryTabLayout;
    private boolean isGrid = true;
    private ImageButton btnLayoutSwitch, btnFilter, btnSort, btnSearch, btnBack;
    private TextView tvSortLabel;

    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;

    private DatabaseReference categoriesRef, productsRef;

    private String selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Init UI
        progressBar = view.findViewById(R.id.progress_bar_shop);
        rvShopProducts = view.findViewById(R.id.rv_shop_products);
        rvCategoryCards = view.findViewById(R.id.rv_category_cards);
        categoryTabLayout = view.findViewById(R.id.category_tab_layout);

        btnLayoutSwitch = view.findViewById(R.id.btn_layout_switch);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnSort = view.findViewById(R.id.btn_sort);
        btnSearch = view.findViewById(R.id.btn_search);
        btnBack = view.findViewById(R.id.btn_back);
        tvSortLabel = view.findViewById(R.id.tv_sort_label);

        // Product grid/list
        productList = new ArrayList<>();
        productGridAdapter = new ProductGridAdapter(getContext(), productList, isGrid);
        rvShopProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvShopProducts.setAdapter(productGridAdapter);

        // Category horizontal cards
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            selectedCategory = category.getName();
            fetchProductsByCategory(selectedCategory);
            selectTabByCategory(selectedCategory);
        });
        rvCategoryCards.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvCategoryCards.setAdapter(categoryAdapter);

        // Firebase references
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        productsRef = FirebaseDatabase.getInstance().getReference("products");

        // Fetch categories and products
        fetchCategories();

        // TabLayout event
        categoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                selectedCategory = tab.getText().toString();
                fetchProductsByCategory(selectedCategory);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Layout switch
        btnLayoutSwitch.setOnClickListener(v -> toggleLayout());

        // Sort and filter
        btnSort.setOnClickListener(v -> showSortDialog());
        btnFilter.setOnClickListener(v -> showFilterDialog());

        // Search button
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                // Launch a search activity or show a search bar
                Intent intent = new Intent(getContext(), com.example.easyshop.SearchActivity.class);
                startActivity(intent);
            });
        }

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        return view;
    }

    private void fetchCategories() {
        progressBar.setVisibility(View.VISIBLE);
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryTabLayout.removeAllTabs();
                categoryList.add(new Category("All", "")); // "All" category
                categoryTabLayout.addTab(categoryTabLayout.newTab().setText("All"));
                for (DataSnapshot catSnap : snapshot.getChildren()) {
                    Category category = catSnap.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                        categoryTabLayout.addTab(categoryTabLayout.newTab().setText(category.getName()));
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                fetchAllProducts();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load categories.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchAllProducts() {
        progressBar.setVisibility(View.VISIBLE);
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(snapshot.getKey());
                        productList.add(product);
                    }
                }
                productGridAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchProductsByCategory(String category) {
        progressBar.setVisibility(View.VISIBLE);
        if ("All".equals(category)) {
            fetchAllProducts();
            return;
        }
        productsRef.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Product product = snapshot.getValue(Product.class);
                            if (product != null) {
                                product.setProductId(snapshot.getKey());
                                productList.add(product);
                            }
                        }
                        productGridAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void selectTabByCategory(String categoryName) {
        for (int i = 0; i < categoryTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = categoryTabLayout.getTabAt(i);
            if (tab != null && tab.getText() != null && tab.getText().toString().equals(categoryName)) {
                tab.select();
                break;
            }
        }
    }

    private void toggleLayout() {
        isGrid = !isGrid;
        if (isGrid) {
            rvShopProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
            btnLayoutSwitch.setImageResource(R.drawable.ic_grid);
        } else {
            rvShopProducts.setLayoutManager(new LinearLayoutManager(getContext()));
            btnLayoutSwitch.setImageResource(R.drawable.ic_list);
        }
        productGridAdapter.setGrid(isGrid);
        productGridAdapter.notifyDataSetChanged();
    }

    private void showSortDialog() {
        // You can use a Dialog or BottomSheetDialog here for sort options
        // For simplicity, here is a stub
        Toast.makeText(getContext(), "Sort dialog coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void showFilterDialog() {
        // You can use a Dialog or BottomSheetDialog here for filter options
        // For simplicity, here is a stub
        Toast.makeText(getContext(), "Filter dialog coming soon!", Toast.LENGTH_SHORT).show();
    }
}