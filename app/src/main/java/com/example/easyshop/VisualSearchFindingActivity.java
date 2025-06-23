package com.example.easyshop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VisualSearchFindingActivity extends AppCompatActivity {
    private static final String EXTRA_IMAGE_URI = "image_uri";

    public static void start(Context context, Uri imageUri) {
        Intent intent = new Intent(context, VisualSearchFindingActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri.toString());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_search_finding);

        Uri imageUri = Uri.parse(getIntent().getStringExtra(EXTRA_IMAGE_URI));

        // Show "Visual search is coming soon!" toast immediately
        Toast.makeText(this, "Visual search is coming soon!", Toast.LENGTH_SHORT).show();

        // Simulate network call delay, then upload (you can comment out this block if you want only the toast)
        /*
        new Handler().postDelayed(() -> {
            uploadImageToBackend(imageUri);
        }, 1000);
        */
    }

    // You can leave this method as it is or remove if not used
    /*
    private void uploadImageToBackend(Uri imageUri) {
        ImageUploader.uploadImage(this, imageUri, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(VisualSearchFindingActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
                finish();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        // Show results (start results activity or update UI)
                        Toast.makeText(VisualSearchFindingActivity.this, "Upload success!", Toast.LENGTH_SHORT).show();
                        // TODO: Parse and show results here
                    } else {
                        Toast.makeText(VisualSearchFindingActivity.this, "Upload error", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
            }
        });
    }
    */
}