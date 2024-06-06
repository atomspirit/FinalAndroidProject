package com.example.finalproject.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalproject.Activities.UserProfileActivity;
import com.example.finalproject.Adapters.RVUserAdapter;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class ParticipantsListFragment extends Fragment implements RVInterface {

    ArrayList<User> participants;
    RecyclerView recyclerView;
    RVUserAdapter adapter;

    public ParticipantsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        // Get the participants
        participants = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        setupAdapterWithRecyclerView();

        return view;
    }

    private void populateParticipantsArray() {
        Room.getCurrentRoom(getActivity(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                if (room != null) {
                    room.loadParticipants(new Room.OnLoadParticipants() {
                        @Override
                        public void onLoadedUser(ArrayList<User> usersSoFar, User currentUser) {
                            participants = usersSoFar;
                            adapter.updateUsers(participants);
                        }
                    });
                }
            }
        });
    }

    private void setupAdapterWithRecyclerView() {
        // Set up the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RVUserAdapter(getContext(), participants, this);
        recyclerView.setAdapter(adapter);

        // Populate participants array and update adapter
        populateParticipantsArray();
    }

    @Override
    public void onItemClicked(int position) {
        String current_username = getActivity().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_username", "");
        if(participants.get(position).getUsername().equals(current_username)) {
            Toast.makeText(getContext(),"This is you!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("username", participants.get(position).getUsername());
        startActivity(intent);
    }
}