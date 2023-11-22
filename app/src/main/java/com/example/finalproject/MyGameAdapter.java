package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyGameAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Game> mData;

    public MyGameAdapter(Context context, ArrayList<Game> data) {
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
            view = inflater.inflate(R.layout.lv_game_item, null);
        }

        de.hdodenhof.circleimageview.CircleImageView imageView = view.findViewById(R.id.cimGameImage);
        TextView textTitle = view.findViewById(R.id.tvGameTitle);
        TextView textDescription = view.findViewById(R.id.tvGameDesc);

        Game game = mData.get(position);

        imageView.setImageResource(game.getImage());
        textTitle.setText(game.getTitle());
        textDescription.setText(game.getDescription());

        return view;
    }
}
