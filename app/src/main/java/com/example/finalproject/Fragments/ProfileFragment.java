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

/**
 * A fragment that displays the user's profile and allows them to sign out or change their profile photo.
 */
public class ProfileFragment extends Fragment {

    private TextView tvSignOut, tvUserEmail, tvUserPassword, tvUsername, tvBio;
    private ShapeableImageView ivPicture;
    private String userImageURL;
    private Uri userImageUri;
    private User mUser;

    /**
     * Default constructor for ProfileFragment.
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment and initializes components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents(view);
        return view;
    }

    /**
     * Initializes UI components and sets up event listeners.
     *
     * @param view The root view of the fragment's layout.
     */
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
                        tvSignOut.setTextColor(ContextCompat.getColor(requireContext(), R.color.actionPressed));
                        showSignOutDialog();
                        return true;
                    case MotionEvent.ACTION_UP:
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

    /**
     * Opens the gallery to select a new profile photo and deletes the old photo from storage.
     */
    private void changePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select A Photo"), 321);

        if (userImageURL != null) {
            try {
                FirebaseManager.deleteImage(userImageURL);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Handles the result from the photo selection activity.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        An Intent that carries the result data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && resultCode == RESULT_OK && data != null) {
            userImageUri = data.getData();
            if (userImageUri != null) {
                ivPicture.setImageURI(userImageUri);
                ivPicture.setBackground(null);

                FirebaseManager.uploadImageToFirebase(userImageUri, new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String newURL) {
                        DatabaseReference databaseReference = FirebaseManager.getReference("users");
                        if (databaseReference != null) {
                            databaseReference.child(mUser.getUsername()).child("url").setValue(newURL);
                        }
                    }
                });
            }
        }
    }

    /**
     * Shows a dialog to confirm sign out.
     */
    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Once you sign-out you'll need to log in again")
                .setTitle("Sign Out?")
                .setPositiveButton("Sign-Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        signOut();
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
     * Signs out the user and navigates to the SignInActivity.
     */
    private void signOut() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current_username", "");
        editor.apply();

        getActivity().finish();
        startActivity(new Intent(getActivity(), SignInActivity.class));
    }
}
