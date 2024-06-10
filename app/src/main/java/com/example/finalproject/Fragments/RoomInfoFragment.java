package com.example.finalproject.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.R;

/**
 * A fragment that displays information about the current room and allows the user to leave or delete the room if they are the host.
 */
public class RoomInfoFragment extends Fragment {

    private TextView tvRoomCode, tvCreationDate, tvLeaveRoom, tvCreatedBy;
    private Boolean isHost = false;
    private Room mRoom;

    /**
     * Default constructor for the RoomInfoFragment.
     */
    public RoomInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for the fragment and initializes its components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_info, container, false);

        // Initialize UI components
        tvRoomCode = view.findViewById(R.id.tvRoomCode);
        tvCreationDate = view.findViewById(R.id.tvCreationDate);
        tvLeaveRoom = view.findViewById(R.id.tvLeave);
        tvCreatedBy = view.findViewById(R.id.tvCreatedBy);

        // Get current room information
        Room.getCurrentRoom(requireContext(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                String host = room.getHost().getUsername();
                tvCreatedBy.setText("Created by: " + host);
                String username = requireContext().getSharedPreferences("shared_pref",
                        Context.MODE_PRIVATE).getString("current_username", "");
                if (host.equals(username)) {
                    tvLeaveRoom.setText("Delete Room");
                    isHost = true;
                }
                mRoom = room;
            }
        });

        // Set onTouchListener for leaving or deleting the room
        tvLeaveRoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        tvLeaveRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.actionPressed));
                        if (isHost)
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

        // Display room code and creation date
        Room.getCurrentRoom(getActivity(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                tvRoomCode.setText("Room code: " + room.getCode());
                tvCreationDate.setText("Created on: " + room.getCreationDate());
            }
        });

        return view;
    }

    /**
     * Shows a dialog to confirm leaving the room.
     */
    private void showLeaveRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Once you leave a room you'll need to re-join the room.")
                .setTitle("Leave Room?")
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        leaveRoom();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Leaves the room.
     */
    private void leaveRoom() {
        User.getCurrentUser(getActivity(), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                mRoom.leave(user);
                getActivity().finish();
            }
        });
    }

    /**
     * Shows a dialog to confirm deleting the room.
     */
    private void showDeleteRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Be careful! This action cannot be undone.")
                .setTitle("Delete Room?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRoom();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Deletes the room.
     */
    private void deleteRoom() {
        mRoom.delete();
        getActivity().finish();
    }
}
