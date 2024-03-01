package com.example.finalproject.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.Domains.Room;
import com.example.finalproject.R;

public class ActiveGameActivity extends AppCompatActivity {
    ImageView ivBack;
    TextView tvGameTitle;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_game);

        initComponent();
    }
    private void initComponent()
    {
        tvGameTitle = findViewById(R.id.tvGameTitle);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Room.getCurrentRoom(getApplicationContext(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                Log.d("TAG", "received room name: " + room.getName());
                if (room != null) {
                    tvGameTitle.setText(room.getName());
                } else tvGameTitle.setText("null");
            }
        });
    }
}
