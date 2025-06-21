package com.example.easyshop.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.Review;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.*;

public class WriteReviewDialog extends DialogFragment {
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private LinearLayout photosContainer;
    private ImageView addPhotoImage;
    private MaterialButton sendReviewButton;
    private List<Uri> selectedImageUris = new ArrayList<>();

    // ActivityResultLauncher must be registered in onCreate (not on click)
    private ActivityResultLauncher<String[]> pickImageLauncher;

    public interface OnReviewSubmitListener {
        void onReviewSubmitted(Review review, List<String> photoUrls);
    }
    private OnReviewSubmitListener reviewSubmitListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnReviewSubmitListener) {
            reviewSubmitListener = (OnReviewSubmitListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        selectedImageUris.add(uri);
                        addPhotoPreview(uri);
                    }
                }
        );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_write_review, null, false);
        ratingBar = v.findViewById(R.id.dialog_rating_bar);
        reviewEditText = v.findViewById(R.id.dialog_edit_review);
        photosContainer = v.findViewById(R.id.dialog_photos_container);
        addPhotoImage = v.findViewById(R.id.dialog_add_photo);
        sendReviewButton = v.findViewById(R.id.button_send_review);

        addPhotoImage.setOnClickListener(view -> pickImage());

        sendReviewButton.setOnClickListener(view -> {
            float rating = ratingBar.getRating();
            String reviewText = reviewEditText.getText().toString().trim();
            if (rating == 0f) {
                Toast.makeText(getContext(), "Please select a rating.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(reviewText)) {
                Toast.makeText(getContext(), "Please enter your review.", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImagesAndSubmitReview(rating, reviewText);
        });

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);
        return dialog;
    }

    // Use the registered pickImageLauncher
    private void pickImage() {
        pickImageLauncher.launch(new String[]{"image/*"});
    }

    private void addPhotoPreview(Uri uri) {
        int px = (int) (64 * getResources().getDisplayMetrics().density);
        ImageView img = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px, px);
        params.setMargins(0, 0, 16, 0);
        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getContext()).load(uri).placeholder(R.drawable.placeholder_image).into(img);
        photosContainer.addView(img);
    }

    // Upload all images to Cloudinary, then submit review
    private void uploadImagesAndSubmitReview(float rating, String reviewText) {
        if (selectedImageUris.isEmpty()) {
            submitReview(rating, reviewText, new ArrayList<>());
            return;
        }
        List<String> uploadedUrls = new ArrayList<>();
        uploadImageRecursive(0, uploadedUrls, rating, reviewText);
    }

    private void uploadImageRecursive(int idx, List<String> uploadedUrls, float rating, String reviewText) {
        if (idx >= selectedImageUris.size()) {
            submitReview(rating, reviewText, uploadedUrls);
            return;
        }
        uploadToCloudinary(selectedImageUris.get(idx), url -> {
            if (url != null) {
                uploadedUrls.add(url);
            }
            uploadImageRecursive(idx + 1, uploadedUrls, rating, reviewText);
        });
    }

    // Upload a single image to Cloudinary (Unsigned)
    private void uploadToCloudinary(Uri imageUri, OnUploadedListener listener) {
        try {
            Context context = getContext();
            if (context == null) {
                listener.onUploaded(null);
                return;
            }
            InputStream is = context.getContentResolver().openInputStream(imageUri);
            byte[] bytes = getBytes(is);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "review_image.jpg", RequestBody.create(bytes, MediaType.parse("image/*")))
                    .addFormDataPart("upload_preset", "ShopEase")
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dhm0edatk/image/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {
                    listener.onUploaded(null);
                }
                @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String body = response.body().string();
                            JSONObject json = new JSONObject(body);
                            String url = json.getString("secure_url");
                            listener.onUploaded(url);
                        } else {
                            listener.onUploaded(null);
                        }
                    } catch (Exception e) {
                        listener.onUploaded(null);
                    }
                }
            });
        } catch (Exception e) {
            listener.onUploaded(null);
        }
    }

    private interface OnUploadedListener {
        void onUploaded(String url);
    }

    private static byte[] getBytes(InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void submitReview(float rating, String reviewText, List<String> photoUrls) {
        Review review = new Review();
        review.setUserName("Anonymous");
        review.setUserAvatarUrl(photoUrls.isEmpty() ? "" : photoUrls.get(0));
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setDate(System.currentTimeMillis());
        review.setPhotoUrls(photoUrls);

        if (reviewSubmitListener != null) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    reviewSubmitListener.onReviewSubmitted(review, photoUrls);
                    dismiss();
                });
            }
        }
    }
}