package com.example.easyshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.easyshop.MainActivity;
import com.example.easyshop.OrderHistoryActivity;
import com.example.easyshop.PaymentMethodsActivity;
import com.example.easyshop.PromocodesActivity;
import com.example.easyshop.MyReviewsActivity;
import com.example.easyshop.R;
import com.example.easyshop.SettingsActivity;
import com.example.easyshop.ShippingAddressesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView ivProfilePicture;
    private TextView tvProfileName, tvProfileEmail;
    private LinearLayout rowMyOrders, rowShippingAddresses, rowPaymentMethods, rowPromocodes, rowMyReviews, rowSettings;
    private MaterialButton btnLogout;
    private ImageView ivProfileSearch;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);

        rowMyOrders = view.findViewById(R.id.row_my_orders);
        rowShippingAddresses = view.findViewById(R.id.row_shipping_addresses);
        rowPaymentMethods = view.findViewById(R.id.row_payment_methods);
        rowPromocodes = view.findViewById(R.id.row_promocodes);
        rowMyReviews = view.findViewById(R.id.row_my_reviews);
        rowSettings = view.findViewById(R.id.row_settings);

        btnLogout = view.findViewById(R.id.btn_logout);
        ivProfileSearch = view.findViewById(R.id.iv_profile_search);

        mAuth = FirebaseAuth.getInstance();
        loadUserData();

        rowMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
        });

        rowShippingAddresses.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ShippingAddressesActivity.class));
        });

        rowPaymentMethods.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PaymentMethodsActivity.class));
        });

        rowPromocodes.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PromocodesActivity.class));
        });

        rowMyReviews.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyReviewsActivity.class));
        });

        rowSettings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        ivProfileSearch.setOnClickListener(v -> {
            // TODO: Add search functionality if needed
        });

        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).logoutUser();
            } else {
                mAuth.signOut();
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                // Optionally redirect to login
            }
        });

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvProfileEmail.setText(currentUser.getEmail());

            // Try to fetch name and photo from Firebase Database (users/uid), fallback to FirebaseUser if not found
            String uid = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                    if (name != null && !name.isEmpty()) {
                        tvProfileName.setText(name);
                    } else if (currentUser.getDisplayName() != null) {
                        tvProfileName.setText(currentUser.getDisplayName());
                    } else {
                        tvProfileName.setText("User");
                    }

                    if (photoUrl != null && !photoUrl.isEmpty() && getContext() != null) {
                        Glide.with(getContext())
                                .load(photoUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .into(ivProfilePicture);
                    } else if (currentUser.getPhotoUrl() != null && getContext() != null) {
                        Glide.with(getContext())
                                .load(currentUser.getPhotoUrl())
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .into(ivProfilePicture);
                    } else {
                        ivProfilePicture.setImageResource(R.drawable.placeholder_image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load profile info.", Toast.LENGTH_SHORT).show();
                    // fallback to FirebaseUser only
                    if (currentUser.getDisplayName() != null) {
                        tvProfileName.setText(currentUser.getDisplayName());
                    } else {
                        tvProfileName.setText("User");
                    }
                    if (currentUser.getPhotoUrl() != null && getContext() != null) {
                        Glide.with(getContext())
                                .load(currentUser.getPhotoUrl())
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .into(ivProfilePicture);
                    } else {
                        ivProfilePicture.setImageResource(R.drawable.placeholder_image);
                    }
                }
            };
            userRef.addValueEventListener(userListener);
        } else {
            tvProfileName.setText("User");
            tvProfileEmail.setText("");
            ivProfilePicture.setImageResource(R.drawable.placeholder_image);
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }
}