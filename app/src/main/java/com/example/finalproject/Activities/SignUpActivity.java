package com.example.finalproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etBio;
    private TextView tvMoveToSignIn;
    private MotionLayout motionLayout;
    private Button btSignUp;
    private DatabaseReference databaseReference;
    private ShapeableImageView ivProfilePicture;
    private String userImageURL;
    private Uri userImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponents();

        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                // No action needed here
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                // No action needed here
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                // No action needed here
            }
        });
    }

    /**
     * Initializes the UI components and sets their event listeners.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initComponents() {
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etBio);
        motionLayout = findViewById(R.id.motion_layout_login);
        tvMoveToSignIn = findViewById(R.id.tvMoveToSignIn);
        tvMoveToSignIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        tvMoveToSignIn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryPressed));
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change text color back on release
                        tvMoveToSignIn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                        return true;
                }
                return false;
            }
        });

        btSignUp = findViewById(R.id.btSignUp);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(getApplicationContext(), getCurrentFocus());
                if (Utilities.validateUsername(etUsername) && Utilities.validatePassword(etPassword)
                        && Utilities.validateEmail(etEmail) && Utilities.validateEditText(etBio, "Bio is required")) {

                    if (userImageUri == null) {
                        int imageResource = getApplicationContext().getResources().getIdentifier("@drawable/ic_default_user", null, getPackageName());
                        userImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + imageResource);
                    }
                    FirebaseManager.uploadImageToFirebase(userImageUri, new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            userImageURL = s;
                            signUp();
                        }
                    });
                }
            }
        });

        Button btAddPic = findViewById(R.id.btAddPic);
        btAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select A Photo"), 321);
            }
        });
        ivProfilePicture = findViewById(R.id.ivUserIcon);
    }

    /**
     * Signs up the user by adding their data to the database and handles profile picture uploading.
     */
    private void signUp() {
        databaseReference = FirebaseManager.getReference("users");
        if (databaseReference == null) {
            return;
        }

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        User user = new User(username, password, email, bio, userImageURL);

        Query query = databaseReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etUsername.setError(null);
                if (snapshot.exists()) {
                    etUsername.setError("Username already exists");
                    etUsername.requestFocus();
                } else {
                    databaseReference.child(username).setValue(user);
                    Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("current_username", username);
                    editor.apply();
                    motionLayout.transitionToEnd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && resultCode == RESULT_OK && data != null) {
            userImageUri = data.getData();
            if (userImageUri != null) {
                ivProfilePicture.setImageURI(userImageUri);
                ivProfilePicture.setBackground(null);
            }
        }
    }
}
