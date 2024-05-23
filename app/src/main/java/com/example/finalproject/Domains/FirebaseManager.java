package com.example.finalproject.Domains;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class FirebaseManager {
    private static final String URL = "https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/";

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
}
