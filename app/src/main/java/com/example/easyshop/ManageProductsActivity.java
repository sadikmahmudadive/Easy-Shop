package com.example.easyshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyshop.adapters.ProductAdapterAdmin;
import com.example.easyshop.models.Product;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import okhttp3.*;

public class ManageProductsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_FIRST = 101;
    private static final int PICK_IMAGE_SECOND = 102;
    private static final int PICK_IMAGE_THIRD = 103;

    private EditText etProductTitle, etBrand, etDescription, etPrice, etOldPrice, etNewPrice, etSearchProduct;
    private Spinner spinnerCategory;
    private ImageView imgFirst, imgSecond, imgThird;
    private Button btnUploadFirst, btnUploadSecond, btnUploadThird, btnPublish, btnSaveDraft, btnLoadMore;
    private ImageButton btnBack, btnMore, btnProductsGrid, btnProductsList;

    private Uri uriFirst, uriSecond, uriThird;
    private String uploadedUrlFirst, uploadedUrlSecond, uploadedUrlThird;

    private ProgressDialog progressDialog;

    private final String CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/ddfpt0htt/image/upload";
    private final String UPLOAD_PRESET = "EasyShop";

    private String productId = null;
    private boolean isEditMode = false;

    private RecyclerView rvProducts;
    private ProductAdapterAdmin adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_products);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        setListeners();
        setupCategorySpinner();
        setupRecycler();
        loadProductsFromFirebase();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("productId")) {
            productId = intent.getStringExtra("productId");
            isEditMode = true;
            loadProductData(productId);
        }
    }

    private void bindViews() {
        etProductTitle = findViewById(R.id.et_product_title);
        etBrand = findViewById(R.id.et_brand);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etOldPrice = findViewById(R.id.et_old_price);
        etNewPrice = findViewById(R.id.et_new_price);
        spinnerCategory = findViewById(R.id.spinner_category);

        imgFirst = findViewById(R.id.img_first);
        imgSecond = findViewById(R.id.img_second);
        imgThird = findViewById(R.id.img_third);

        btnUploadFirst = findViewById(R.id.btn_upload_first);
        btnUploadSecond = findViewById(R.id.btn_upload_second);
        btnUploadThird = findViewById(R.id.btn_upload_third);

        btnPublish = findViewById(R.id.btn_publish);
        btnSaveDraft = findViewById(R.id.btn_save_draft);
        btnBack = findViewById(R.id.btn_back);
        btnMore = findViewById(R.id.btn_more);

        etSearchProduct = findViewById(R.id.et_search_product);
        rvProducts = findViewById(R.id.rv_products);
        btnLoadMore = findViewById(R.id.btn_load_more);

        btnProductsGrid = findViewById(R.id.btn_products_grid);
        btnProductsList = findViewById(R.id.btn_products_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnUploadFirst.setOnClickListener(v -> pickImage(PICK_IMAGE_FIRST));
        btnUploadSecond.setOnClickListener(v -> pickImage(PICK_IMAGE_SECOND));
        btnUploadThird.setOnClickListener(v -> pickImage(PICK_IMAGE_THIRD));

        btnPublish.setOnClickListener(v -> {
            if (isEditMode) {
                updateProduct(false);
            } else {
                saveProduct(false);
            }
        });

        btnSaveDraft.setOnClickListener(v -> {
            if (isEditMode) {
                updateProduct(true);
            } else {
                saveProduct(true);
            }
        });

        btnMore.setOnClickListener(v -> showMoreOptions());

        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterAndDisplayProducts(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnLoadMore.setOnClickListener(v -> {
            currentPage++;
            filterAndDisplayProducts();
        });

        btnProductsGrid.setOnClickListener(v -> Toast.makeText(this, "Grid view not implemented", Toast.LENGTH_SHORT).show());
        btnProductsList.setOnClickListener(v -> Toast.makeText(this, "List view selected", Toast.LENGTH_SHORT).show());
    }

    private void setupCategorySpinner() {
        String[] categories = {"T-Shirt", "Man Pants", "Woman Pants", "Shirt", "Blouse", "Man Shoes", "Woman Shoes", "Tops", "Jumpsuits", "Accessories", "Hoodies", "Sweatshirts", "Coats", "Jackets", "Shorts", "Skirts", "Dresses"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            switch (requestCode) {
                case PICK_IMAGE_FIRST:
                    uriFirst = uri;
                    Picasso.get().load(uri).into(imgFirst);
                    break;
                case PICK_IMAGE_SECOND:
                    uriSecond = uri;
                    Picasso.get().load(uri).into(imgSecond);
                    break;
                case PICK_IMAGE_THIRD:
                    uriThird = uri;
                    Picasso.get().load(uri).into(imgThird);
                    break;
            }
        }
    }

    private void loadProductData(String productId) {
        progressDialog.setMessage("Loading product...");
        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(productId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        etProductTitle.setText(product.getTitle());
                        etBrand.setText(product.getBrand());
                        etDescription.setText(product.getDescription());
                        etPrice.setText(product.getPrice());
                        etOldPrice.setText(product.getOldPrice());
                        etNewPrice.setText(product.getNewPrice());

                        String category = product.getCategory();
                        if (category != null) {
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
                            int pos = adapter.getPosition(category);
                            if (pos >= 0) spinnerCategory.setSelection(pos);
                        }

                        List<String> imageUrls = product.getImageUrls();
                        if (imageUrls != null && imageUrls.size() > 0) {
                            Picasso.get().load(imageUrls.get(0)).into(imgFirst);
                            uploadedUrlFirst = imageUrls.get(0);
                        } else uploadedUrlFirst = null;
                        if (imageUrls != null && imageUrls.size() > 1) {
                            Picasso.get().load(imageUrls.get(1)).into(imgSecond);
                            uploadedUrlSecond = imageUrls.get(1);
                        } else uploadedUrlSecond = null;
                        if (imageUrls != null && imageUrls.size() > 2) {
                            Picasso.get().load(imageUrls.get(2)).into(imgThird);
                            uploadedUrlThird = imageUrls.get(2);
                        } else uploadedUrlThird = null;
                        uriFirst = uriSecond = uriThird = null;
                    }
                } else {
                    Toast.makeText(ManageProductsActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ManageProductsActivity.this, "Failed to load product", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveProduct(boolean isDraft) {
        String name = etProductTitle.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String price = etPrice.getText().toString().trim();
        String oldPrice = etOldPrice.getText().toString().trim();
        String newPrice = etNewPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(price) || uriFirst == null) {
            Toast.makeText(this, "Fill required fields and select at least the first image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        List<Uri> imageUris = new ArrayList<>();
        imageUris.add(uriFirst);
        if (uriSecond != null) imageUris.add(uriSecond);
        if (uriThird != null) imageUris.add(uriThird);

        uploadImagesToCloudinary(imageUris, (uploadedUrls) -> {
            String productId = "product" + System.currentTimeMillis();
            Product product = new Product(
                    productId,
                    name,
                    brand,
                    desc,
                    category,
                    price,
                    oldPrice,
                    newPrice,
                    uploadedUrls.size() > 0 ? uploadedUrls.get(0) : "",
                    uploadedUrls,
                    false,
                    false,
                    false,
                    0.0f,
                    0.0,
                    0,
                    null,
                    isDraft
            );
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(productId);
            ref.setValue(product).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(this, isDraft ? "Product saved as draft" : "Product published", Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateProduct(boolean isDraft) {
        String name = etProductTitle.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String price = etPrice.getText().toString().trim();
        String oldPrice = etOldPrice.getText().toString().trim();
        String newPrice = etNewPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Fill required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating...");
        progressDialog.show();

        List<String> finalImageUrls = new ArrayList<>();
        List<Uri> imageUrisToUpload = new ArrayList<>();
        if (uriFirst != null) {
            imageUrisToUpload.add(uriFirst);
        } else if (uploadedUrlFirst != null) {
            finalImageUrls.add(uploadedUrlFirst);
        }
        if (uriSecond != null) {
            imageUrisToUpload.add(uriSecond);
        } else if (uploadedUrlSecond != null) {
            finalImageUrls.add(uploadedUrlSecond);
        }
        if (uriThird != null) {
            imageUrisToUpload.add(uriThird);
        } else if (uploadedUrlThird != null) {
            finalImageUrls.add(uploadedUrlThird);
        }

        if (imageUrisToUpload.size() > 0) {
            uploadImagesToCloudinary(imageUrisToUpload, (uploadedUrls) -> {
                int urlIndex = 0;
                List<String> merged = new ArrayList<>();
                if (uriFirst != null) merged.add(uploadedUrls.get(urlIndex++));
                else if (uploadedUrlFirst != null) merged.add(uploadedUrlFirst);
                if (uriSecond != null) merged.add(uploadedUrls.get(urlIndex++));
                else if (uploadedUrlSecond != null) merged.add(uploadedUrlSecond);
                if (uriThird != null) merged.add(uploadedUrls.get(urlIndex));
                else if (uploadedUrlThird != null) merged.add(uploadedUrlThird);

                performProductUpdate(merged, name, brand, desc, category, price, oldPrice, newPrice, isDraft);
            });
        } else {
            List<String> merged = new ArrayList<>();
            if (uploadedUrlFirst != null) merged.add(uploadedUrlFirst);
            if (uploadedUrlSecond != null) merged.add(uploadedUrlSecond);
            if (uploadedUrlThird != null) merged.add(uploadedUrlThird);
            performProductUpdate(merged, name, brand, desc, category, price, oldPrice, newPrice, isDraft);
        }
    }

    private void performProductUpdate(List<String> imageUrls, String name, String brand, String desc, String category, String price, String oldPrice, String newPrice, boolean isDraft) {
        Product product = new Product(
                productId,
                name,
                brand,
                desc,
                category,
                price,
                oldPrice,
                newPrice,
                imageUrls.size() > 0 ? imageUrls.get(0) : "",
                imageUrls,
                false,
                false,
                false,
                0.0f,
                0.0,
                0,
                null,
                isDraft
        );
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(productId);
        ref.setValue(product).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, isDraft ? "Product updated as draft" : "Product updated", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        etProductTitle.setText("");
        etBrand.setText("");
        etDescription.setText("");
        etPrice.setText("");
        etOldPrice.setText("");
        etNewPrice.setText("");
        spinnerCategory.setSelection(0);
        imgFirst.setImageResource(R.drawable.placeholder_image);
        imgSecond.setImageResource(R.drawable.placeholder_image);
        imgThird.setImageResource(R.drawable.placeholder_image);
        uriFirst = uriSecond = uriThird = null;
        uploadedUrlFirst = uploadedUrlSecond = uploadedUrlThird = null;
        productId = null;
        isEditMode = false;
    }

    interface CloudinaryUploadCallback {
        void onComplete(List<String> urls);
    }

    private void uploadImagesToCloudinary(List<Uri> imageUris, CloudinaryUploadCallback callback) {
        List<String> uploadedUrls = new ArrayList<>();
        uploadImageToCloudinaryRecursive(imageUris, 0, uploadedUrls, callback);
    }

    private void uploadImageToCloudinaryRecursive(List<Uri> imageUris, int index, List<String> uploadedUrls, CloudinaryUploadCallback callback) {
        if (index >= imageUris.size()) {
            callback.onComplete(uploadedUrls);
            return;
        }
        Uri uri = imageUris.get(index);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imageData = baos.toByteArray();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg",
                            RequestBody.create(imageData, okhttp3.MediaType.parse("image/jpeg")))
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build();

            Request request = new Request.Builder()
                    .url(CLOUDINARY_URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(ManageProductsActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            String url = json.getString("secure_url");
                            uploadedUrls.add(url);
                            uploadImageToCloudinaryRecursive(imageUris, index + 1, uploadedUrls, callback);
                        } catch (JSONException e) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(ManageProductsActivity.this, "Image parse error", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(ManageProductsActivity.this, "Image upload error", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecycler() {
        adapter = new ProductAdapterAdmin(new ArrayList<>());
        adapter.setOnEditClickListener((product, position) -> {
            productId = product.getProductId();
            isEditMode = true;
            etProductTitle.setText(product.getTitle());
            etBrand.setText(product.getBrand());
            etDescription.setText(product.getDescription());
            etPrice.setText(product.getPrice());
            etOldPrice.setText(product.getOldPrice());
            etNewPrice.setText(product.getNewPrice());
            String category = product.getCategory();
            if (category != null) {
                ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
                int pos = spinnerAdapter.getPosition(category);
                if (pos >= 0) spinnerCategory.setSelection(pos);
            }
            List<String> imageUrls = product.getImageUrls();
            if (imageUrls != null && imageUrls.size() > 0) {
                Picasso.get().load(imageUrls.get(0)).into(imgFirst);
                uploadedUrlFirst = imageUrls.get(0);
            } else uploadedUrlFirst = null;
            if (imageUrls != null && imageUrls.size() > 1) {
                Picasso.get().load(imageUrls.get(1)).into(imgSecond);
                uploadedUrlSecond = imageUrls.get(1);
            } else uploadedUrlSecond = null;
            if (imageUrls != null && imageUrls.size() > 2) {
                Picasso.get().load(imageUrls.get(2)).into(imgThird);
                uploadedUrlThird = imageUrls.get(2);
            } else uploadedUrlThird = null;
            uriFirst = uriSecond = uriThird = null;
        });
        adapter.setOnDeleteClickListener((product, position) -> {
            confirmDeleteProduct(product.getProductId());
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
    }

    private void loadProductsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allProducts.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    allProducts.add(product);
                }
                currentPage = 1;
                filterAndDisplayProducts();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ManageProductsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAndDisplayProducts() {
        String query = etSearchProduct.getText().toString().trim().toLowerCase();
        filteredProducts.clear();
        for (Product product : allProducts) {
            String name = product.getTitle() != null ? product.getTitle().toLowerCase() : "";
            if (query.isEmpty() || name.contains(query)) {
                filteredProducts.add(product);
            }
        }
        int end = Math.min(currentPage * PAGE_SIZE, filteredProducts.size());
        // Pass a fresh copy to avoid ConcurrentModificationException
        adapter.updateData(new ArrayList<>(filteredProducts.subList(0, end)));
        btnLoadMore.setVisibility(end < filteredProducts.size() ? View.VISIBLE : View.GONE);
    }

    private void showMoreOptions() {
        PopupMenu popup = new PopupMenu(this, btnMore);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_manage_product, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_update) {
                if (isEditMode) {
                    updateProduct(false);
                } else {
                    Toast.makeText(this, "Not in edit mode", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (id == R.id.action_delete) {
                if (isEditMode) {
                    confirmDeleteProduct(productId);
                } else {
                    Toast.makeText(this, "Not in edit mode", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    private void confirmDeleteProduct(String prodId) {
        if (prodId == null) {
            Toast.makeText(this, "No product selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProduct(prodId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct(String prodId) {
        if (prodId == null) return;
        progressDialog.setMessage("Deleting...");
        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(prodId);
        ref.removeValue().addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}