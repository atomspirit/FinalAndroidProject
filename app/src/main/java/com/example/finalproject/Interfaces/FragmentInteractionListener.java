package com.example.finalproject.Interfaces;

/**
 * Interface for communication between fragments and their hosting activities.
 * Defines a method to notify the activity when a button is clicked in the fragment.
 */
public interface FragmentInteractionListener {

    /**
     * Called when a button is clicked in the fragment.
     */
    void onButtonClicked();
}

