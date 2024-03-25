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

public class JoinGameFragment extends Fragment {

    EditText etCode;
    Button btJoin;
    private FragmentInteractionListener fragmentInteractionListener;



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
                if(Utilities.validateEditText(etCode, "Room code is required")){
                    Utilities.hideKeyboard(getContext(), getActivity().getCurrentFocus());
                    checkRoom();
                    if (fragmentInteractionListener != null)
                        fragmentInteractionListener.onButtonClicked(); // TODO: fix bug - null arg
                }
            }
        });

        return v;
    }

    public void checkRoom(){
        String roomCode = etCode.getText().toString().trim().toUpperCase();

        DatabaseReference reference = FirebaseManager.getReference("rooms");
        if(reference == null){
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

                    User.getCurrentUser(getContext(), new User.UserCallback() {
                        @Override
                        public void onUserReceived(User user) {

                            Room.getCurrentRoom(getContext(), new Room.RoomCallback() {
                                @Override
                                public void onRoomReceived(Room room) {
                                    if(room != null){
                                        // Check if the user is already in the room
                                        if(!room.containsParticipant(user)){
                                            room.addParticipant(user);
                                            room.updateParticipants(getContext());
                                        }
                                    }
                                }
                            });
                            User.addToRoom(user.getUsername(), roomCode);
                        }
                    });




                    // go to ActiveGameActivity
                    Intent intent = new Intent(getActivity().getApplicationContext(), ActiveGameActivity.class);
                    intent.putExtra("room_code", roomCode);
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
    public void setFragmentInteractionListener(FragmentInteractionListener fragmentInteractionListener) {
        this.fragmentInteractionListener = fragmentInteractionListener;
    }
}