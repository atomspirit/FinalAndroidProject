package com.example.finalproject.Domains;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.finalproject.Activities.SignUpActivity;
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

public abstract class FirebaseManager {
    private static final String URL = "https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final String TAG = "firebaseManager";

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

    public static void uploadImageToFirebase(Uri imageUri,OnSuccessListener<String> onSuccessListener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("profile_pictures/" + UUID.randomUUID());

        imagesRef.putFile(imageUri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();
                                onSuccessListener.onSuccess(imageURL);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to upload image: " + e.getMessage());
            }
        });
    }
    public static void deleteImage(String imageURl) throws UnsupportedEncodingException {
        // Parse the URL to get the path
        Uri uri = Uri.parse(imageURl);
        String path = uri.getPath();

        if (path != null) {
            // The path includes a leading '/v0/b/<bucket>/o/', so we need to remove the leading '/o/'
            int index = path.indexOf("/o/");
            if (index != -1) {
                String decodedPath = path.substring(index + 3);

                // URL decode to handle special characters in paths
                decodedPath = Uri.decode(decodedPath);

                // Get a reference to the storage service using the default Firebase App
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storage.getReference().child(decodedPath);

            }
        }
    }

}
