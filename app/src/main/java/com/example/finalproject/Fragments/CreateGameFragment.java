package com.example.finalproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalproject.Activities.ActiveRoomActivity;
import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.Interfaces.FragmentInteractionListener;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment that allows users to create a new game room.
 */
public class CreateGameFragment extends Fragment {

    // UI elements
    private EditText etCode, etName, etDesc;
    private Button btCreate;
    private ShapeableImageView ivRoomPic;

    // Firebase reference
    private DatabaseReference databaseReference;

    // Room image URI and URL
    private String roomImageURL;
    private Uri roomImageUri;

    // Fragment interaction listener
    private FragmentInteractionListener fragmentInteractionListener;

    /**
     * Default constructor for CreateGameFragment.
     */
    public CreateGameFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment and sets up the UI elements and event listeners.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_game, container, false);

        // Initialize UI elements
        ivRoomPic = v.findViewById(R.id.ivRoomIcon);
        etCode = v.findViewById(R.id.etCode);
        etName = v.findViewById(R.id.etName);
        etDesc = v.findViewById(R.id.etDesc);
        btCreate = v.findViewById(R.id.btCreate);

        // Set click listener for the create button
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.validateEditText(etName, "Room name is required") &&
                        Utilities.validateEditText(etDesc, "Room description is required") &&
                        Utilities.validateEditText(etCode, "Room code is required")) {

                    // Hide keyboard
                    Utilities.hideKeyboard(getContext(), getActivity().getCurrentFocus());

                    // Use default image if no image is selected
                    if (roomImageUri == null) {
                        int imageResource = getActivity().getResources().getIdentifier("@drawable/ic_default_room", null, getActivity().getPackageName());
                        roomImageUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + imageResource);
                    }

                    // Upload image to Firebase
                    FirebaseManager.uploadImageToFirebase(roomImageUri, new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            roomImageURL = s;
                            addRoom();
                        }
                    });

                    // Notify fragment interaction listener
                    if (fragmentInteractionListener != null)
                        fragmentInteractionListener.onButtonClicked();
                }
            }
        });

        // Set click listener for the add picture button
        Button btAddPicture = v.findViewById(R.id.btAddPic);
        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select A Photo"), 321);
            }
        });

        return v;
    }

    /**
     * Adds a new room to the database.
     */
    private void addRoom() {
        databaseReference = FirebaseManager.getReference("rooms");
        if (databaseReference == null) {
            return;
        }

        String roomName = etName.getText().toString().trim();
        String roomCode = etCode.getText().toString().trim().toUpperCase();
        String roomDesc = etDesc.getText().toString().trim();

        // Get the current user and create the room
        User.getCurrentUser(getActivity(), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String date = formatter.format(new Date());
                Room room = new Room(roomName, roomCode, user, roomDesc, roomImageURL, date);
                compareRoom(room);
            }
        });
    }

    /**
     * Compares the room code with existing rooms in the database and adds the room if the code is unique.
     *
     * @param room The room to be added.
     */
    public void compareRoom(Room room) {
        Query query = databaseReference.orderByChild("code").equalTo(room.getCode());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etCode.setError(null);
                if (snapshot.exists()) {
                    etCode.setError("A room with that code already exists");
                    etCode.requestFocus();
                } else {
                    databaseReference.child(room.getCode()).setValue(room.toMap());
                    Log.d("TAG", "" + room.toMap());

                    // Create or access the shared preferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("current_room", room.getCode());
                    editor.apply();

                    // Add user to the room
                    User.addToRoom(room.getHost().getUsername(), room.getCode());

                    // Navigate to ActiveRoomActivity
                    Intent intent = new Intent(getActivity().getApplicationContext(), ActiveRoomActivity.class);
                    intent.putExtra("room_code", room.getCode());
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }

    /**
     * Sets the fragment interaction listener.
     *
     * @param fragmentInteractionListener The listener to be set.
     */
    public void setFragmentInteractionListener(FragmentInteractionListener fragmentInteractionListener) {
        this.fragmentInteractionListener = fragmentInteractionListener;
    }

    /**
     * Handles the result of the activity for selecting an image.
     *
     * @param requestCode The request code of the activity.
     * @param resultCode  The result code of the activity.
     * @param data        The data returned from the activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && resultCode == Activity.RESULT_OK && data != null) {
            roomImageUri = data.getData();
            if (roomImageUri != null) {
                ivRoomPic.setImageURI(roomImageUri);
                ivRoomPic.setBackground(null);
            }
        }
    }
}
