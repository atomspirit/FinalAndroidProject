package com.example.finalproject.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.finalproject.Activities.SignInActivity;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.R;

public class ProfileFragment extends Fragment {

    TextView tvSignOut, tvUserEmail, tvUserPassword, tvUsername, tvBio;


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
                tvUserEmail.setText("email: " + user.getEmail());
                tvUserPassword.setText("password: " + user.getPassword());
                tvUsername.setText(user.getUsername());
                tvBio.setText(user.getBio());
            }
        });
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
