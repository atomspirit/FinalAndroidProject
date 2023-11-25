package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView btnGame;
    private TextView btnOptions;
    private TextView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGame = findViewById(R.id.btnGame);
        btnOptions = findViewById(R.id.btnOptions);
        btnProfile = findViewById(R.id.btnProfile);

        // set default view to game
        switchFragment(new GameFragment());
        btnGame.setTextColor(getResources().getColor(R.color.action));
        btnGame.setPaintFlags(btnGame.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set click listeners for each tab
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new GameFragment());
                setTabOn(btnGame);
                setTabOff(btnOptions);
                setTabOff(btnProfile);
            }
        });

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new OptionsFragment());
                setTabOn(btnOptions);
                setTabOff(btnGame);
                setTabOff(btnProfile);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new ProfileFragment());
                setTabOn(btnProfile);
                setTabOff(btnOptions);
                setTabOff(btnGame);
            }
        });
    }

    private void setTabOn(TextView tab)
    {
        tab.setTextColor(getResources().getColor(R.color.action));
        tab.setPaintFlags(tab.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
    private void setTabOff(TextView tab)
    {
        tab.setTextColor(getResources().getColor(R.color.secondary));
        tab.setPaintFlags(0);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}