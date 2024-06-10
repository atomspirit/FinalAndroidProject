package com.example.finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.Domains.Room;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

/**
 * Adapter class for displaying Room objects in a RecyclerView.
 */
public class RVRoomAdapter extends RecyclerView.Adapter<RVRoomAdapter.RoomAdapterViewHolder> {
    private Context context;
    private ArrayList<Room> rooms;
    private RVInterface rvInterface;

    /**
     * Constructor for the adapter.
     *
     * @param context The context in which the adapter is used.
     * @param rooms The list of Room objects to display.
     * @param rvInterface The interface for handling click events on the RecyclerView items.
     */
    public RVRoomAdapter(Context context, ArrayList<Room> rooms, RVInterface rvInterface) {
        this.context = context;
        this.rooms = rooms;
        this.rvInterface = rvInterface;
    }

    /**
     * Called when RecyclerView needs a new {@link RoomAdapterViewHolder} of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new RoomAdapterViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RVRoomAdapter.RoomAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_game_item, parent, false);
        return new RVRoomAdapter.RoomAdapterViewHolder(view, rvInterface);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RVRoomAdapter.RoomAdapterViewHolder holder, int position) {
        // Assigning values to the views in the layout based on the position of the RecyclerView
        holder.tvRoomName.setText(rooms.get(position).getName());
        // Load the image from the URL using Glide
        Glide.with(context)
                .load(rooms.get(position).getURL())
                .placeholder(R.drawable.ic_default_room)
                .into(holder.ivRoomIcon);
        holder.tvRoomDescription.setText(rooms.get(position).getDescription());
    }

    /**
     * Returns the total number of items to display in the RecyclerView.
     *
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return rooms.size();
    }

    /**
     * ViewHolder class for holding the views for each item in the RecyclerView.
     */
    public static class RoomAdapterViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivRoomIcon;
        TextView tvRoomName, tvRoomDescription;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view of the item.
         * @param rvInterface The interface for handling click events on the RecyclerView items.
         */
        public RoomAdapterViewHolder(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);

            ivRoomIcon = itemView.findViewById(R.id.ivGameIcon);
            tvRoomName = itemView.findViewById(R.id.tvName);
            tvRoomDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rvInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            rvInterface.onItemClicked(pos);
                        }
                    }
                }
            });
        }
    }
}
