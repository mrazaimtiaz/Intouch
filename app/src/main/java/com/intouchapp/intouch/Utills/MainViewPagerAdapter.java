package com.intouchapp.intouch.Utills;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.Events.EventFragment;
import com.intouchapp.intouch.Main.Home.HomeFragment;
import com.intouchapp.intouch.Main.Posts.PostFragment;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.Register.Introduction.AvatarIntroFragment;
import com.intouchapp.intouch.Register.Introduction.MapOrientedIntroFragment;
import com.intouchapp.intouch.Register.Introduction.ShareIntroFragment;

import androidx.annotation.NonNull;
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
                return  "hood";
            case 1:
                return "Posts";

            case 2:
                return "Events";
            default:
                return null;
        }
    }
}
