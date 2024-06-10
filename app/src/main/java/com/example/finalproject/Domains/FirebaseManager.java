package com.example.finalproject.Domains;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * Utility class to manage Firebase operations such as database reference retrieval and image uploading/deleting.
 */
public abstract class FirebaseManager {
    private static final String URL = "https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final String TAG = "firebaseManager";

    /**
     * Retrieves a database reference with the given path.
     *
     * @param path The path to the database reference.
     * @return DatabaseReference object corresponding to the specified path, or null if an error occurs.
     */
    public static DatabaseReference getReference(String path) {
        DatabaseReference reference;
        try {
            reference = FirebaseDatabase.getInstance(URL).getReference(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return reference;
    }

    /**
     * Uploads an image to Firebase Storage.
     *
     * @param imageUri           The URI of the image to upload.
     * @param onSuccessListener  Listener to handle success event, providing the uploaded image URL.
     */
    public static void uploadImageToFirebase(Uri imageUri, OnSuccessListener<String> onSuccessListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("profile_pictures/" + UUID.randomUUID());

        imagesRef.putFile(imageUri).addOnSuccessListener(
                taskSnapshot -> imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageURL = uri.toString();
                    onSuccessListener.onSuccess(imageURL);
                })).addOnFailureListener(e ->
                Log.e(TAG, "Failed to upload image: " + e.getMessage())
        );
    }

    /**
     * Deletes an image from Firebase Storage.
     *
     * @param imageURL The URL of the image to delete.
     * @throws UnsupportedEncodingException If URL decoding fails.
     */
    public static void deleteImage(String imageURL) throws UnsupportedEncodingException {
        Uri uri = Uri.parse(imageURL);
        String path = uri.getPath();

        if (path != null) {
            int index = path.indexOf("/o/");
            if (index != -1) {
                String decodedPath = path.substring(index + 3);
                decodedPath = Uri.decode(decodedPath);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                storage.getReference().child(decodedPath);
            }
        }
    }
}
