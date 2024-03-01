package com.example.finalproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    TextView tvMoveToSignUp;
    MotionLayout motionLayout;
    Button btSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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
    private void initComponents()
    {
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        motionLayout = findViewById(R.id.motion_layout_login);
        tvMoveToSignUp = findViewById(R.id.tvMoveToSignUp);
        tvMoveToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        btSignIn = findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateUsername() && validatePassword()){
                    checkUser();
                }
            }
        });

    }
    public Boolean validateUsername(){
        String username=etUsername.getText().toString();
        if(username.isEmpty()){
            etUsername.setError("User name is required");
            return false;
        } else {
            etUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String password=etPassword.getText().toString();
        if(password.isEmpty()){
            etPassword.setError("Password is required");
            return false;
        } else {
            etPassword.setError(null);
            return true;
        }
    }


    public void checkUser(){
        String username=etUsername.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        DatabaseReference reference;
        try {
            reference = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        } catch (Exception e) {
            etUsername.setError("User does not exist");
            return;
        }
        Query query=reference.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    etUsername.setError(null);
                    String passFromDB=snapshot.child(username).child("password").getValue(String.class);
                    if(passFromDB.equals(password)){
                        etUsername.setError(null);
                        getApplicationContext();
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("current_username",username);
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

            }
        });
    }
}