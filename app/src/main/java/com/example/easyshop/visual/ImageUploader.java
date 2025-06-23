package com.example.easyshop.visual;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import okhttp3.*;

public class ImageUploader {
    public static void uploadImage(Context context, Uri imageUri, Callback callback) {
        File file = new File(imageUri.getPath());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/jpeg")))
                .build();

        Request request = new Request.Builder()
                .url("https://your-backend-url.com/visual-search") // <-- Change to your backend
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}