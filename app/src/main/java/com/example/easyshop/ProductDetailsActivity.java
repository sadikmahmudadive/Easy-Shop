package com.example.easyshop;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.easyshop.adapters.ReviewsAdapter;
import com.example.easyshop.adapters.SimilarProductsAdapter;
import com.example.easyshop.dialogs.WriteReviewDialog;
import com.example.easyshop.models.BagProduct;
import com.example.easyshop.models.Product;
import com.example.easyshop.models.Review;
import com.example.easyshop.ui.SelectSizeBottomSheet;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity implements WriteReviewDialog.OnReviewSubmitListener {

    private ViewPager2 imageCarousel;
    private TextView productTitle, productBrand, productCategory, productPrice, productOldPrice, productRating, productReviewCount, productDescription;
    private MaterialButton favoriteButton;
    private Button addToCartButton;
    private RecyclerView similarProductsRecycler, reviewsRecycler;
    private List<Product> similarProductsList = new ArrayList<>();
    private List<Review> reviewList = new ArrayList<>();
    private SimilarProductsAdapter similarProductsAdapter;
    private ReviewsAdapter reviewsAdapter;
    private DatabaseReference productsRef, reviewsRef;
    private Product currentProduct;

    private MaterialButton writeReviewButton;
    private CheckBox checkboxWithPhoto;
    private ValueEventListener productListener;
    private String lastSimilarCategory;

    // Track selected size for cart (if needed)
    private String selectedSize = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize views
        imageCarousel = findViewById(R.id.product_image_carousel);
        productTitle = findViewById(R.id.product_title);
        productBrand = findViewById(R.id.product_brand);
        productCategory = findViewById(R.id.product_category);
        productPrice = findViewById(R.id.product_price);
        productOldPrice = findViewById(R.id.product_old_price);
        productRating = findViewById(R.id.product_rating);
        productReviewCount = findViewById(R.id.product_review_count);
        productDescription = findViewById(R.id.product_description);
        addToCartButton = findViewById(R.id.button_add_to_cart);
        favoriteButton = findViewById(R.id.button_favorite);
        similarProductsRecycler = findViewById(R.id.recycler_similar_products);
        reviewsRecycler = findViewById(R.id.recycler_reviews);
        writeReviewButton = findViewById(R.id.button_write_review);
        checkboxWithPhoto = findViewById(R.id.checkbox_with_photo);

        productOldPrice.setPaintFlags(productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        similarProductsAdapter = new SimilarProductsAdapter(similarProductsList, product -> openProductDetails(product));
        similarProductsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        similarProductsRecycler.setAdapter(similarProductsAdapter);

        reviewsAdapter = new ReviewsAdapter(this, reviewList);
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecycler.setAdapter(reviewsAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://easyshop-24640-default-rtdb.firebaseio.com/");
        productsRef = database.getReference("products");
        reviewsRef = database.getReference("reviews");

        // Support both "product_id" and "productId" for intent extra key
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) productId = getIntent().getStringExtra("productId");
        if (productId != null && !productId.isEmpty()) {
            fetchProduct(productId);
            fetchReviews(productId);
        } else {
            Toast.makeText(this, "No product ID supplied!", Toast.LENGTH_SHORT).show();
            finish();
        }

        addToCartButton.setOnClickListener(v -> {
            if (currentProduct == null) return;

            // If your product has sizes, let user select before adding
            if (currentProduct.getAvailableSizes() != null && !currentProduct.getAvailableSizes().isEmpty()) {
                SelectSizeBottomSheet sizeSheet = SelectSizeBottomSheet.newInstance(currentProduct);
                sizeSheet.setOnSizeSelectedListener(size -> {
                    selectedSize = size;
                    addToCart(currentProduct, selectedSize);
                });
                sizeSheet.show(getSupportFragmentManager(), "SelectSizeBottomSheetCart");
            } else {
                addToCart(currentProduct, null);
            }
        });

        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(v -> {
                if (currentProduct == null) return;
                SelectSizeBottomSheet sheet = SelectSizeBottomSheet.newInstance(currentProduct);
                sheet.setOnSizeSelectedListener(selectedSize -> {
                    currentProduct.setSelectedSize(selectedSize);
                    addToFavourites(currentProduct);
                });
                sheet.show(getSupportFragmentManager(), "SelectSizeBottomSheet");
            });
        }

        if (writeReviewButton != null) {
            writeReviewButton.setOnClickListener(v -> {
                WriteReviewDialog dialog = new WriteReviewDialog();
                dialog.show(getSupportFragmentManager(), "WriteReviewDialog");
            });
        }

        if (checkboxWithPhoto != null) {
            checkboxWithPhoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                reviewsAdapter.filterWithPhotos(isChecked);
            });
        }
    }

    // Make product click open new ProductDetailsActivity
    private void openProductDetails(Product product) {
        if (product != null && product.getProductId() != null) {
            if (currentProduct != null && product.getProductId().equals(currentProduct.getProductId())) {
                return; // Don't open if it's the same product
            }
            Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getProductId());
            startActivity(intent);
        }
    }

    private void fetchProduct(String productId) {
        DatabaseReference productRef = productsRef.child(productId);

        // Remove any previous listener to avoid duplication
        if (productListener != null) {
            productRef.removeEventListener(productListener);
        }

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentProduct = snapshot.getValue(Product.class);
                if (currentProduct != null) {
                    updateProductUI(currentProduct);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Product not found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to fetch product", Toast.LENGTH_SHORT).show();
            }
        };
        productRef.addValueEventListener(productListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productListener != null && currentProduct != null) {
            productsRef.child(currentProduct.getProductId()).removeEventListener(productListener);
        }
    }

    private void updateProductUI(Product product) {
        productTitle.setText(nonNull(product.getName()));
        productBrand.setText(nonNull(product.getBrand()));
        productCategory.setText(nonNull(product.getCategory()));
        productPrice.setText("$" + nonNull(product.getPrice()));
        productOldPrice.setText(product.getOldPrice() != null ? "$" + product.getOldPrice() : "");
        productRating.setText(
                product.getAvgRating() != null
                        ? String.format(Locale.US, "%.1f", product.getAvgRating())
                        : "0.0"
        );
        productReviewCount.setText("(" + (product.getReviewCount() != null ? product.getReviewCount() : 0) + ")");
        productDescription.setText(nonNull(product.getDescription()));

        List<String> imagesList = product.getImageUrls();
        List<String> fallbackList = new ArrayList<>();
        if (imagesList != null && !imagesList.isEmpty()) {
            imageCarousel.setAdapter(new ImageCarouselAdapter(imagesList));
        } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            fallbackList.add(product.getImageUrl());
            imageCarousel.setAdapter(new ImageCarouselAdapter(fallbackList));
        }
        if (favoriteButton != null) {
            favoriteButton.setIconResource(product.getIsFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        }

        // --- Similar products ---
        if (product.getCategory() != null && !product.getCategory().equals(lastSimilarCategory)) {
            lastSimilarCategory = product.getCategory();
            fetchSimilarProducts(product.getCategory());
        }
    }

    private void fetchSimilarProducts(String category) {
        productsRef.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                similarProductsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    String productId = product.getProductId();
                    String currentId = currentProduct != null ? currentProduct.getProductId() : null;
                    if (currentId == null || productId == null || !productId.equals(currentId)) {
                        similarProductsList.add(product);
                    }
                }
                similarProductsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to fetch similar products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviews(String productId) {
        reviewsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (review != null) reviewList.add(review);
                }
                reviewsAdapter.setReviews(reviewList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to fetch reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Add the product to user's cart in Firebase with selected size (and default qty 1).
     * Uses productId + "_" + size as cart item key, and sets productId in BagProduct.
     */
    private void addToCart(Product product, String size) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String productId = product.getProductId();
        String brand = product.getBrand();
        String description = product.getDescription();
        String category = product.getCategory();
        String color = product.getColor();
        String imageUrl = product.getImageUrl();
        String selectedSizeForCart = size != null ? size : product.getSize();
        int quantity = 1;
        int price = 0;
        try {
            price = (int) Math.round(Double.parseDouble(product.getPrice()));
        } catch (Exception e) {
            // handle parse error if needed
        }

        BagProduct bagProduct = new BagProduct(
                productId,
                product.getName(),
                brand != null ? brand : "",
                description != null ? description : "",
                category != null ? category : "",
                color != null ? color : "",
                selectedSizeForCart != null ? selectedSizeForCart : "",
                quantity,
                price,
                imageUrl != null ? imageUrl : ""
        );

        String cartItemId = productId + "_" + (selectedSizeForCart != null ? selectedSizeForCart : "");

        FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(userId)
                .child(cartItemId)
                .setValue(bagProduct)
                .addOnSuccessListener(unused ->
                        Toast.makeText(ProductDetailsActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProductDetailsActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                );
    }

    private void addToFavourites(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference("favourites")
                .child(userId)
                .child(product.getProductId())
                .setValue(product)
                .addOnSuccessListener(unused ->
                        Toast.makeText(ProductDetailsActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProductDetailsActivity.this, "Failed to add to favourites", Toast.LENGTH_SHORT).show()
                );
    }

    private String nonNull(String s) {
        return s == null ? "" : s;
    }

    private static class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {
        private final List<String> imageUrls;

        public ImageCarouselAdapter(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Glide.with(holder.imageView.getContext())
                    .load(imageUrls.get(position))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return imageUrls.size();
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_product);
            }
        }
    }

    @Override
    public void onReviewSubmitted(Review review, List<String> photoUrls) {
        if (currentProduct == null) return;
        String productId = currentProduct.getProductId();
        String reviewId = reviewsRef.child(productId).push().getKey();
        if (reviewId != null) {
            reviewsRef.child(productId).child(reviewId).setValue(review)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Review added", Toast.LENGTH_SHORT).show();
                        updateProductAvgRating(productId);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add review", Toast.LENGTH_SHORT).show());
        }
    }

    // Recalculate avg rating and count, and update product node
    private void updateProductAvgRating(String productId) {
        DatabaseReference _reviewsRef = FirebaseDatabase.getInstance("https://easyshop-24640-default-rtdb.firebaseio.com/")
                .getReference("reviews").child(productId);

        _reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                float total = 0f;
                for (DataSnapshot reviewSnap : snapshot.getChildren()) {
                    Review r = reviewSnap.getValue(Review.class);
                    if (r != null) {
                        total += r.getRating();
                        count++;
                    }
                }
                float avg = count > 0 ? total / count : 0f;
                DatabaseReference productRef = FirebaseDatabase.getInstance("https://easyshop-24640-default-rtdb.firebaseio.com/")
                        .getReference("products").child(productId);
                productRef.child("avgRating").setValue(avg);
                productRef.child("reviewCount").setValue(count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}