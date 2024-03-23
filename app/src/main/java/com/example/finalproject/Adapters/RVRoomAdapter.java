package com.example.finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class RVRoomAdapter extends RecyclerView.Adapter<RVRoomAdapter.RoomAdapterViewHolder>{
    Context context;
    ArrayList<Room> rooms;
    RVInterface rvInterface;

    public RVRoomAdapter(Context context, ArrayList<Room> rooms,RVInterface rvInterface) {
        this.context = context;
        this.rooms = rooms;
        this.rvInterface = rvInterface;
    }

    @NonNull
    @Override
    public RVRoomAdapter.RoomAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_game_item, parent, false);
        return new RVRoomAdapter.RoomAdapterViewHolder(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RVRoomAdapter.RoomAdapterViewHolder holder, int position) {
        // assigning values to the views in the layout
        // based on the position of the recycler view

        holder.tvRoomName.setText(rooms.get(position).getName());
        holder.ivRoomIcon.setImageResource(R.drawable.game_item_bg_01); // TODO: change default image
        holder.tvRoomDescription.setText(rooms.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        // return the total number of items to display
        return rooms.size();
    }

    public static class RoomAdapterViewHolder extends RecyclerView.ViewHolder {
        // grabbing the views from the layout

        ShapeableImageView ivRoomIcon;
        TextView tvRoomName, tvRoomDescription;

        public RoomAdapterViewHolder(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);

            ivRoomIcon = itemView.findViewById(R.id.ivGameIcon);
            tvRoomName = itemView.findViewById(R.id.tvName);
            tvRoomDescription = itemView.findViewById(R.id.tvDescription);

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
