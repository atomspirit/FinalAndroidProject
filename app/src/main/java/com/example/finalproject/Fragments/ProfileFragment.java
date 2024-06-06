package com.example.finalproject.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.finalproject.Activities.SignInActivity;
import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;

import java.io.UnsupportedEncodingException;

public class ProfileFragment extends Fragment {

    TextView tvSignOut, tvUserEmail, tvUserPassword, tvUsername, tvBio;
    ShapeableImageView ivPicture;
    String userImageURL;
    Uri userImageUri;
    User mUser;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponents(view);

        return view;
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initComponents(View view) {
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserPassword = view.findViewById(R.id.tvUserPassword);
        tvBio = view.findViewById(R.id.tvUserBio);
        ivPicture = view.findViewById(R.id.ivUserIcon);
        tvSignOut = view.findViewById(R.id.tvSignOut);
        tvSignOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        tvSignOut.setTextColor(ContextCompat.getColor(requireContext(), R.color.actionPressed));

                        showSignOutDialog();

                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change text color back on release
                        tvSignOut.setTextColor(ContextCompat.getColor(requireContext(), R.color.action));

                        return true;
                }
                return false;
            }
        });

        User.getCurrentUser(requireContext(), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                mUser = user;
                tvUserEmail.setText("email: " + user.getEmail());
                tvUserPassword.setText("password: " + user.getPassword());
                tvUsername.setText(user.getUsername());
                tvBio.setText(user.getBio());
                userImageURL = user.getURL();

                // Load the image from the URL using Glide
                Glide.with(ProfileFragment.this)
                        .load(user.getURL())
                        .placeholder(R.drawable.ic_default_user)
                        .into(ivPicture);


                ivPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePhoto();
                    }
                });
            }

        });
    }

    // Change Photo -------------------------------------------------------------------------------
    private void changePhoto()
    {
        // open gallery and get new photo uri
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select A Photo"),321);

        // delete old photo in storage
        if(userImageURL != null) {
            try {
                FirebaseManager.deleteImage(userImageURL);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 321 && resultCode == RESULT_OK && data != null)
        {
            userImageUri = data.getData();
            if(userImageUri != null)
            {
                // display new photo
                ivPicture.setImageURI(userImageUri);
                ivPicture.setBackground(null);

                FirebaseManager.uploadImageToFirebase(userImageUri, new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String newURL) {
                        DatabaseReference databaseReference = FirebaseManager.getReference("users");
                        if(databaseReference != null)
                            databaseReference.child(mUser.getUsername()).child("url").setValue(newURL);
                    }
                });
            }
        }
    }

    // Sign Out -----------------------------------------------------------------------------------
    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Once you sign-out you'll need to log in again")
                .setTitle("Sign Out?")
                .setPositiveButton("Sign-Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Add code here to handle leaving the room
                        signOut();
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
    private void signOut()
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("current_username","");
        editor.apply();

        getActivity().finish();
        startActivity(new Intent(getActivity(), SignInActivity.class));
    }
}
