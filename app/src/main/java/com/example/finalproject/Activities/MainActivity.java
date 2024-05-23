package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.finalproject.Adapters.VPAdapter;
import com.example.finalproject.Fragments.GameFragment;
import com.example.finalproject.Fragments.AboutFragment;
import com.example.finalproject.Fragments.ProfileFragment;
import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        VPAdapter vpAdapter = new VPAdapter(this);
        vpAdapter.addFragment(new AboutFragment(), "About");
        vpAdapter.addFragment(new GameFragment(), "Games");
        vpAdapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(vpAdapter);

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getFragmentTitle(position));
        }).attach();

        viewPager.setCurrentItem(1,false);

    }
}