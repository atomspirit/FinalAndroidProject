package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.finalproject.Game;
import com.example.finalproject.MyGameAdapter;
import com.example.finalproject.R;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    ImageView ivAddGame;
    ListView listView;
    ArrayList<Game> games;
    Dialog createJoinGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ivAddGame = findViewById(R.id.ivAddGame);
        listView = findViewById(R.id.lvGameList);

        createJoinGame = new Dialog(this);

        games = new ArrayList<>();// populate your list of games here


        ivAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
    }
    private void addGame(int index)
    {
        games.add(new Game("Game " + index, "Description " + index, R.drawable.game_item_bg_01));
        MyGameAdapter adapter = new MyGameAdapter(this, games);
        listView.setAdapter(adapter);
    }
    private void showPopup(View v)
    {
        createJoinGame.setContentView(R.layout.dialog_create_join_game);
        createJoinGame.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createJoinGame.show();
        addGame(6);
        Toast.makeText(this,"click!", Toast.LENGTH_SHORT).show();
        //dialog.dismiss()
    }
}