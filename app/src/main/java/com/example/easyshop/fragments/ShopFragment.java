package com.example.easyshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private RecyclerView rvShopProducts, rvCategoryCards;
    private ProductGridAdapter productGridAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private DatabaseReference productsRef;
    private LinearLayout categoryTabContainer;

    private boolean isGrid = true;
    private ImageButton btnLayoutSwitch;
    private ImageButton btnFilter;
    private ImageButton btnSort;
    private TextView tvSortLabel;
    private ImageButton btnLogout;

    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Init UI
        progressBar = view.findViewById(R.id.progress_bar_shop);
        rvShopProducts = view.findViewById(R.id.rv_shop_products);
        rvCategoryCards = view.findViewById(R.id.rv_category_cards);
        categoryTabContainer = view.findViewById(R.id.category_tab_container);

        btnLayoutSwitch = view.findViewById(R.id.btn_layout_switch);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnSort = view.findViewById(R.id.btn_sort);
        tvSortLabel = view.findViewById(R.id.tv_sort_label);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Logout button handler
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Example: sign out with FirebaseAuth, show message, and/or navigate
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), getString(R.string.logout), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to login screen if needed
            });
        }

        // Default: grid layout for products
        rvShopProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productList = new ArrayList<>();
        productGridAdapter = new ProductGridAdapter(getContext(), productList, isGrid);
        rvShopProducts.setAdapter(productGridAdapter);

        // Setup categories
        categoryList = Category.getDefaultCategories(); // ["New", "Clothes", "Shoes", "Accessories"]
        categoryAdapter = new CategoryAdapter(categoryList, category -> fetchProductsByCategory(category.getName()));
        rvCategoryCards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategoryCards.setAdapter(categoryAdapter);

        setupCategoryTabs();

        productsRef = FirebaseDatabase.getInstance().getReference("products");
        fetchAllProducts();

        btnLayoutSwitch.setOnClickListener(v -> toggleLayout());
        btnSort.setOnClickListener(v -> showSortDialog());
        btnFilter.setOnClickListener(v -> showFilterDialog());

        return view;
    }

    private void setupCategoryTabs() {
        categoryTabContainer.removeAllViews();
        for (Category cat : categoryList) {
            TextView tab = new TextView(getContext());
            tab.setText(cat.getName());
            tab.setBackgroundResource(R.drawable.tab_selector);
            tab.setPadding(48, 20, 48, 20);
            tab.setTextColor(getResources().getColorStateList(R.color.primary_red, null));
            tab.setTextSize(16f);
            tab.setOnClickListener(v -> fetchProductsByCategory(cat.getName()));
            categoryTabContainer.addView(tab);
        }
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
        // Show a bottom sheet or dialog with sort options
        // On selection, update tvSortLabel and re-sort productList, then notify adapter
    }

    private void showFilterDialog() {
        // Show a filter bottom sheet
        // On apply, filter productList and notify adapter
    }
}