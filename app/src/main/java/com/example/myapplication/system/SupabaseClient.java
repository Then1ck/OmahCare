package com.example.myapplication.system;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseClient {
    private static OkHttpClient client = new OkHttpClient();

    public static String uploadImage(Context context, File imageFile, String uploadPath) throws IOException {
        // 🔹 Replace with YOUR Supabase project URL + anon key
        String supabaseUrl = "https://jwbsadoueaqttgkfywim.supabase.co";
        String supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp3YnNhZG91ZWFxdHRna2Z5d2ltIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTkzNzE2NTgsImV4cCI6MjA3NDk0NzY1OH0.QLB0crIjaueAnD9P2QggL3RT_Ag58hljNJ7JDvhpQJ4";

        // 🔹 Use your bucket name (create one if not already created in Supabase Storage)
        String bucketName = "article";

        // Build the upload URL
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + uploadPath;

        // Prepare the file body
        MediaType mediaType = MediaType.parse("image/*");
        RequestBody body = RequestBody.create(imageFile, mediaType);

        // HTTP request
        Request request = new Request.Builder()
                .url(uploadUrl)
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", "image/*")
                .put(body)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            String responseBody = response.body() != null ? response.body().string() : "empty";
            throw new IOException("Upload failed: " + response + ", body: " + responseBody);
        }

        // Return the public URL (only works if the bucket is set to public)
        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + uploadPath;
    }
}
