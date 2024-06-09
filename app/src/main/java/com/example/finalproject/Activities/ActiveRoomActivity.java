package com.example.finalproject.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.Adapters.VPAdapter;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Fragments.ArcadeFragment;
import com.example.finalproject.Fragments.ParticipantsListFragment;
import com.example.finalproject.Fragments.RoomInfoFragment;
import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ActiveRoomActivity extends AppCompatActivity {

    /** ImageView for the back button. */
    ImageView ivBack;

    /** TextView to display the game title. */
    TextView tvGameTitle;

    /** Room object representing the current game room. */
    Room room;

    /** ViewPager2 for managing tab navigation. */
    ViewPager2 viewPager;

    /** TabLayout for displaying tabs. */
    TabLayout tabLayout;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_game);

        initComponent();
        setupTabLayout();
    }

    /**
     * Initializes UI components and sets up event listeners.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initComponent() {
        tvGameTitle = findViewById(R.id.tvGameTitle);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change icon color on press
                        ivBack.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primaryPressed));
                        finish();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change icon color back on release
                        ivBack.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                        return true;
                }
                return false;
            }
        });

        // Create or access the shared preferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);

        // Get an editor to edit SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put the room code into the SharedPreferences with key "current_room"
        editor.putString("current_room", getIntent().getStringExtra("room_code"));

        // Apply the changes
        editor.apply();

        Room.getCurrentRoom(getApplicationContext(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                Log.d("TAG", "received room name: " + room.getName());
                if (room != null) {
                    tvGameTitle.setText(room.getName());
                } else {
                    tvGameTitle.setText("null");
                }
            }
        });
    }

    /**
     * Sets up the tab layout and ViewPager for navigating between different fragments.
     */
    private void setupTabLayout() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        VPAdapter vpAdapter = new VPAdapter(this);
        vpAdapter.addFragment(new ArcadeFragment(), "Arcade");
        vpAdapter.addFragment(new ParticipantsListFragment(), "Players");
        vpAdapter.addFragment(new RoomInfoFragment(), "Info");
        viewPager.setAdapter(vpAdapter);

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getFragmentTitle(position));
        }).attach();
    }
}
