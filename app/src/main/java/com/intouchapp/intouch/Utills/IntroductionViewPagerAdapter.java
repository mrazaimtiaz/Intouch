package com.intouchapp.intouch.Utills;

import com.intouchapp.intouch.Register.Introduction.AvatarIntroFragment;
import com.intouchapp.intouch.Register.Introduction.MapOrientedIntroFragment;
import com.intouchapp.intouch.Register.Introduction.ShareIntroFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public  class IntroductionViewPagerAdapter extends FragmentStatePagerAdapter {

    public IntroductionViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MapOrientedIntroFragment();
            case 1:
                return new ShareIntroFragment();
            case 2:
                return new AvatarIntroFragment() ;
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
                return "Request";
            case 1:
                return "Fragment 2";

            case 2:
                return "Fragment 3";
            default:
                return null;
        }
    }
}
