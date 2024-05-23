package com.example.finalproject.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Activities.ConnectionsActivity;
import com.example.finalproject.Activities.NearbyShareActivity;
import com.example.finalproject.Activities.UserProfileActivity;
import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.nearby.connection.*;


public class MainNFCFragment extends Fragment{
    ImageButton ibConnect;
    TextView tvHeadline;

    public MainNFCFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_nfc, container, false);

        ibConnect = rootView.findViewById(R.id.ibConnect);
        ibConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NearbyShareActivity.class));
            }
        });

        tvHeadline = rootView.findViewById(R.id.tvHeadline);
        String roomCode = requireContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_room", "");
        Room.getCatcher(roomCode, new FirebaseManager.DataCallback() {
            @Override
            public void onDataReceived(String data) {
                String username = requireContext().getSharedPreferences("shared_pref",
                        Context.MODE_PRIVATE).getString("current_username", "");
                if (!username.equals(data))
                {
                    tvHeadline.setText("You're Not It!");
                }
            }
        });

        return rootView;
    }
}