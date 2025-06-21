package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.easyshop.fragments.BagFragment;
import com.example.easyshop.fragments.FavoritesFragment;
import com.example.easyshop.fragments.HomeFragment;
import com.example.easyshop.fragments.ProfileFragment;
import com.example.easyshop.fragments.ShopFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navShop, navBag, navFavorites, navProfile;
    private ImageView iconHome, iconShop, iconBag, iconFavorites, iconProfile;
    private TextView textHome, textShop, textBag, textFavorites, textProfile;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase and Google Sign-In
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Custom Bottom Nav Bar
        navHome = findViewById(R.id.nav_home);
        navShop = findViewById(R.id.nav_shop);
        navBag = findViewById(R.id.nav_bag);
        navFavorites = findViewById(R.id.nav_favorites);
        navProfile = findViewById(R.id.nav_profile);

        iconHome = findViewById(R.id.icon_home);
        iconShop = findViewById(R.id.icon_shop);
        iconBag = findViewById(R.id.icon_bag);
        iconFavorites = findViewById(R.id.icon_favorites);
        iconProfile = findViewById(R.id.icon_profile);

        textHome = findViewById(R.id.text_home);
        textShop = findViewById(R.id.text_shop);
        textBag = findViewById(R.id.text_bag);
        textFavorites = findViewById(R.id.text_favorites);
        textProfile = findViewById(R.id.text_profile);

        navHome.setOnClickListener(v -> { selectTab(0); animateTab(navHome); });
        navShop.setOnClickListener(v -> { selectTab(1); animateTab(navShop); });
        navBag.setOnClickListener(v -> { selectTab(2); animateTab(navBag); });
        navFavorites.setOnClickListener(v -> { selectTab(3); animateTab(navFavorites); });
        navProfile.setOnClickListener(v -> { selectTab(4); animateTab(navProfile); });

        // Default fragment
        if (savedInstanceState == null) {
            selectTab(0);
        }
    }

    // Tab selection logic
    private void selectTab(int tab) {
        @ColorInt int selected = getColor(R.color.bottom_nav_selected);
        @ColorInt int unselected = getColor(R.color.bottom_nav_unselected);

        iconHome.setColorFilter(unselected);
        textHome.setTextColor(unselected);
        iconShop.setColorFilter(unselected);
        textShop.setTextColor(unselected);
        iconBag.setColorFilter(unselected);
        textBag.setTextColor(unselected);
        iconFavorites.setColorFilter(unselected);
        textFavorites.setTextColor(unselected);
        iconProfile.setColorFilter(unselected);
        textProfile.setTextColor(unselected);

        Fragment selectedFragment = null;
        switch (tab) {
            case 0:
                iconHome.setColorFilter(selected);
                textHome.setTextColor(selected);
                selectedFragment = new HomeFragment();
                break;
            case 1:
                iconShop.setColorFilter(selected);
                textShop.setTextColor(selected);
                selectedFragment = new ShopFragment();
                break;
            case 2:
                iconBag.setColorFilter(selected);
                textBag.setTextColor(selected);
                selectedFragment = new BagFragment();
                break;
            case 3:
                iconFavorites.setColorFilter(selected);
                textFavorites.setTextColor(selected);
                selectedFragment = new FavoritesFragment();
                break;
            case 4:
                iconProfile.setColorFilter(selected);
                textProfile.setTextColor(selected);
                selectedFragment = new ProfileFragment();
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    }

    // Animate tab selection
    private void animateTab(LinearLayout tab) {
        tab.animate()
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(120)
                .withEndAction(() -> tab.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start())
                .start();
    }

    /**
     * Signs the user out from both Firebase Authentication and Google Sign-In,
     * then navigates back to the LoginActivity.
     */
    public void logoutUser() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(MainActivity.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}