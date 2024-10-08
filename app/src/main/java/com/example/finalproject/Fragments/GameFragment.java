package com.example.finalproject.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.Activities.ActiveRoomActivity;
import com.example.finalproject.Adapters.RVRoomAdapter;
import com.example.finalproject.Adapters.VPAdapter;
import com.example.finalproject.Domains.FirebaseManager;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.FragmentInteractionListener;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A fragment that displays a list of game rooms and allows the user to create or join a game.
 */
public class GameFragment extends Fragment implements RVInterface {

    private ImageView ivAddGame;
    private TextView tvEmptyRooms;
    private RecyclerView recyclerView;
    private ArrayList<Room> games;
    private Dialog createJoinGame;

    /**
     * Default constructor for GameFragment.
     */
    public GameFragment() {
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
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        initComponent(v);

        // Load rooms for the current user
        User.getCurrentUser(getContext(), new User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                DatabaseReference databaseReference = FirebaseManager.getReference("users").child(user.getUsername()).child("rooms");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loadRooms(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
            }
        });

        return v;
    }

    /**
     * Initializes UI components and sets up event listeners.
     *
     * @param view The parent view that the fragment's UI should be attached to.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initComponent(View view) {
        games = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmptyRooms = view.findViewById(R.id.tvEmptyRooms);

        ivAddGame = view.findViewById(R.id.ivAddGame);
        ivAddGame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change icon color on press
                        ivAddGame.setColorFilter(ContextCompat.getColor(getContext(), R.color.primaryPressed));
                        showPopup();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change icon color back on release
                        ivAddGame.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                        return true;
                }
                return false;
            }
        });
        createJoinGame = new Dialog(view.getContext());
    }

    /**
     * Adds a game room to the list and updates the RecyclerView.
     *
     * @param room The room to be added.
     */
    public void addGame(Room room) {
        games.add(room);
        setupAdapterWithRecyclerView();
    }

    /**
     * Clears the list of game rooms and updates the RecyclerView.
     */
    public void clearGames() {
        games.clear();
        setupAdapterWithRecyclerView();
    }

    /**
     * Sets up the RecyclerView with the appropriate adapter and layout manager.
     */
    private void setupAdapterWithRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RVRoomAdapter adapter = new RVRoomAdapter(getContext(), games, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Displays a popup dialog for creating or joining a game.
     */
    private void showPopup() {
        createJoinGame.setContentView(R.layout.dialog_create_join_game);

        TabLayout tabLayout = createJoinGame.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = createJoinGame.findViewById(R.id.viewPager);

        VPAdapter vpAdapter = new VPAdapter(getActivity());

        // Create the fragments
        JoinGameFragment joinGameFragment = new JoinGameFragment();
        CreateGameFragment createGameFragment = new CreateGameFragment();

        // Set interaction listeners for the fragments
        joinGameFragment.setFragmentInteractionListener(new FragmentInteractionListener() {
            @Override
            public void onButtonClicked() {
                createJoinGame.dismiss();
            }
        });
        createGameFragment.setFragmentInteractionListener(new FragmentInteractionListener() {
            @Override
            public void onButtonClicked() {
                createJoinGame.dismiss();
            }
        });

        // Add the fragments to the adapter
        vpAdapter.addFragment(joinGameFragment, "Join");
        vpAdapter.addFragment(createGameFragment, "Create");
        viewPager.setAdapter(vpAdapter);

        // Attach TabLayout to ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getFragmentTitle(position));
        }).attach();

        createJoinGame.show();
        createJoinGame.setCancelable(true);
    }

    /**
     * Loads the rooms from the database and updates the UI.
     *
     * @param snapshot The DataSnapshot containing the room data.
     */
    private void loadRooms(DataSnapshot snapshot) {
        clearGames();
        boolean isEmpty = true;
        for (DataSnapshot ds : snapshot.getChildren()) {
            String code = ds.getValue(String.class);
            isEmpty = false;
            Room.createRoomFromCode(code, new Room.RoomCallback() {
                @Override
                public void onRoomReceived(Room room) {
                    if (room != null)
                        addGame(room);
                }
            });
        }
        if (isEmpty) {
            tvEmptyRooms.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyRooms.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handles item click events in the RecyclerView.
     *
     * @param position The position of the clicked item.
     */
    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ActiveRoomActivity.class);
        intent.putExtra("room_code", games.get(position).getCode());
        getActivity().startActivity(intent);
    }
}
