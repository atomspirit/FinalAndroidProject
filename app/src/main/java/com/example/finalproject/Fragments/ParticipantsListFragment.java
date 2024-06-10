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

/**
 * A fragment that displays a list of participants in the current room.
 */
public class ParticipantsListFragment extends Fragment implements RVInterface {

    private ArrayList<User> participants;
    private RecyclerView recyclerView;
    private RVUserAdapter adapter;

    /**
     * Default constructor for ParticipantsListFragment.
     */
    public ParticipantsListFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment and sets up the RecyclerView and adapter.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        // Initialize participants list and RecyclerView
        participants = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        setupAdapterWithRecyclerView();

        return view;
    }

    /**
     * Populates the participants array with users from the current room.
     */
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

    /**
     * Sets up the RecyclerView and its adapter.
     */
    private void setupAdapterWithRecyclerView() {
        // Set up the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RVUserAdapter(getContext(), participants, this);
        recyclerView.setAdapter(adapter);

        // Populate participants array and update adapter
        populateParticipantsArray();
    }

    /**
     * Handles item clicks in the RecyclerView.
     *
     * @param position The position of the clicked item.
     */
    @Override
    public void onItemClicked(int position) {
        String currentUsername = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_username", "");
        if (participants.get(position).getUsername().equals(currentUsername)) {
            Toast.makeText(getContext(), "This is you!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("username", participants.get(position).getUsername());
        startActivity(intent);
    }
}
