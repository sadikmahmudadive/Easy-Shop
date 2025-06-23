package com.example.easyshop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class CapturePhotoActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private File photoFile;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) startCamera();
                else finish();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_photo);

        previewView = findViewById(R.id.previewView);
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        findViewById(R.id.btn_capture).setOnClickListener(v -> takePhoto());
        // Add logic for flash and flip if needed
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) { e.printStackTrace(); }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        photoFile = new File(getCacheDir(), "captured_photo_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri photoUri = Uri.fromFile(photoFile);
                CropImageActivity.start(CapturePhotoActivity.this, photoUri, 200); // 200 is arbitrary, CropImageActivity handles uCrop
                finish();
            }
            @Override public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }
}