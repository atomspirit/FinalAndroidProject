package com.example.finalproject.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject.Activities.UserProfileActivity;
import com.example.finalproject.Adapters.RVUserAdapter;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;

import java.util.ArrayList;

public class ParticipantsListFragment extends Fragment implements RVInterface {

    ArrayList<User> participants;
    RecyclerView recyclerView;

    public ParticipantsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        // Get the participants
        participants = new ArrayList<>();
        populateParticipantsArray();

        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    private void populateParticipantsArray() {
        Room.getCurrentRoom(getActivity(), new Room.RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                if (room != null) {
                    participants = room.getParticipants();

                    setupAdapterWithRecyclerView();
                }
            }
        });
    }
    private void setupAdapterWithRecyclerView() {
        // Set up the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RVUserAdapter adapter = new RVUserAdapter(getContext(), participants, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("username", participants.get(position).getUsername());
        // TODO: startActivity userProfile
    }
}