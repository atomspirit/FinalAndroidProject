package com.example.finalproject.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class RVUserAdapter extends RecyclerView.Adapter<RVUserAdapter.UserAdapterViewHolder> {

    Context context;
    ArrayList<User> users;
    RVInterface rvInterface;

    public RVUserAdapter(Context context, ArrayList<User> users,RVInterface rvInterface) {
        this.context = context;
        this.users = users;
        this.rvInterface = rvInterface;
    }

    @NonNull
    @Override
    public RVUserAdapter.UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_user_item, parent, false);
        return new UserAdapterViewHolder(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RVUserAdapter.UserAdapterViewHolder holder, int position) {
        // assigning values to the views in the layout
        // based on the position of the recycler view

        holder.tvUserName.setText(users.get(position).getUsername());
        // Load the image from the URL using Glide
        Glide.with(context) //problem here
                .load(users.get(position).getURL())
                .placeholder(R.drawable.ic_default_user)
                .into(holder.ivPlayerIcon);
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

        public UserAdapterViewHolder(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);

            ivPlayerIcon = itemView.findViewById(R.id.ivPlayerIcon);
            tvUserName = itemView.findViewById(R.id.tvPlayerName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rvInterface != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                            rvInterface.onItemClicked(pos);
                    }
                }
            });
        }
    }
}
