package com.intouchapp.intouch.Main.Account.Knowers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.KnowersViewPagerAdapter;
import com.intouchapp.intouch.Utills.SearchViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class KnowersActivity extends AppCompatActivity {

    //widgets
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ImageView mBack;



    private static final String TAG = "KnowersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowers);


        //initalization
        mViewPager  = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mBack = (ImageView) findViewById(R.id.iv_back);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingViewPager();

    }



    //----------------------------------- setting up view pager -------------------------------------------------------------
    private void settingViewPager(){
        KnowersViewPagerAdapter viewPagerAdapter = new KnowersViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }
}
