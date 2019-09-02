package com.intouchapp.intouch.Utills;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.intouchapp.intouch.Main.Chats.SelectChat.SelectChatFamilyFragment;
import com.intouchapp.intouch.Main.Chats.SelectChat.SelectChatFriendsFragment;
import com.intouchapp.intouch.Main.Chats.SelectChat.SelectChatRelativesFragment;

public  class SelectChatViewPagerAdapter extends FragmentStatePagerAdapter {

    public SelectChatViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SelectChatFamilyFragment();
            case 1:
                return new SelectChatRelativesFragment();
            case 2:
                return new SelectChatFriendsFragment() ;
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
