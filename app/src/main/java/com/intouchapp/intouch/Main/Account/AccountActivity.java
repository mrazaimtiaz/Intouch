package com.intouchapp.intouch.Main.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Chats.ChatsActivity;
import com.intouchapp.intouch.Main.Account.Knowers.KnowersActivity;
import com.intouchapp.intouch.Main.Account.Requests.RequestsActivity;
import com.intouchapp.intouch.Main.Explore.ExploreActivity;
import com.intouchapp.intouch.Main.Home.MainHouseInfoActivity;
import com.intouchapp.intouch.Main.Notification.NotificationActivity;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Main.Settings.SettingsActivity;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.UniversalImageLoader;

import java.util.List;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    //widget
    private ImageView ivUser;

    private TextView mName,mBio;

    private TextView mTitle;
    private ImageView mLogo,mExplore,mNotification,mMessage,mRedNotifivation,mRedRequest;

    private static final String TAG = "AccountActivity";

    private FirebaseFirestore mDb;

    private User ownUser;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ivUser = (ImageView) findViewById(R.id.iv_user);
        mName = (TextView) findViewById(R.id.tv_user_name);
        mBio = (TextView) findViewById(R.id.tv_user_bio);

        mTitle = (TextView) findViewById(R.id.tv_title);

        mLogo = (ImageView) findViewById(R.id.iv_intouch_logo);

        mExplore = (ImageView) findViewById(R.id.iv_explore);
        mNotification = (ImageView) findViewById(R.id.iv_notification);
        mMessage = (ImageView) findViewById(R.id.iv_requests);

        mRedNotifivation = (ImageView) findViewById(R.id.iv_red_notification);
        mRedRequest = (ImageView) findViewById(R.id.iv_red_request);

        mRedNotifivation.setVisibility(View.GONE);
        mRedRequest.setVisibility(View.GONE);


        mDb = FirebaseFirestore.getInstance();

        mContext = AccountActivity.this;
        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();

        if(ownUser != null){
            mName.setText(ownUser.getName());
            if(!ownUser.getBio().equals(getString(R.string.empty))){
                mBio.setText(ownUser.getBio());
            }
            UniversalImageLoader.setImage(ownUser.getFamily_avatar(), ivUser, null, "",mContext);
        }

        intentMethod();
        checkNorification();

    }
    //*************************************** intent method **********************************************
    private void intentMethod(){
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ExploreActivity.class);
                startActivity(intent);
            }
        });
        mNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NotificationActivity.class);
                startActivity(intent);
            }
        });
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, ChatsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void accountPostClick(View view) {
        Intent intent = new Intent(mContext,MyPostsActivity.class);
        intent.putExtra(getString(R.string.u_id), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        startActivity(intent);
    }

    public void accountEventClick(View view) {
        Intent intent = new Intent(mContext,MyEventsActivity.class);
        intent.putExtra(getString(R.string.u_id), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        startActivity(intent);
    }

    public void accountKnowerClick(View view) {
        Intent intent = new Intent(mContext, KnowersActivity.class);
        startActivity(intent);
    }

    public void accountRequestClick(View view) {
        Intent intent = new Intent(mContext, RequestsActivity.class);
        startActivity(intent);
    }

    public void accountHouseClick(View view) {
        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
        intent.putExtra(getString(R.string.house_id),ownUser.getH_id()+ownUser.getN_id()+getString(R.string.type_member));
        startActivity(intent);
    }

    public void accountSettingClick(View view) {
        Intent intent = new Intent(mContext, SettingsActivity.class);
        startActivity(intent);
    }

    //------------------------------------ check notification -----------------------------------------------------
    private void checkNorification(){
        try {
            mDb.collection(getString(R.string.collection_notification)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        final boolean[] notify = {true};
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(int i = 0; i < list.size(); i++){
                            Notification notification = list.get(i).toObject(Notification.class);
                            assert notification != null;
                            if(!notification.getR_id().equals(notification.getS_id())){
                                if(notification.getR_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) && notification.getStatus().equals(getString(R.string.status_unread))){
                                    mRedNotifivation.setVisibility(View.VISIBLE);
                                    notify[0] = false;
                                }
                            }if(list.size() -1 == i){
                                if(notify[0]){
                                    mRedNotifivation.setVisibility(View.GONE);
                                }
                            }

                        }
                    }
                }
            });
            mDb.collection(getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "onComplete: get task");
                    final boolean[] req = {true};

                    List<DocumentSnapshot> requestlist = Objects.requireNonNull(task.getResult()).getDocuments();

                    for(int j = 0; j < requestlist.size() ; j++){
                        Request request = requestlist.get(j).toObject(Request.class);
                        if(ownUser != null){
                            assert request != null;
                            if((request.getR_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) || request.getR_id().equals(ownUser.getH_id())) && request.getStatus().equals(getString(R.string.request_send))){
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
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
