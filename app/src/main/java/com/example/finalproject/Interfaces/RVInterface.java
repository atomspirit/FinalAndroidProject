package com.example.finalproject.Interfaces;

/**
 * Interface for handling item click events in RecyclerViews.
 */
public interface RVInterface {

    /**
     * Called when an item in the RecyclerView is clicked.
     *
     * @param position The position of the clicked item in the RecyclerView.
     */
    void onItemClicked(int position);
}
