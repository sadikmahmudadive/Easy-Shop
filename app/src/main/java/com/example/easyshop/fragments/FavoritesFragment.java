package com.example.easyshop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.R;
import com.example.easyshop.adapters.FavoriteAdapter;
import com.example.easyshop.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<Product> favouriteProducts = new ArrayList<>();
    private TextView emptyView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = root.findViewById(R.id.recycler_favourites);
        emptyView = root.findViewById(R.id.empty_favourites);

        adapter = new FavoriteAdapter(favouriteProducts, product -> removeFromFavourites(product));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadFavourites();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User not logged in
        } else {
            loadFavourites();
        }
    }

    private void loadFavourites() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("favourites")
                .child(userId);
        Log.d("FAV_FRAGMENT_DEBUG", "Loading favourites for user: " + userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouriteProducts.clear();
                int count = 0;
                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    Product p = productSnap.getValue(Product.class);
                    if (p != null) {
                        favouriteProducts.add(p);
                        Log.d("FAV_FRAGMENT_DEBUG", "Loaded: " + p.getProductId() + " | " + p.getName());
                        count++;
                    } else {
                        Log.w("FAV_FRAGMENT_DEBUG", "Null product encountered");
                    }
                }
                Log.d("FAV_FRAGMENT_DEBUG", "Total loaded: " + count);
                adapter.notifyDataSetChanged();
                if (count == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FAV_FRAGMENT_DEBUG", "Failed to load favourites: " + error.getMessage());
            }
        });
    }

    private void removeFromFavourites(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("favourites")
                .child(userId)
                .child(product.getProductId());
        ref.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("FAV_FRAGMENT_DEBUG", "Removed: " + product.getProductId()))
                .addOnFailureListener(e -> Log.e("FAV_FRAGMENT_DEBUG", "Failed to remove: " + product.getProductId()));
    }
}