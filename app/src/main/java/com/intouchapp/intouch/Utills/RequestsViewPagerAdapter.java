package com.intouchapp.intouch.Utills;

import com.intouchapp.intouch.Main.Account.Requests.RecievedRequestsFragment;
import com.intouchapp.intouch.Main.Account.Requests.SendRequestsFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public  class RequestsViewPagerAdapter extends FragmentStatePagerAdapter {

    public RequestsViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RecievedRequestsFragment();
            case 1:
                return new SendRequestsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Recieved Requests";
            case 1:
                return "Send Requests";

            default:
                return null;
        }
    }
}
