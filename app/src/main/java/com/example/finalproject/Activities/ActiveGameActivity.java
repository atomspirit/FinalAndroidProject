package com.example.finalproject.Activities;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.Adapters.VPAdapter;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Fragments.GameFragment;
import com.example.finalproject.Fragments.MainNFCFragment;
import com.example.finalproject.Fragments.OptionsFragment;
import com.example.finalproject.Fragments.ParticipantsListFragment;
import com.example.finalproject.Fragments.ProfileFragment;
import com.example.finalproject.Fragments.RoomInfoFragment;
import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ActiveGameActivity extends AppCompatActivity {
    ImageView ivBack;
    TextView tvGameTitle;
    Room room;
    ViewPager2 viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_game);

        initComponent();
        setupTabLayout();
    }
    private void initComponent()
    {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tvGameTitle = findViewById(R.id.tvGameTitle);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Create or access the shared preferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);

        // Get an editor to edit SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put the code the room code into the SharedPreferences with key "current_room"
        editor.putString("current_room", getIntent().getStringExtra("room_code"));

        // Apply the changes
        editor.apply();


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
    private void setupTabLayout()
    {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        VPAdapter vpAdapter = new VPAdapter(this);
        vpAdapter.addFragment(new MainNFCFragment(), "Main");
        vpAdapter.addFragment(new ParticipantsListFragment(), "Players");
        vpAdapter.addFragment(new RoomInfoFragment(), "Info");
        viewPager.setAdapter(vpAdapter);

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getFragmentTitle(position));
        }).attach();
    }

}
