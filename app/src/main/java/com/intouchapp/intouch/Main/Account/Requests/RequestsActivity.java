package com.intouchapp.intouch.Main.Account.Requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.KnowersViewPagerAdapter;
import com.intouchapp.intouch.Utills.RequestsViewPagerAdapter;

public class RequestsActivity extends AppCompatActivity {

    //widgets
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
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
        RequestsViewPagerAdapter viewPagerAdapter = new RequestsViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }
}
