package com.example.finalproject.Domains;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.finalproject.Domains.Game;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class MyGameAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Room> mData;

    public MyGameAdapter(Context context, ArrayList<Room> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card_view_game_item, null);
        }

        ShapeableImageView imageView = view.findViewById(R.id.ivGameIcon);
        TextView textTitle = view.findViewById(R.id.tvName);
        TextView textDescription = view.findViewById(R.id.tvDescription);

        Room game = mData.get(position);

        imageView.setImageResource(R.drawable.game_item_bg_01); // TODO: change default image
        textTitle.setText(game.getName());
        textDescription.setText("Default description"); // TODO: change default description

        return view;
    }
}
