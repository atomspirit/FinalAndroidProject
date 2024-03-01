package com.example.finalproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinGameFragment extends Fragment {

    EditText etCode;
    Button btJoin;

    public JoinGameFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_join_game, container, false);

        etCode = v.findViewById(R.id.etCode);
        btJoin = v.findViewById(R.id.btJoin);
        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateCode()){
                    checkRoom();
                }
            }
        });

        return v;
    }
    public Boolean validateCode(){
        String username = etCode.getText().toString();
        if(username.isEmpty()){
            etCode.setError("Room code is required");
            return false;
        } else {
            etCode.setError(null);
            return true;
        }
    }

    public void checkRoom(){
        String roomCode = etCode.getText().toString().trim();

        DatabaseReference reference;
        try {
            reference = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("rooms");
        } catch (Exception e) {
            etCode.setError("Room does not exist");
            return;
        }
        Query query=reference.orderByChild("code").equalTo(roomCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    etCode.setError(null);

                    // Create or access the shared preferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);

                    // Get an editor to edit SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Put the code the room code into the SharedPreferences with key "current_room"
                    editor.putString("current_room", roomCode);

                    // Apply the changes
                    editor.apply();

                    // go to ActiveGameActivity
                    Intent intent = new Intent(getActivity().getApplicationContext(), ActiveGameActivity.class);
                    getActivity().startActivity(intent);

                } else {
                    etCode.setError("Room does not exist");
                    etCode.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}