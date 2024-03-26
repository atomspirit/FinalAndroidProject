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

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.Interfaces.FragmentInteractionListener;
import com.example.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CreateGameFragment extends Fragment  {

    EditText etCode, etName, etDesc;
    Button btCreate;
    DatabaseReference databaseReference;
    private FragmentInteractionListener fragmentInteractionListener;


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
        etDesc = v.findViewById(R.id.etDesc);
        btCreate = v.findViewById(R.id.btCreate);
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.validateEditText(etName, "Room name is required") &&
                        (Utilities.validateEditText(etCode, "Room description is required"))
                        && Utilities.validateEditText(etCode, "Room code is required")){
                    Utilities.hideKeyboard(getContext(), getActivity().getCurrentFocus());
                    addRoom();
                    if(fragmentInteractionListener != null)
                        fragmentInteractionListener.onButtonClicked();
                }
            }
        });

        return v;
    }
    private void addRoom()
    {
        databaseReference = FirebaseManager.getReference("rooms");
        if(databaseReference == null){
            return;
        }

        String roomName = etName.getText().toString().trim();
        String roomCode = etCode.getText().toString().trim().toUpperCase();
        String roomDesc = etDesc.getText().toString().trim();

        User.getCurrentUser(getActivity(), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                Room room = new Room(roomName, roomCode, user, roomDesc);
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
                    etCode.setError("A room with that code already exists");
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

                    User.addToRoom(room.getHost().getUsername(), room.getCode());

                    // go to ActiveGameActivity
                    Intent intent = new Intent(getActivity().getApplicationContext(), ActiveGameActivity.class);
                    intent.putExtra("room_code", room.getCode());
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setFragmentInteractionListener(FragmentInteractionListener fragmentInteractionListener) {
        this.fragmentInteractionListener = fragmentInteractionListener;
    }
}