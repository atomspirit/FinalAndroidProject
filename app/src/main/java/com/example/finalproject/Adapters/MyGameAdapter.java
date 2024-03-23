package com.example.finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.Domains.Game;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class MyGameAdapter extends ArrayAdapter<Room> {

    public MyGameAdapter(@NonNull Context context, ArrayList<Room> data) {
        super(context, R.layout.card_view_game_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Room room = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_view_game_item, parent, false);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        ShapeableImageView ivGameIcon = convertView.findViewById(R.id.ivGameIcon);


        tvName.setText(room.getName());
        tvDescription.setText(room.getDescription());
        ivGameIcon.setImageResource(R.drawable.game_item_bg_01); // TODO: change default image


        return convertView;
    }
}
