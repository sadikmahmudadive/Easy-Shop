package com.example.easyshop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.R;
import com.example.easyshop.adapters.FavoriteAdapter;
import com.example.easyshop.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";

    private RecyclerView rvFavorites;
    private FavoriteAdapter favoriteAdapter;
    private List<Product> favoriteProductList;
    private TextView tvNoFavorites;
    private DatabaseReference favoritesRef;
    private DatabaseReference productsRef;
    private ValueEventListener favoritesListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Initialize UI
        rvFavorites = view.findViewById(R.id.rv_favorite_items);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteProductList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(getContext(), favoriteProductList);
        rvFavorites.setAdapter(favoriteAdapter);

        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(userId);
            productsRef = FirebaseDatabase.getInstance().getReference("products");
            attachFavoritesListener();
        } else {
            // Handle user not logged in case
            Toast.makeText(getContext(), "Please log in to see favorites", Toast.LENGTH_SHORT).show();
            updateUI();
        }

        return view;
    }

    /**
     * Attaches a listener to the user's favorites node to get a list of product IDs.
     * This listener will react to any additions or removals.
     */
    private void attachFavoritesListener() {
        if (favoritesRef == null) return;
        favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favoriteIds = new ArrayList<>();
                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    favoriteIds.add(idSnapshot.getKey());
                }
                fetchFavoriteProducts(favoriteIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching favorite IDs: " + error.getMessage());
                Toast.makeText(getContext(), "Could not load favorites.", Toast.LENGTH_SHORT).show();
            }
        };
        favoritesRef.addValueEventListener(favoritesListener);
    }

    /**
     * Given a list of product IDs, this method fetches the full product details
     * for each one from the 'products' node in Firebase.
     */
    private void fetchFavoriteProducts(List<String> favoriteIds) {
        favoriteProductList.clear(); // Clear the list before repopulating
        if (favoriteIds.isEmpty()) {
            updateUI(); // Update UI to show "no favorites" message
            return;
        }

        for (String id : favoriteIds) {
            if (id == null || productsRef == null) continue;
            productsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(snapshot.getKey());
                        favoriteProductList.add(product);
                    }
                    // This ensures the UI is updated after all items have been processed
                    if (favoriteProductList.size() == favoriteIds.size()) {
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching product details for ID " + id + ": " + error.getMessage());
                }
            });
        }
    }

    /**
     * Updates the visibility of the RecyclerView and the "no favorites" text view
     * based on whether the favorites list is empty.
     */
    private void updateUI() {
        favoriteAdapter.notifyDataSetChanged();
        if (favoriteProductList.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Crucial: Detach the Firebase listener to prevent memory leaks and unwanted background activity
        // when the fragment's view is destroyed.
        if (favoritesRef != null && favoritesListener != null) {
            favoritesRef.removeEventListener(favoritesListener);
        }
    }
}