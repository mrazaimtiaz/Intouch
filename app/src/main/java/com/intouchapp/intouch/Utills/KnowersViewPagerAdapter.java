package com.intouchapp.intouch.Utills;

import com.intouchapp.intouch.Main.Account.Knowers.FamilyFragment;
import com.intouchapp.intouch.Main.Account.Knowers.FriendsFragment;
import com.intouchapp.intouch.Main.Account.Knowers.RelativesFragment;
import com.intouchapp.intouch.Main.Explore.Searching.HoodSearchResultFragment;
import com.intouchapp.intouch.Main.Explore.Searching.HouseSearchResultFragment;
import com.intouchapp.intouch.Main.Explore.Searching.PeopleSearchResultFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public  class KnowersViewPagerAdapter extends FragmentStatePagerAdapter {

    public KnowersViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FamilyFragment();
            case 1:
                return new RelativesFragment();
            case 2:
                return new FriendsFragment() ;
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
                return "Family";
            case 1:
                return "Relatives";

            case 2:
                return "Friends";
            default:
                return null;
        }
    }
}
