package com.example.finalproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.AddGameListener;
import com.example.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CreateGameFragment extends Fragment {

    EditText etCode, etName;
    Button btCreate;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private AddGameListener addGameListener;


    public CreateGameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_game, container, false);

        etCode = v.findViewById(R.id.etCode);
        etName = v.findViewById(R.id.etName);
        btCreate = v.findViewById(R.id.btCreate);
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateCode() && validateName()){
                    addRoom();
                }
            }
        });

        return v;
    }
    private void addRoom()
    {
        firebaseDatabase = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("rooms");

        String roomName = etName.getText().toString().trim();
        String roomCode = etCode.getText().toString().trim();

        User.getCurrentUser(getActivity(), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                Room room = new Room(roomName,roomCode,user);
                compareRoom(room);
            }
        });


    }

    // Handle the firebase
    public void compareRoom(Room room){
        Query query = databaseReference.orderByChild("code").equalTo(room.getCode());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etCode.setError(null);
                if(snapshot.exists()){
                    etCode.setError("Username already exists");
                    etCode.requestFocus();
                }
                else {
                    databaseReference.child(room.getCode()).setValue(room.toMap());
                    Log.d("TAG", "" + room.toMap());
                    // Create or access the shared preferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);

                    // Get an editor to edit SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Put the code the room code into the SharedPreferences with key "current_room"
                    editor.putString("current_room", room.getCode());

                    // Apply the changes
                    editor.apply();

                    // Add the game to the listView of games
                    if(addGameListener != null){
                        addGameListener.onAddGame(room);
                    }

                    // go to ActiveGameActivity
                    Intent intent = new Intent(getActivity().getApplicationContext(), ActiveGameActivity.class);
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    public Boolean validateName(){
        String username = etName.getText().toString();
        if(username.isEmpty()){
            etName.setError("Room name is required");
            return false;
        } else {
            etName.setError(null);
            return true;
        }
    }

    public void setAddGameListener(AddGameListener listener) {
        this.addGameListener = listener;
    }
}