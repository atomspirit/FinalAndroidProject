package com.example.finalproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.finalproject.Domains.User;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

/**
 * Adapter class for displaying User objects in a RecyclerView.
 */
public class RVUserAdapter extends RecyclerView.Adapter<RVUserAdapter.UserAdapterViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private RVInterface rvInterface;

    /**
     * Constructor for the adapter.
     *
     * @param context     The context in which the adapter is used.
     * @param users       The list of User objects to display.
     * @param rvInterface The interface for handling click events on the RecyclerView items.
     */
    public RVUserAdapter(Context context, ArrayList<User> users, RVInterface rvInterface) {
        this.context = context;
        this.users = users;
        this.rvInterface = rvInterface;
    }

    /**
     * Method to update the user list in the adapter.
     *
     * @param newUsers The new list of User objects to display.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateUsers(ArrayList<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link UserAdapterViewHolder} of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new UserAdapterViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RVUserAdapter.UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_user_item, parent, false);
        return new UserAdapterViewHolder(view, rvInterface);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RVUserAdapter.UserAdapterViewHolder holder, int position) {
        // Assigning values to the views in the layout based on the position of the RecyclerView
        holder.tvUserName.setText(users.get(position).getUsername());
        // Load the image from the URL using Glide
        long startTime = System.currentTimeMillis();
        Log.d("IMAGE_LOAD", users.get(position).getURL());
        Glide.with(context)
                .load(users.get(position).getURL())
                .placeholder(R.drawable.ic_default_user)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable Glide disk caching
                .skipMemoryCache(true) // Disable Glide memory caching
                .into(holder.ivPlayerIcon);
    }

    /**
     * Returns the total number of items to display in the RecyclerView.
     *
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class for holding the views for each item in the RecyclerView.
     */
    public static class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivPlayerIcon;
        TextView tvUserName;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView    The view of the item.
         * @param rvInterface The interface for handling click events on the RecyclerView items.
         */
        public UserAdapterViewHolder(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);

            ivPlayerIcon = itemView.findViewById(R.id.ivPlayerIcon);
            tvUserName = itemView.findViewById(R.id.tvPlayerName);

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
