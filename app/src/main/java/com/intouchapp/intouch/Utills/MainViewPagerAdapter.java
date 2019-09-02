package com.intouchapp.intouch.Utills;

import com.intouchapp.intouch.Main.Events.EventFragment;
import com.intouchapp.intouch.Main.Home.HomeFragment;
import com.intouchapp.intouch.Main.Posts.PostFragment;
import com.intouchapp.intouch.Register.Introduction.AvatarIntroFragment;
import com.intouchapp.intouch.Register.Introduction.MapOrientedIntroFragment;
import com.intouchapp.intouch.Register.Introduction.ShareIntroFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public  class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new PostFragment();
            case 2:
                return new EventFragment() ;
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
                return "";
            case 1:
                return "";

            case 2:
                return "";
            default:
                return null;
        }
    }
}
