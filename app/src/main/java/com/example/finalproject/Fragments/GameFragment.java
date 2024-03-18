package com.example.finalproject.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.Adapters.VPAdapterForFragment;
import com.example.finalproject.Adapters.MyGameAdapter;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.FragmentInteractionListener;
import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameFragment extends Fragment {

    ImageView ivAddGame;
    ListView listView;
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(user.getUsername()).child("rooms");
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



    private void initComponent(View view)
    {
        games = new ArrayList<>();
        listView =  view.findViewById(R.id.lvGameList);

        ivAddGame = view.findViewById(R.id.ivAddGame);
        ivAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        setOnItemClickListView();
        createJoinGame = new Dialog(view.getContext());

    }

    public void addGame(Room room)
    {
        games.add(room);
        MyGameAdapter adapter = new MyGameAdapter(getContext(), games);
        listView.setAdapter(adapter);
    }
    private void setOnItemClickListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ActiveGameActivity.class);
                //intent.putExtra(); using position find the database id and pass it
                getActivity().startActivity(intent);
            }
        });
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
        for (DataSnapshot ds : snapshot.getChildren()) {
            Room room = ds.getValue(Room.class); // TODO: change to String and get the room from db (i save only the code)
            addGame(room);
        }
    }

}
