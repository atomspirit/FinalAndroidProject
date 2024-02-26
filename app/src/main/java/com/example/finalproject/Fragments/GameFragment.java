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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.Activities.ActiveGameActivity;
import com.example.finalproject.Adapters.VPAdapter;
import com.example.finalproject.Domains.Game;
import com.example.finalproject.Domains.MyGameAdapter;
import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class GameFragment extends Fragment{

    ImageView ivAddGame;
    ListView listView;
    ArrayList<Game> games;
    Dialog createJoinGame;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        initComponent(v);

        return v;
    }



    private void initComponent(View view)
    {
        games = new ArrayList<Game>();
        listView =  view.findViewById(R.id.lvGameList);

        ivAddGame = view.findViewById(R.id.ivAddGame);
        if (ivAddGame == null)Toast.makeText(getActivity().getApplication(), "null arg!", Toast.LENGTH_LONG).show();
        ivAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        setOnItemClickListView();
        createJoinGame = new Dialog(view.getContext());

    }

    private void addGame(int index)
    {
        games.add(new Game("Game " + index, "Description " + index, R.drawable.game_item_bg_01));
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
        createJoinGame.setContentView(R.layout.dialog_create_join_game);

        TabLayout tabLayout = createJoinGame.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = createJoinGame.findViewById(R.id.viewPager);

        VPAdapter vpAdapter = new VPAdapter(getActivity());
        vpAdapter.addFragment(new JoinGameFragment(), "Join");
        vpAdapter.addFragment(new CreateGameFragment(), "Create");
        viewPager.setAdapter(vpAdapter);

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getFragmentTitle(position));
        }).attach();

        createJoinGame.show();
        addGame(1);
        createJoinGame.setCancelable(true);
    }


}
