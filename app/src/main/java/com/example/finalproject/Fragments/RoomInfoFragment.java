package com.example.finalproject.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.Activities.UserProfileActivity;
import com.example.finalproject.Adapters.RVUserAdapter;
import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class RoomInfoFragment extends Fragment {

    TextView tvRoomCode, tvCreationDate, tvLeaveRoom, tvCreatedBy;
    Boolean isHost = false;
    Room mRoom;

    public RoomInfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_info, container, false);

        // init components
        tvRoomCode = view.findViewById(R.id.tvRoomCode);
        tvCreationDate = view.findViewById(R.id.tvCreationDate);
        tvLeaveRoom = view.findViewById(R.id.tvLeave);
        tvCreatedBy = view.findViewById(R.id.tvCreatedBy);


        Room.getCurrentRoom(requireContext(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                String host = room.getHost().getUsername();
                tvCreatedBy.setText("Created by: " + host);
                String username = requireContext().getSharedPreferences("shared_pref",
                        Context.MODE_PRIVATE).getString("current_username", "");
                if(host.equals(username))
                {
                    tvLeaveRoom.setText("Delete Room");
                    isHost = true;
                }

                mRoom = room;
            }
        });
        // set on click leave
        tvLeaveRoom.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        tvLeaveRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.actionPressed));

                        if(isHost)
                            showDeleteRoomDialog();
                        else
                            showLeaveRoomDialog();

                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change text color back on release
                        tvLeaveRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.action));

                        return true;
                }
                return false;
            }

        });

        // room code
        Room.getCurrentRoom(getActivity(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                tvRoomCode.setText("Room code: " + room.getCode());
                // TODO: add date
            }
        });

        return view;
    }
    private void showLeaveRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Once you leave a room you'll need to re-join the room.")
                .setTitle("Leave Room?")
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Add code here to handle leaving the room
                        leaveRoom();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void leaveRoom()
    {
        User.getCurrentUser(getActivity(), new User.UserCallback() {

            @Override
            public void onUserReceived(User user) {

                mRoom.leave(getContext(), user);

                getActivity().finish();

            }
        });
    }

    private void showDeleteRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Be careful! This action cannot be un-done")
                .setTitle("Delete Room?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Add code here to handle leaving the room
                        deleteRoom();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void deleteRoom()
    {
        /*ArrayList<User> parts = mRoom.getParticipants();
        for(User user : parts)
        {
            mRoom.leave(requireContext(),user);
        }*/
        mRoom.delete();
        getActivity().finish();
    }
}