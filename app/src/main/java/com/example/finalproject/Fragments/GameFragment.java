package com.example.finalproject.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.Adapters.RVRoomAdapter;
import com.example.finalproject.Adapters.RVUserAdapter;
import com.example.finalproject.Adapters.VPAdapterForFragment;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class GameFragment extends Fragment implements RVInterface {


    ImageView ivAddGame;
    TextView tvEmptyRooms;
    RecyclerView recyclerView;
    ArrayList<Room> games;
    Dialog createJoinGame;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        initComponent(v);

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

                    }
                });
            }
        });

        return v;
    }



    @SuppressLint("ClickableViewAccessibility")
    private void initComponent(View view)
    {
        games = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmptyRooms = view.findViewById(R.id.tvEmptyRooms);

        ivAddGame = view.findViewById(R.id.ivAddGame);
        ivAddGame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color on press
                        ivAddGame.setColorFilter(ContextCompat.getColor(getContext(),R.color.primaryPressed));
                        showPopup();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Change text color back on release
                        ivAddGame.setColorFilter(ContextCompat.getColor(getContext(),R.color.primary));


                        return true;
                }
                return false;
            }
        });
        createJoinGame = new Dialog(view.getContext());

    }

    public void addGame(Room room)
    {
        games.add(room);
        setupAdapterWithRecyclerView();

    }
    public void clearGames()
    {
        games.clear();
        setupAdapterWithRecyclerView();
    }
    private void setupAdapterWithRecyclerView() {
        // Set up the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RVRoomAdapter adapter = new RVRoomAdapter(getContext(), games, this);
        recyclerView.setAdapter(adapter);
    }
    private void showPopup()
    {
        // Create the dialog
        createJoinGame.setContentView(R.layout.dialog_create_join_game);

        TabLayout tabLayout = createJoinGame.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = createJoinGame.findViewById(R.id.viewPager);

        // Create the adapter
        VPAdapterForFragment vpAdapter = new VPAdapterForFragment(getActivity());

        // Create the fragments
        JoinGameFragment joinGameFragment = new JoinGameFragment();
        CreateGameFragment createGameFragment = new CreateGameFragment();

        // Pass FragmentInteractionListener to the fragments
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

        // Add the fragments
        vpAdapter.addFragment(joinGameFragment, "Join");
        vpAdapter.addFragment(createGameFragment, "Create");
        viewPager.setAdapter(vpAdapter);

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getFragmentTitle(position));
        }).attach();

        createJoinGame.show();
        createJoinGame.setCancelable(true);
    }
    private void loadRooms(DataSnapshot snapshot) {
        clearGames();
        boolean isEmpty = true;
        for (DataSnapshot ds : snapshot.getChildren()) {
            String code = ds.getValue(String.class);
            isEmpty = false;
            Room.createRoomFromCode(code, new Room.RoomCallback() {
                @Override
                public void onRoomReceived(Room room) {
                    if(room != null)
                        addGame(room);
                }
            });
        }
        if (isEmpty)
        {
            tvEmptyRooms.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            tvEmptyRooms.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ActiveGameActivity.class);
        intent.putExtra("room_code", games.get(position).getCode());
        getActivity().startActivity(intent);
    }
}
