package com.intouchapp.intouch.Utills;

import com.intouchapp.intouch.Main.Events.EventFragment;
import com.intouchapp.intouch.Main.Explore.Searching.HoodSearchResultFragment;
import com.intouchapp.intouch.Main.Explore.Searching.HouseSearchResultFragment;
import com.intouchapp.intouch.Main.Explore.Searching.PeopleSearchResultFragment;
import com.intouchapp.intouch.Main.Home.HomeFragment;
import com.intouchapp.intouch.Main.Posts.PostFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public  class SearchViewPagerAdapter extends FragmentStatePagerAdapter {

    public SearchViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HoodSearchResultFragment();
            case 1:
                return new HouseSearchResultFragment();
            case 2:
                return new PeopleSearchResultFragment() ;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Hood";
            case 1:
                return "House";

            case 2:
                return "People";
            default:
                return null;
        }
    }
}
