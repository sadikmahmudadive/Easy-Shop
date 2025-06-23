package com.example.easyshop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.yalantis.ucrop.UCrop;
import java.io.File;

public class CropImageActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "extra_image_uri";
    public static final String EXTRA_RESULT_URI = "extra_result_uri";

    // Call this to start cropping from any Activity
    public static void start(Activity activity, Uri imageUri, int requestCode) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri sourceUri = null;
        if (getIntent() != null && getIntent().hasExtra(EXTRA_IMAGE_URI)) {
            sourceUri = Uri.parse(getIntent().getStringExtra(EXTRA_IMAGE_URI));
        }

        if (sourceUri != null) {
            Uri destUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
            UCrop.of(sourceUri, destUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(800, 800)
                    .start(this);
        } else {
            finish(); // Nothing to crop
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                Intent result = new Intent();
                result.putExtra(EXTRA_RESULT_URI, resultUri != null ? resultUri.toString() : null);
                setResult(RESULT_OK, result);
                finish();
            } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
                Throwable cropError = UCrop.getError(data);
                cropError.printStackTrace();
                setResult(RESULT_CANCELED);
                finish();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}