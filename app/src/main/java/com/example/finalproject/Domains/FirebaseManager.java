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

    public interface DataCallback {
        void onDataReceived(String data);
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
}
