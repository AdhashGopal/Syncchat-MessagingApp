package com.chatapp.synchat.app.adapter;

/**
 *
 */

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mTabsList;

    /**
     *   Create constructor
     * @param fm FragmentManager
     * @param mTabsList list of value
     */
    public PagerAdapter(FragmentManager fm, List<Fragment> mTabsList) {
        super(fm);
        this.mTabsList = mTabsList;
    }

    /**
     * getItem
     * @param position select position
     * @return
     */
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return mTabsList.get(position);
    }

    /**
     * getCount
     * @return value
     */
    @Override
    public int getCount() {
        return mTabsList.size();
    }
}