package com.example.finalproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

public class SignInActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvMoveToSignUp;
    private MotionLayout motionLayout;
    private Button btSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initComponents();
        checkIfUserAlreadySignedIn();

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
        motionLayout = findViewById(R.id.motion_layout_login);
        tvMoveToSignUp = findViewById(R.id.tvMoveToSignUp);
        tvMoveToSignUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        tvMoveToSignUp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryPressed));
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change text color back on release
                        tvMoveToSignUp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                        return true;
                }
                return false;
            }
        });

        btSignIn = findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(getApplicationContext(), getCurrentFocus());
                if (Utilities.validateUsername(etUsername) && Utilities.validatePassword(etPassword)) {
                    checkUser();
                }
            }
        });
    }

    /**
     * Checks if a user is already signed in. If so, navigates to the main activity.
     */
    private void checkIfUserAlreadySignedIn() {
        String currentUsername = getApplicationContext().getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_username", "");
        Log.d("SignInActivity", "Current user: " + currentUsername);
        if (!currentUsername.equals("")) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Checks the entered username and password against the database and logs in the user if the
     * credentials are correct.
     */
    public void checkUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseManager.getReference("users");
        if (reference == null) {
            etUsername.setError("User does not exist");
            return;
        }

        Query query = reference.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etUsername.setError(null);
                    String passFromDB = snapshot.child(username).child("password").getValue(String.class);
                    if (passFromDB.equals(password)) {
                        etUsername.setError(null);
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("current_username", username);
                        editor.apply();
                        motionLayout.transitionToEnd();
                    } else {
                        etPassword.setError("Wrong password");
                        etPassword.requestFocus();
                    }
                } else {
                    etUsername.setError("User does not exist");
                    etUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
