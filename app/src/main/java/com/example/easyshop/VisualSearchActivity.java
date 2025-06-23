package com.example.easyshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VisualSearchActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_CROP_IMAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_search);

        findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            startActivity(new Intent(this, CapturePhotoActivity.class));
        });

        findViewById(R.id.btn_upload_image).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            CropImageActivity.start(this, data.getData(), REQUEST_CROP_IMAGE);
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK && data != null) {
            String resultUriStr = data.getStringExtra(CropImageActivity.EXTRA_RESULT_URI);
            if (resultUriStr != null) {
                Uri croppedUri = Uri.parse(resultUriStr);
                VisualSearchFindingActivity.start(this, croppedUri);
            }
        }
    }
}