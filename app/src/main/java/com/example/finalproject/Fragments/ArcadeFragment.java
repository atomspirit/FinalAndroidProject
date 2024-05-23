package com.example.finalproject.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.Activities.RockPaperScissorsActivity;
import com.example.finalproject.Activities.TicTacToeActivity;
import com.example.finalproject.R;


public class ArcadeFragment extends Fragment{
    TextView tvHeadline;

    public ArcadeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_arcade, container, false);

        CardView btRockPaperScissors = rootView.findViewById(R.id.btRockPaperScissors);
        btRockPaperScissors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RockPaperScissorsActivity.class));
            }
        });
        CardView btTicTacToe = rootView.findViewById(R.id.btTicTacToe);
        btTicTacToe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TicTacToeActivity.class));
            }
        });


        tvHeadline = rootView.findViewById(R.id.tvHeadline);

        return rootView;
    }
}