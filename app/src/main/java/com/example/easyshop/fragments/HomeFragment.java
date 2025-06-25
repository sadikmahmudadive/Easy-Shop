package com.example.easyshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.adapters.ProductAdapter;
import com.example.easyshop.models.Product;
import com.example.easyshop.ProductDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvSale, rvNew;
    private ProductAdapter saleAdapter, newAdapter;
    private ImageView imgHeader;
    private TextView tvHeaderTitle, tvSaleViewAll, tvNewViewAll;

    private final List<Product> saleProducts = new ArrayList<>();
    private final List<Product> newProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        imgHeader = v.findViewById(R.id.img_header);
        tvHeaderTitle = v.findViewById(R.id.tv_header_title);

        // Set header image and title (static, or you could fetch from Firebase if you want)
        Glide.with(this)
                .load(R.drawable.home_header)
                .into(imgHeader);
        tvHeaderTitle.setText("Street clothes");

        rvSale = v.findViewById(R.id.rv_sale);
        rvNew = v.findViewById(R.id.rv_new);

        rvSale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        saleAdapter = new ProductAdapter(saleProducts, true, product -> openProductDetails(product));
        newAdapter = new ProductAdapter(newProducts, false, product -> openProductDetails(product));

        rvSale.setAdapter(saleAdapter);
        rvNew.setAdapter(newAdapter);

        tvSaleViewAll = v.findViewById(R.id.tv_sale_view_all);
        tvNewViewAll = v.findViewById(R.id.tv_new_view_all);

        tvSaleViewAll.setOnClickListener(view -> {
            // TODO: Open sale all products screen
        });
        tvNewViewAll.setOnClickListener(view -> {
            // TODO: Open new all products screen
        });

        fetchProductsFromFirebase();

        return v;
    }

    private void fetchProductsFromFirebase() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saleProducts.clear();
                newProducts.clear();

                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    Product product = productSnap.getValue(Product.class);
                    if (product == null) continue;

                    // Add sale products (if there's a discount)
                    int oldPrice = 0, price = 0;
                    try {
                        oldPrice = Integer.parseInt(product.getOldPrice());
                        price = Integer.parseInt(product.getPrice());
                    } catch (Exception ignored) {}

                    if (oldPrice > price) {
                        int discount = (int) (((float) (oldPrice - price) / oldPrice) * 100);
                        product.setDiscountText("-" + discount + "%");
                        product.setNew(false);
                        saleProducts.add(product);
                    }

                    // Add new products (if isNew flag or your own logic)
                    // Here: mark as new if reviewCount <= 5 (demo logic)
                    if (product.getReviewCount() <= 5) {
                        product.setNew(true);
                        product.setDiscountText(null);
                        newProducts.add(product);
                    }
                }
                saleAdapter.notifyDataSetChanged();
                newAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to fetch products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openProductDetails(Product product) {
        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
        intent.putExtra("productId", product.getProductId());
        // If needed, you can pass the entire Product object by making it Parcelable/Serializable
        startActivity(intent);
    }
}