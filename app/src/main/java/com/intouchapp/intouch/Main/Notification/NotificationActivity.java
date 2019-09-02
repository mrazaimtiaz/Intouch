package com.intouchapp.intouch.Main.Notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Explore.ExploreActivity;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.NotificationRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NotificationActivity extends AppCompatActivity {


    //widget
    private RecyclerView mRecyclerView;

    private ConstraintLayout mNothing,mMain,mProgress;

    private Context mContext;

    private TextView mTitle;
    private ImageView mLogo,mExplore,mAccount,mMessage,mRedRequest;

    private List<Notification> notifications = new ArrayList<>();

    private List<String> freindList = new ArrayList<>();
    private List<String> memberList = new ArrayList<>();
    private List<String> relativeList = new ArrayList<>();

    //firebase
    private FirebaseFirestore mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTitle = (TextView) findViewById(R.id.tv_title);

        mLogo = (ImageView) findViewById(R.id.iv_intouch_logo);

        mExplore = (ImageView) findViewById(R.id.iv_explore);
        mAccount = (ImageView) findViewById(R.id.iv_account);
        mMessage = (ImageView) findViewById(R.id.iv_requests);

        mNothing = (ConstraintLayout) findViewById(R.id.constraintLayoutNothing);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mRedRequest = (ImageView) findViewById(R.id.iv_red_request);

        mMain.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        mRedRequest.setVisibility(View.GONE);

        mContext = NotificationActivity.this;
        mDb = FirebaseFirestore.getInstance();

        intentMethod();
        gettingUserList();
        checkNorification();
    }
    //******************************************************** getting userdata ****************************************************
    private void gettingUserList() {

        try {
            mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                        try {
                            assert user != null;
                            mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                        assert house != null;
                                        if (house.getMembers() != null) {
                                            if (house.getMembers().size() != 0) {
                                                Log.d(TAG, "onComplete: get member list");
                                                memberList.addAll(house.getMembers());
                                                Log.d(TAG, "onComplete:memberList " + memberList);
                                            }
                                        }
                                    }
                                    mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                for (int i = 0; i < list.size(); i++) {
                                                    Relative relative = list.get(i).toObject(Relative.class);
                                                    assert relative != null;
                                                    if (relative.getR_id() != null) {
                                                        if (relative.getR_id().size() != 0)
                                                            Log.d(TAG, "onComplete: get relative list");
                                                        relativeList.addAll(relative.getR_id());
                                                        Log.d(TAG, "onComplete:relativeList " + relativeList);
                                                    }
                                                }
                                            }
                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                        for (int i = 0; i < list.size(); i++) {
                                                            Friend friend = list.get(i).toObject(Friend.class);
                                                            assert friend != null;
                                                            if (friend.getF_id() != null) {
                                                                if (friend.getF_id().size() != 0) {
                                                                    Log.d(TAG, "onComplete: get friend list");
                                                                    freindList.addAll(friend.getF_id());
                                                                    Log.d(TAG, "onComplete:freindList " + freindList);
                                                                }
                                                            }
                                                            if (i == list.size() - 1) {
                                                                settingNotification();

                                                            }
                                                        }
                                                    } if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                        settingNotification();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                        }
                    }

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    //*********************************** setting notification *******************************************
    private void settingNotification(){
        try {
            mDb.collection(getString(R.string.collection_notification)).orderBy(getString(R.string.timestamp)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(int i = 0; i < list.size(); i++){
                            Notification notification = list.get(i).toObject(Notification.class);
                            assert notification != null;

                            if(!notification.getR_id().equals(notification.getS_id())){

                                if(notification.getR_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Log.d(TAG, "onComplete: comment " + notification.getType());
                                    notifications.add(notification);
                                }

                            }
                            if(list.size() - 1 == i){
                                Log.d(TAG, "onComplete: called");
                                settingRecyclerView(notifications);
                            }
                        }
                    }if(Objects.requireNonNull(task.getResult()).size() == 0){
                        settingRecyclerView(notifications);
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
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
        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountActivity.class);
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
    //------------------------------------------------ setting recyclerview -------------------------------------------------------
    public void settingRecyclerView(List<Notification> notifications){

        if(notifications != null){
            if(notifications.size() != 0){
                mProgress.setVisibility(View.GONE);
                mMain.setVisibility(View.VISIBLE);
                Collections.reverse(notifications);
                mRecyclerView.setAdapter(new NotificationRecyclerAdapter(notifications,memberList,freindList,relativeList, mContext));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            }else{
                mProgress.setVisibility(View.GONE);
                mMain.setVisibility(View.VISIBLE);
                mNothing.setVisibility(View.VISIBLE);
            }
        }else{
            mProgress.setVisibility(View.GONE);
            mMain.setVisibility(View.VISIBLE);
            mNothing.setVisibility(View.VISIBLE);
        }
    }

    //------------------------------------ check notification -----------------------------------------------------
    private void checkNorification(){
        try {
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
