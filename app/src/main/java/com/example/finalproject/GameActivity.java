package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ListView listView = findViewById(R.id.lvGameList);

        ArrayList<Game> games = new ArrayList<>();// populate your list of games here
        games.add(new Game("Game 1", "Description 1", R.drawable.game_item_bg_01));
        games.add(new Game("Game 2", "Description 2", R.drawable.game_item_bg_01));
        games.add(new Game("Game 3", "Description 3", R.drawable.game_item_bg_01));

        MyGameAdapter adapter = new MyGameAdapter(this, games);
        listView.setAdapter(adapter);
    }
}