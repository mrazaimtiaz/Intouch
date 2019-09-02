package com.intouchapp.intouch.Main.Chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SelectChatViewPagerAdapter;

public class SelectChatActivity extends AppCompatActivity {

    //widgets
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ImageView mBack;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat);

        //initalization
        mViewPager  = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mContext = SelectChatActivity.this;

        settingViewPager();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //----------------------------------- setting up view pager -------------------------------------------------------------
    private void settingViewPager(){
        SelectChatViewPagerAdapter viewPagerAdapter = new SelectChatViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }
}
