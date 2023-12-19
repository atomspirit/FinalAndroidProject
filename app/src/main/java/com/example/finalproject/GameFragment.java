package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class GameFragment extends Fragment {

    ImageView ivAddGame;
    ListView listView;
    ArrayList<Game> games;
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_game, container, false);
    }

    private void addGame(int index)
    {
        games.add(new Game("Game " + index, "Description " + index, R.drawable.game_item_bg_01));
        MyGameAdapter adapter = new MyGameAdapter(getContext(), games);
        listView.setAdapter(adapter);
    }
}
