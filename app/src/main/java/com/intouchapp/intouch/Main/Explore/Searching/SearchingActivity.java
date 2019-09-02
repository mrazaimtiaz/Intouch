package com.intouchapp.intouch.Main.Explore.Searching;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SearchViewPagerAdapter;

import java.util.Map;
import java.util.TreeMap;

public class SearchingActivity extends AppCompatActivity {

    //widgets
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SearchView mSearchView;

    private ImageView mBack;

    private static final String TAG = "SearchingActivity";

    private String search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);


        //initalization
        mViewPager  = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mSearchView = (SearchView) findViewById(R.id.et_search);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search = getIntent().getStringExtra(getString(R.string.search));

        settingViewPager();
        settingSearchView();
    }
    public String sendData() {
        return search;
    }
    //************************************************* setting search view **************************************************
    private void settingSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty()){
                    Intent intent = new Intent(SearchingActivity.this, SearchingActivity.class);
                    intent.putExtra(getString(R.string.search),query);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }
    //----------------------------------- setting up view pager -------------------------------------------------------------
    private void settingViewPager(){
        SearchViewPagerAdapter viewPagerAdapter = new SearchViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }
}
