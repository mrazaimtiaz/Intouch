package com.intouchapp.intouch.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
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
import com.intouchapp.intouch.Main.Chats.ChatsActivity;
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Explore.ExploreActivity;
import com.intouchapp.intouch.Main.Notification.NotificationActivity;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SharedPreManager;
import com.intouchapp.intouch.Utills.MainViewPagerAdapter;
import com.intouchapp.intouch.Utills.UserClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    //widget
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private ImageView mExplore,mNotifcation,mMessage,mAccount,mRedNotifivation,mRedRequest;


    //variable
    private static final String TAG = "MainActivity";
    String device_token;
    private Context mContext;




    //firebase
    FirebaseFirestore mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initalization
        mViewPager  = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        mExplore = (ImageView) findViewById(R.id.iv_explore);
        mNotifcation = (ImageView) findViewById(R.id.iv_notification);
        mMessage = (ImageView) findViewById(R.id.iv_message);
        mAccount = (ImageView) findViewById(R.id.iv_account);

        mRedNotifivation = (ImageView) findViewById(R.id.iv_red_notification);
        mRedRequest = (ImageView) findViewById(R.id.iv_red_request);



        mContext = MainActivity.this;

        settingViewPager();

        mDb = FirebaseFirestore.getInstance();

        deviceToken();
        intentMethod();
        checkNorification();

    }

    @Override
    protected void onResume() {
        super.onResume();
        settingViewPager();
        checkNorification();

    }

    //----------------------------------- setting up view pager -------------------------------------------------------------
    private void settingViewPager(){
        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }

    //--------------------------------- setting device token ----------------------------------------------------
    private void deviceToken(){
        if(SharedPreManager.getInstance(MainActivity.this).getToken() != null) {
            device_token = SharedPreManager.getInstance(MainActivity.this).getToken();
            Map<String,Object> map = new HashMap<>();
            map.put(getString(R.string.device_token),device_token);
            try {
                mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }

    //----------------------------------- intent method -------------------------------------------------------------
    private void intentMethod(){
        mExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ExploreActivity.class);
                startActivity(intent);
            }
        });

        mNotifcation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NotificationActivity.class);
                startActivity(intent);
            }
        });
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatsActivity.class);
                startActivity(intent);
            }
        });

        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    //------------------------------------ check notification -----------------------------------------------------
    private void checkNorification(){
        final boolean[] notify = {true};
        final boolean[] req = {true};
        try {
            mDb.collection(getString(R.string.collection_notification)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(int i = 0; i < list.size(); i++){
                            Notification notification = list.get(i).toObject(Notification.class);
                            assert notification != null;
                            if(!notification.getR_id().equals(notification.getS_id())){
                                if(notification.getR_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) && notification.getStatus().equals(getString(R.string.status_unread))){
                                    mRedNotifivation.setVisibility(View.VISIBLE);
                                    notify[0] = false;
                                }
                            }
                            if(list.size() -1 == i){
                                if(notify[0]){
                                    mRedNotifivation.setVisibility(View.GONE);
                                }
                            }

                        }
                    }
                }
            });
            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        final User user = task.getResult().toObject(User.class);
                        mDb.collection(getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d(TAG, "onComplete: get task");

                                List<DocumentSnapshot> requestlist = Objects.requireNonNull(task.getResult()).getDocuments();

                                for(int j = 0; j < requestlist.size() ; j++){
                                    Request request = requestlist.get(j).toObject(Request.class);
                                    if(user != null){
                                        assert request != null;
                                        if((request.getR_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) || request.getR_id().equals(user.getH_id())) && request.getStatus().equals(getString(R.string.request_send))){
                                            mRedRequest.setVisibility(View.VISIBLE);
                                            req[0] = false;
                                        }
                                    }
                                    if(requestlist.size() -1 == j){
                                        if(req[0]){
                                            mRedRequest.setVisibility(View.GONE);
                                        }
                                    }

                                }
                            }
                        });
                    }

                }
            });

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
