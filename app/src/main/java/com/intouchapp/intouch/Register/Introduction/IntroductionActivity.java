package com.intouchapp.intouch.Register.Introduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ServerValue;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.Login.LoginActivity;
import com.intouchapp.intouch.Signup.SignupActivity;
import com.intouchapp.intouch.Utills.IntroductionViewPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class IntroductionActivity extends AppCompatActivity {

    //widgets
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private Button mLogin,mSignup;

    private static final String TAG = "IntroductionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        //initalization
        mViewPager  = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mSignup = (Button) findViewById(R.id.btn_signup);
        mLogin = (Button) findViewById(R.id.btn_login);

        //full sceen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        settingViewPager();

        intentMethod();
        Map<String, String> timestamp = ServerValue.TIMESTAMP;


        Log.d(TAG, "onCreate: timestamp " + ServerValue.TIMESTAMP);


    }

        //----------------------------------- setting up view pager -------------------------------------------------------------
        private void settingViewPager(){
            IntroductionViewPagerAdapter introductionViewPagerAdapter = new IntroductionViewPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(introductionViewPagerAdapter);
            mTabLayout.setupWithViewPager(mViewPager, true);
        }

    //----------------------------------- intent method -------------------------------------------------------------
        private void intentMethod(){
            mSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IntroductionActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            });

            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IntroductionActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
}
