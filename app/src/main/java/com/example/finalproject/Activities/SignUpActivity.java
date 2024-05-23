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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etEmail, etBio;
    TextView tvMoveToSignIn;
    MotionLayout motionLayout;
    Button btSignUp;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponents();

        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initComponents()
    {
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
                if(Utilities.validateUsername(etUsername) && Utilities.validatePassword(etPassword)
                        && Utilities.validateEmail(etEmail) && Utilities.validateEditText(etBio, "Bio is required"))
                    signUp();
            }
        });

    }

    private void signUp()
    {
        databaseReference = FirebaseManager.getReference("users");
        if(databaseReference == null){
            return;
        }

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        User user = new User(username,password,email,bio);

        Query query = databaseReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etUsername.setError(null);
                if(snapshot.exists()){
                    etUsername.setError("Username already exists");
                    etUsername.requestFocus();
                }
                else {
                    databaseReference.child(username).setValue(user);
                    Toast.makeText(getApplicationContext(),"Register Successfully",Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("current_username",username);
                    editor.apply();

                    motionLayout.transitionToEnd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}