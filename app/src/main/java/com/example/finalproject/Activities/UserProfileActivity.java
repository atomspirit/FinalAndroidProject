package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.R;

public class UserProfileActivity extends AppCompatActivity {

    TextView tvUsername, tvUsernameHeadline;
    ImageView ivUserIcon, ivBackIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        User.createUserFromUsername(getIntent().getStringExtra("username"), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                initComponent(user);
            }
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initComponent(User user){
        tvUsername = findViewById(R.id.tvUsername);
        tvUsernameHeadline = findViewById(R.id.tvUsernameHeadline);
        ivUserIcon = findViewById(R.id.ivUserIcon);

        tvUsername.setText(user.getUsername());
        tvUsernameHeadline.setText(user.getUsername());
        //ivUserIcon.setImageResource(user.getPic()); TODO

        ivBackIcon = findViewById(R.id.ivBack);
        ivBackIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        ivBackIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.primaryPressed));
                        finish();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change text color back on release
                        ivBackIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.primary));


                        return true;
                }
                return false;
            }
        });
    }
}

