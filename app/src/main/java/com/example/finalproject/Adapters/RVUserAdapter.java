package com.example.finalproject.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Domains.User;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class RVUserAdapter extends RecyclerView.Adapter<RVUserAdapter.UserAdapterViewHolder> {

    Context context;
    ArrayList<User> users;

    public RVUserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        Log.d("TAG2", "RVUserAdapter: " + users);
    }

    @NonNull
    @Override
    public RVUserAdapter.UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_user_item, parent, false);
        return new UserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVUserAdapter.UserAdapterViewHolder holder, int position) {
        // assigning values to the views in the layout
        // based on the position of the recycler view

        holder.tvUserName.setText(users.get(position).getUsername());
        holder.ivPlayerIcon.setImageResource(R.drawable.game_item_bg_01); // TODO: change default image
    }

    @Override
    public int getItemCount() {
        // return the total number of items to display
        return users.size();
    }

    public static class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        // grabbing the views from the layout

        ShapeableImageView ivPlayerIcon;
        TextView tvUserName;

        public UserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPlayerIcon = itemView.findViewById(R.id.ivPlayerIcon);
            tvUserName = itemView.findViewById(R.id.tvPlayerName);
        }
    }
}
