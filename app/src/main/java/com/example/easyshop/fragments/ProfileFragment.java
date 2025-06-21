package com.example.easyshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.easyshop.MainActivity;
import com.example.easyshop.OrderHistoryActivity;
import com.example.easyshop.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView ivProfilePicture;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private Button btnLogout;
    private MaterialCardView cardMyOrders;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        cardMyOrders = view.findViewById(R.id.card_my_orders);

        mAuth = FirebaseAuth.getInstance();

        loadUserData();

        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).logoutUser();
            }
        });

        cardMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvProfileName.setText(currentUser.getDisplayName());
            tvProfileEmail.setText(currentUser.getEmail());

            if (currentUser.getPhotoUrl() != null && getContext() != null) {
                Glide.with(getContext())
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivProfilePicture);
            }
        } else {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}