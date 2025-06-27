package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class AdminPanelActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view_admin);
        toolbar = findViewById(R.id.toolbar_admin);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set window insets for edge-to-edge
        View mainContent = findViewById(R.id.main_content_layout);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupDrawerHeader();
        setupNavigationMenu();

        // Removed main panel buttons as requested and to match your layout.
    }

    private void setupDrawerHeader() {
        View headerView = navView.getHeaderView(0);
        ImageView imgAdmin = headerView.findViewById(R.id.img_admin_avatar);
        TextView tvAdminName = headerView.findViewById(R.id.tv_admin_name);
        TextView tvAdminEmail = headerView.findViewById(R.id.tv_admin_email);

        // Fetch from Firebase user/profile for displaying name/email/photo
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvAdminEmail.setText(user.getEmail());
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    String photoUrl = snapshot.child("photoUrl").getValue(String.class);
                    if (name != null) {
                        tvAdminName.setText(name);
                    }
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(AdminPanelActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.ic_admin_avatar)
                                .into(imgAdmin);
                    } else {
                        imgAdmin.setImageResource(R.drawable.ic_admin_avatar);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    private void setupNavigationMenu() {
        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                // Already on dashboard, do nothing
                return true;
            } else if (id == R.id.nav_manage_products) {
                startActivity(new Intent(this, ManageProductsActivity.class));
            } else if (id == R.id.nav_manage_orders) {
                startActivity(new Intent(this, ManageOrdersActivity.class));
            } else if (id == R.id.nav_manage_users) {
                startActivity(new Intent(this, ManageUsersActivity.class));
            } else if (id == R.id.nav_manage_promos) {
                startActivity(new Intent(this, ManagePromosActivity.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, AdminStatisticsActivity.class));
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        });
    }
}