package com.example.finalproject.Adapters;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.finalproject.Fragments.CreateGameFragment;
import com.example.finalproject.Fragments.JoinGameFragment;

import java.util.ArrayList;

/**
 * View Pager Adapter
 */
public class VPAdapter extends FragmentStateAdapter {
    private final ArrayList<Pair<Fragment,String>> fragmentsList = new ArrayList<>();

    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    /**
     * Adds a fragment to the adapter
     * @param fragment - the fragment
     * @param title - the tab's title of the fragment
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentsList.add(new Pair<>(fragment, title));
    }

    /**
     * Creates a fragment based on position
     * @param position - the position in the list
     * @return - new fragment based on title
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String title = fragmentsList.get(position).second;
        if (title.equals("Create")) return new CreateGameFragment();
        if (title.equals("Join")) return new JoinGameFragment();
        else return new Fragment();
    }

    @Override
    public int getItemCount()  {
        return fragmentsList.size();
    }

    /**
     * Gets the title of a fragment based on position
     * @param position - the position in the list
     * @return - the title
     */
    public String getFragmentTitle(int position) {
        return fragmentsList.get(position).second;
    }

}
