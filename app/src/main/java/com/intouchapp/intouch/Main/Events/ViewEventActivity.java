package com.intouchapp.intouch.Main.Events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Main.Posts.ViewPostActivity;
import com.intouchapp.intouch.Models.Comment;
import com.intouchapp.intouch.Models.Event;
import com.intouchapp.intouch.Models.ListofStrings;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.CommentsRecyclerAdapter;
import com.intouchapp.intouch.Utills.EventRecyclerAdapter;
import com.intouchapp.intouch.Utills.JoinedRecyclerAdapter;
import com.intouchapp.intouch.Utills.UniversalImageLoader;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewEventActivity extends AppCompatActivity {

    //widget
    private RecyclerView mRecyclerView;
    private TextView mCategory;

    ConstraintLayout mConstraintLayout;
    TextView mPlace,mAttendent,mName,mComment,mDate,mTime;
    ImageView mEvent;
    Button mAccept,mReject,mJoined;

    //variable
    private ListofStrings list;
    private Event events;
    private Context mContext;
    private List<String> relativeList,freindList,memberlist;
    private List<Comment> comment;
    private boolean publicType;
    Drawable colorType;

    //firebase
    private FirebaseFirestore mDb;


    private static final String TAG = "ViewEventActivity";

    private String typeMember;

    User ownUser;

    private RequestQueue mRequestQue;

    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCategory = (TextView) findViewById(R.id.tv_category);
        mCategory.bringToFront();
        mContext = ViewEventActivity.this;
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.constraint_recycler);
        mPlace = (TextView) findViewById(R.id.tv_place);
        mAttendent = (TextView) findViewById(R.id.tv_attendent);
        mName = (TextView) findViewById(R.id.tv_name);
        mComment = (TextView) findViewById(R.id.tv_description);
        mDate = (TextView) findViewById(R.id.event_date);
        mTime = (TextView) findViewById(R.id.tv_event_time);
        mEvent = (ImageView) findViewById(R.id.iv_event);
        mAccept = (Button) findViewById(R.id.btn_accept);
        mReject = (Button) findViewById(R.id.btn_reject);
        mJoined = (Button) findViewById(R.id.btn_joined);


        mDb = FirebaseFirestore.getInstance();

        comment = new ArrayList<>();

        publicType = true;

        typeMember = getString(R.string.empty);

        AndroidThreeTen.init(this);
        mRequestQue = Volley.newRequestQueue(mContext);



        list =  Objects.requireNonNull(getIntent().getExtras()).getParcelable(getString(R.string.intent_type));
        if(list != null){
            relativeList = list.getRelativeList();
            freindList = list.getFriendList();
            memberlist = list.getMemberList();
        }
        events =  getIntent().getExtras().getParcelable(getString(R.string.post));
        Log.d(TAG, "onCreate: " + list.getFriendList() + list.getMemberList() + list.getRelativeList());

        Log.d(TAG, "onCreate: " + events + events.getJoined());

        init();
        settingRecyclerView();
    }
    //********************************************************************* init ***************************************************************
    private void init(){
        if(list != null && events != null){
            Log.d(TAG, "init: called" + events.getDate());

            try {
                mDb.collection(mContext.getString(R.string.collection_users)).document(events.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: 1" + events);
                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            assert user != null;
                            mName.setText(user.getName());
                            Log.d(TAG, "onComplete: 1" + user.getName());
                            if(relativeList != null){
                                Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                                if(relativeList.size() != 0){
                                    if(relativeList.contains(events.getU_id())){
                                        publicType = false;
                                        mName.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                                        mName.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, MemberInfoActivity.class);

                                                intent.putExtra(getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                intent.putExtra(getString(R.string.intent_memberid),events.getU_id());
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            }
                            if(freindList != null){
                                Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                                if(freindList.size() != 0){
                                    if(freindList.contains(events.getU_id())){
                                        publicType = false;
                                        mName.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                                        mName.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, MemberInfoActivity.class);

                                                intent.putExtra(getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                intent.putExtra(getString(R.string.intent_memberid),events.getU_id());
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            }
                            if(memberlist != null){
                                Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                                if(memberlist.size() != 0){
                                    if(memberlist.contains(events.getU_id())){
                                        publicType = false;
                                        if(events.getU_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            mName.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(mContext, AccountActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }else{
                                            mName.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(mContext, MemberInfoActivity.class);

                                                    intent.putExtra(getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                    intent.putExtra(getString(R.string.intent_memberid),events.getU_id());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        mName.setTextColor(ContextCompat.getColor(mContext,R.color.green));

                                    }
                                }
                            }
                            if(publicType){
                                mName.setTextColor(ContextCompat.getColor(mContext,R.color.black));
                                mName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, MemberInfoActivity.class);

                                        intent.putExtra(getString(R.string.intent_type),mContext.getString(R.string.type_other));
                                        intent.putExtra(getString(R.string.intent_memberid),events.getU_id());
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            UniversalImageLoader.setImage(events.getImage(), mEvent, null, "",mContext);

            if(events.getType().equals(mContext.getString(R.string.type_member))){
                mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                mAttendent.setText(mContext.getString(R.string.family));
            }
            if(events.getType().equals(mContext.getString(R.string.type_relative))){
                mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                mAttendent.setText(mContext.getString(R.string.relative));
            }
            if(events.getType().equals(mContext.getString(R.string.type_friend))){
                mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                mAttendent.setText(mContext.getString(R.string.friend));
            }
            if(events.getType().equals(mContext.getString(R.string.type_knower))){
                mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.magenta));
                mAttendent.setText(mContext.getString(R.string.knowers));
            }
            if(events.getType().equals(mContext.getString(R.string.type_hood))){
                mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
                mAttendent.setText(mContext.getString(R.string.hood));
            }
            if(events.getType().equals(mContext.getString(R.string.type_other))){
                mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.black));
                mAttendent.setText(mContext.getString(R.string.public_));
            }

            mCategory.setText(events.getCategory());
            mPlace.setText(events.getPlace());
            mComment.setText(events.getDescription());
            mDate.setText(events.getDate());
            mTime.setText(events.getTime());

            try {
                mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Event event = Objects.requireNonNull(task.getResult()).toObject(Event.class);

                        assert event != null;
                        if(event.getRejected() != null){
                            if(event.getRejected().size() != 0){
                                if(event.getRejected().contains(FirebaseAuth.getInstance().getUid())){
                                    mAccept.setVisibility(View.GONE);
                                    mReject.setVisibility(View.GONE);
                                    mJoined.setVisibility(View.GONE);
                                }else{
                                    if(event.getJoined() != null){
                                        if(event.getJoined().size() != 0){
                                            if(event.getJoined().contains(FirebaseAuth.getInstance().getUid())){
                                                mJoined.setVisibility(View.VISIBLE);
                                                mReject.setVisibility(View.GONE);
                                                mAccept.setVisibility(View.GONE);
                                            }else{
                                                mReject.setVisibility(View.VISIBLE);
                                                mAccept.setVisibility(View.VISIBLE);
                                            }
                                        }else{ mReject.setVisibility(View.VISIBLE);
                                            mAccept.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        mReject.setVisibility(View.VISIBLE);
                                        mAccept.setVisibility(View.VISIBLE);
                                    }
                                }
                            }else{
                                if(event.getJoined() != null){
                                    if(event.getJoined().size() != 0){
                                        if(event.getJoined().contains(FirebaseAuth.getInstance().getUid())){
                                            mJoined.setVisibility(View.VISIBLE);
                                            mReject.setVisibility(View.GONE);
                                            mAccept.setVisibility(View.GONE);
                                        }else{
                                            mReject.setVisibility(View.VISIBLE);
                                            mAccept.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        mReject.setVisibility(View.VISIBLE);
                                        mAccept.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    mReject.setVisibility(View.VISIBLE);
                                    mAccept.setVisibility(View.VISIBLE);
                                }
                            }
                        }else{
                            if(event.getJoined() != null){
                                if(event.getJoined().size() != 0){
                                    if(event.getJoined().contains(FirebaseAuth.getInstance().getUid())){
                                        mJoined.setVisibility(View.VISIBLE);
                                        mReject.setVisibility(View.GONE);
                                        mAccept.setVisibility(View.GONE);
                                    }else{
                                        mReject.setVisibility(View.VISIBLE);
                                        mAccept.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    mReject.setVisibility(View.VISIBLE);
                                    mAccept.setVisibility(View.VISIBLE);
                                }
                            }else{
                                mReject.setVisibility(View.VISIBLE);
                                mAccept.setVisibility(View.VISIBLE);
                            }
                        }


                    }
                });

            }catch (NullPointerException e){
                e.printStackTrace();
            }


            mAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mAccept.setVisibility(View.GONE);
                        mReject.setVisibility(View.GONE);
                        mJoined.setVisibility(View.VISIBLE);

                        mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                List<String> id = new ArrayList<>();
                                final Event event = Objects.requireNonNull(task.getResult()).toObject(Event.class);
                                assert event != null;
                                if(event.getJoined() != null){
                                    if(event.getJoined().size() != 0){
                                        event.getJoined().add(FirebaseAuth.getInstance().getUid());
                                        mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    final Notification notification = new Notification();
                                                    ZoneId tz =  ZoneId.systemDefault();
                                                    LocalDateTime localDateTime = LocalDateTime.now();
                                                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                                                    int nanos = localDateTime.getNano();
                                                    Timestamp timestamp = new Timestamp(seconds, nanos);
                                                    String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                                    notification.setId(n_id);
                                                    notification.setC_id(events.getE_id());
                                                    notification.setCategory(mContext.getString(R.string.category_event));
                                                    notification.setS_id(FirebaseAuth.getInstance().getUid());
                                                    notification.setR_id(events.getU_id());
                                                    notification.setType(mContext.getString(R.string.notify_type_accept));
                                                    notification.setStatus(mContext.getString(R.string.status_unread));
                                                    notification.setTimestamp(timestamp);

                                                    mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            mDb.collection(mContext.getString(R.string.collection_users)).document(events.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                    assert user != null;
                                                                    sendNotification(user.getDevice_token());
                                                                }
                                                            });
                                                        }
                                                    });

                                                }else{
                                                    mAccept.setVisibility(View.VISIBLE);
                                                    mReject.setVisibility(View.VISIBLE);
                                                    mJoined.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }else{
                                        id.add(FirebaseAuth.getInstance().getUid());
                                        event.setJoined(id);
                                        mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    final Notification notification = new Notification();
                                                    ZoneId tz =  ZoneId.systemDefault();
                                                    LocalDateTime localDateTime = LocalDateTime.now();
                                                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                                                    int nanos = localDateTime.getNano();
                                                    Timestamp timestamp = new Timestamp(seconds, nanos);
                                                    String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                                    notification.setId(n_id);
                                                    notification.setC_id(events.getE_id());
                                                    notification.setCategory(mContext.getString(R.string.category_event));
                                                    notification.setS_id(FirebaseAuth.getInstance().getUid());
                                                    notification.setR_id(events.getU_id());
                                                    notification.setType(mContext.getString(R.string.notify_type_accept));
                                                    notification.setStatus(mContext.getString(R.string.status_unread));
                                                    notification.setTimestamp(timestamp);

                                                    mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            mDb.collection(mContext.getString(R.string.collection_users)).document(events.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                    assert user != null;
                                                                    sendNotification(user.getDevice_token());
                                                                }
                                                            });
                                                        }
                                                    });


                                                }else{
                                                    mAccept.setVisibility(View.VISIBLE);
                                                    mReject.setVisibility(View.VISIBLE);
                                                    mJoined.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    id.add(FirebaseAuth.getInstance().getUid());
                                    event.setJoined(id);
                                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                final Notification notification = new Notification();
                                                ZoneId tz =  ZoneId.systemDefault();
                                                LocalDateTime localDateTime = LocalDateTime.now();
                                                long seconds = localDateTime.atZone(tz).toEpochSecond();
                                                int nanos = localDateTime.getNano();
                                                Timestamp timestamp = new Timestamp(seconds, nanos);

                                                String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                                notification.setId(n_id);
                                                notification.setC_id(events.getE_id());
                                                notification.setCategory(mContext.getString(R.string.category_event));
                                                notification.setS_id(FirebaseAuth.getInstance().getUid());
                                                notification.setR_id(events.getU_id());
                                                notification.setType(mContext.getString(R.string.notify_type_accept));
                                                notification.setStatus(mContext.getString(R.string.status_unread));
                                                notification.setTimestamp(timestamp);

                                                mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDb.collection(mContext.getString(R.string.collection_users)).document(events.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                assert user != null;
                                                                sendNotification(user.getDevice_token());
                                                            }
                                                        });
                                                    }
                                                });

                                            }else{
                                                mAccept.setVisibility(View.VISIBLE);
                                                mReject.setVisibility(View.VISIBLE);
                                                mJoined.setVisibility(View.GONE);
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
            });

            mReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                List<String> id = new ArrayList<>();
                                Event event = Objects.requireNonNull(task.getResult()).toObject(Event.class);
                                assert event != null;
                                if(event.getRejected() != null){
                                    if(event.getRejected().size() != 0){
                                        event.getRejected().add(FirebaseAuth.getInstance().getUid());
                                        mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }else{
                                                    mAccept.setVisibility(View.VISIBLE);
                                                    mReject.setVisibility(View.VISIBLE);
                                                    mJoined.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }else{
                                        id.add(FirebaseAuth.getInstance().getUid());
                                        event.setRejected(id);
                                        mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }else{
                                                    mAccept.setVisibility(View.VISIBLE);
                                                    mReject.setVisibility(View.VISIBLE);
                                                    mJoined.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    id.add(FirebaseAuth.getInstance().getUid());
                                    event.setRejected(id);
                                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }else{
                                                mAccept.setVisibility(View.VISIBLE);
                                                mReject.setVisibility(View.VISIBLE);
                                                mJoined.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    mAccept.setVisibility(View.GONE);
                    mReject.setVisibility(View.GONE);

                }
            });

        }
    }
    //*********************************************** send notification *****************************************************
    private void sendNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.event));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_accept)) ;
            notificationObj.put("sound","default");

            json.put("notification",notificationObj);



            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("MUR", "onResponse: ");
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AIzaSyAMzSvgFOknCphW8nUgks6fmTi7zAhLdBg");

                    return header;
                }
            };
            mRequestQue.add(request);

        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

    }


    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(){
        if(events.getJoined() != null){
            if((events.getJoined().size() != 0)){
                Log.d(TAG, "settingRecyclerView: " + (events.getJoined()));
                mRecyclerView.setAdapter(new JoinedRecyclerAdapter((events.getJoined()),relativeList,freindList,memberlist,mContext));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            }
        }
    }

}
