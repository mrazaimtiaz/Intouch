package com.intouchapp.intouch.Utills;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intouchapp.intouch.Main.Posts.ViewPostActivity;
import com.intouchapp.intouch.Models.ListofStrings;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.MyOwnHolder> {

    //variable
    private List<Post> posts;
    private List<String> relativeList,freindList,memberlist;

    Context mContext;
    boolean publicType;
    Drawable colorType;

    private RequestQueue mRequestQue;

    private String URL = "https://fcm.googleapis.com/fcm/send";



    FirebaseFirestore mDb;

    private static final String TAG = "MemberRecyclerViewAdapt";


    public PostRecyclerAdapter(List<Post> posts,List<String> relativeList,List<String> friendList,List<String> memberList, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");

        mDb = FirebaseFirestore.getInstance();
        this.relativeList = relativeList;
        this.freindList = friendList;
        this.memberlist = memberList;
        this.posts = posts;
        mContext = cnt;
        publicType = true;
        Log.d(TAG, "PostRecyclerAdapter: " + relativeList + friendList + memberList);

        mRequestQue = Volley.newRequestQueue(mContext);
        AndroidThreeTen.init(mContext);



    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_post,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(posts.get(position).getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: 1" + posts);
                        User user = task.getResult().toObject(User.class);
                        holder.mMemberName.setText(user.getName());
                        Log.d(TAG, "onComplete: 1" + user.getName());
                        if(relativeList != null){
                            Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                            if(relativeList.size() != 0){
                                if(relativeList.contains(posts.get(position).getU_id())){
                                    publicType = false;
                                    UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                }
                            }
                        }
                        if(freindList != null){
                            Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                            if(freindList.size() != 0){
                                if(freindList.contains(posts.get(position).getU_id())){
                                    publicType = false;

                                    UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                }
                            }
                        }
                        if(memberlist != null){
                            Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                            if(memberlist.size() != 0){
                                if(memberlist.contains(posts.get(position).getU_id())){
                                    publicType = false;
                                    UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                }
                            }
                        }
                        if(publicType){
                            UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG, "onBindViewHolder: " + posts.get(position).getType() + posts.get(position).getShareWith());
        if(posts.get(position).getType().equals(mContext.getString(R.string.type_member))){
            holder.mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));
            holder.mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
            colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px);
        }
        if(posts.get(position).getType().equals(mContext.getString(R.string.type_relative))){
            holder.mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_relative_red));
            holder.mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
            colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px);
        }
        if(posts.get(position).getType().equals(mContext.getString(R.string.type_friend))){
            holder.mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_friends_blue));
            holder.mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
            colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px);

        }
        if(posts.get(position).getType().equals(mContext.getString(R.string.type_knower))){
            holder.mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_knower_magenta));
            holder.mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.magenta));
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
            colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px);

        }
        if(posts.get(position).getType().equals(mContext.getString(R.string.type_hood))){
            holder.mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_castle_with_flag_yellow));
            holder.mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
            colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px);

        }
        if(posts.get(position).getType().equals(mContext.getString(R.string.type_other))){
            holder.mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_public_black));
            holder.mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.black));
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
            colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px);

        }
        if(posts.get(position).getLike() != null){
            if(posts.get(position).getLike().size() != 0){
                if(posts.get(position).getLike().contains(FirebaseAuth.getInstance().getUid())){
                    holder.mImageLike.setVisibility(View.VISIBLE);
                    holder.mClickedLike.setVisibility(View.VISIBLE);
                    holder.mLike.setVisibility(View.GONE);
                }else {
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
                    holder.mImageLike.setVisibility(View.VISIBLE);
                    holder.mLike.setVisibility(View.VISIBLE);
                    holder.mClickedLike.setVisibility(View.GONE);
                }
            }else{
                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
                holder.mImageLike.setVisibility(View.VISIBLE);
                holder.mLike.setVisibility(View.VISIBLE);
                holder.mClickedLike.setVisibility(View.GONE);
            }
        }else {
            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
            holder.mLike.setVisibility(View.VISIBLE);
            holder.mClickedLike.setVisibility(View.GONE);


        }
        holder.mPrivacy.setText(posts.get(position).getType());
        long seconds = (System.currentTimeMillis() / 1000) - posts.get(position).getTimestamp().getSeconds();
        Log.d(TAG, "onBindViewHolder: " + seconds);
        long diffMinutes = seconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        if(seconds < 60){
            holder.time.setText (seconds + mContext.getString(R.string.seconds_ago));
        }else if(diffMinutes < 60){
            holder.time.setText(diffMinutes +  mContext.getString(R.string.minutes_ago));
        }else if(diffHours < 24){
            holder.time.setText(diffHours + mContext.getString(R.string.hours_ago));
        }else if(diffHours < 48){
            holder.time.setText(mContext.getString(R.string.yesterday));
        }
        else{
            holder.time.setText(diffDays + mContext.getString(R.string.days_ago));
        }

        try{
            mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).collection(mContext.getString(R.string.collection_comment)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                        if(list.size() != 0){
                            Log.d(TAG, "onComplete: comment size not 0");
                            holder.mTotalComment.setText("" + list.size());
                        }else {
                            Log.d(TAG, "onComplete: comment size is 0");
                            holder.mTotalComment.setText("" + 0);
                        }
                    }else {
                        Log.d(TAG, "onComplete: comment task is unsuccessful");
                        holder.mTotalComment.setText("" + 0);
                    }
                }
            });
            mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).collection(mContext.getString(R.string.collection_comment)).get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: comment faliure");
                    holder.mTotalComment.setText("" + 0);
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        UniversalImageLoader.setImage(posts.get(position).getImage(), holder.mPost, null, "",mContext);
        holder.mComment.setText(posts.get(position).getDescription());
        if(posts.get(position).getLike() != null){
            Log.d(TAG, "onBindViewHolder: into the like" );
            if(posts.get(position).getLike().size() != 0){
                Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                holder.mTotalLike.setText("" + posts.get(position).getLike().size());
            }else{
                holder.mTotalLike.setText("" + 0);
            }
        }else{
            holder.mTotalLike.setText("" + 0);
        }

        holder.mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> likes = new ArrayList<>();
                holder.mClickedLike.setVisibility(View.VISIBLE);
                holder.mLike.setEnabled(false);
                holder.mImageLike.setBackground(colorType);
                holder.mClickedLike.setEnabled(false);


                if(posts.get(position).getType().equals(mContext.getString(R.string.type_member))){
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                }
                if(posts.get(position).getType().equals(mContext.getString(R.string.type_relative))){
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                }
                if(posts.get(position).getType().equals(mContext.getString(R.string.type_friend))){
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                }
                if(posts.get(position).getType().equals(mContext.getString(R.string.type_knower))){
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                }
                if(posts.get(position).getType().equals(mContext.getString(R.string.type_hood))){
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                }
                if(posts.get(position).getType().equals(mContext.getString(R.string.type_other))){
                    holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                }


                if(posts.get(position).getLike() != null){
                    Log.d(TAG, "onBindViewHolder: into the like" );
                    if(posts.get(position).getLike().size() != 0){
                        Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                        int size= posts.get(position).getLike().size() + 1;
                        holder.mTotalLike.setText("" + size);
                    }else{
                        holder.mTotalLike.setText("" + 1);
                    }
                }else{
                    holder.mTotalLike.setText("" + 1);
                }


                            if(posts.get(position).getLike() != null){
                                Log.d(TAG, "onComplete: not null" + posts.get(position).getLike().size());
                                posts.get(position).getLike().add(FirebaseAuth.getInstance().getUid());
                                mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).set(posts.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            holder.mClickedLike.setEnabled(true);
                                            holder.mClickedLike.setVisibility(View.VISIBLE);
                                            holder.mLike.setVisibility(View.GONE);
                                            holder.mLike.setEnabled(true);

                                            final Notification notification = new Notification();
                                            ZoneId tz =  ZoneId.systemDefault();
                                            LocalDateTime localDateTime = LocalDateTime.now();
                                            long seconds = localDateTime.atZone(tz).toEpochSecond();
                                            int nanos = localDateTime.getNano();
                                            Timestamp timestamp = new Timestamp(seconds, nanos);
                                            String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                            notification.setId(n_id);
                                            notification.setC_id(posts.get(position).getP_id());
                                            notification.setCategory(mContext.getString(R.string.category_post));
                                            notification.setS_id(FirebaseAuth.getInstance().getUid());
                                            notification.setR_id(posts.get(position).getU_id());
                                            notification.setType(mContext.getString(R.string.notify_type_like));
                                            notification.setStatus(mContext.getString(R.string.status_unread));
                                            notification.setTimestamp(timestamp);

                                            try {
                                                mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDb.collection(mContext.getString(R.string.collection_users)).document(posts.get(position).getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                assert user != null;
                                                                sendNotification(user.getDevice_token());
                                                            }
                                                        });
                                                    }
                                                });
                                            }catch (NullPointerException e){
                                                e.printStackTrace();
                                            }

                                        }else{
                                            holder.mClickedLike.setEnabled(true);
                                            holder.mClickedLike.setVisibility(View.GONE);
                                            holder.mLike.setVisibility(View.VISIBLE);
                                            holder.mLike.setEnabled(true);
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                            if(posts.get(position).getLike() != null){
                                                Log.d(TAG, "onBindViewHolder: into the like" );
                                                if(posts.get(position).getLike().size() != 0){
                                                    Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                                                    int size= posts.get(position).getLike().size() - 1;
                                                    holder.mTotalLike.setText("" + size);
                                                }else{
                                                    holder.mTotalLike.setText("" + 0);
                                                }
                                            }else{
                                                holder.mTotalLike.setText("" + 0);
                                            }
                                        }
                                    }
                                });
                                try {
                                    mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).set(posts.get(position)).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                            holder.mClickedLike.setEnabled(true);
                                            holder.mClickedLike.setVisibility(View.GONE);
                                            holder.mLike.setVisibility(View.VISIBLE);
                                            holder.mLike.setEnabled(true);
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                            if(posts.get(position).getLike() != null){
                                                Log.d(TAG, "onBindViewHolder: into the like" );
                                                if(posts.get(position).getLike().size() != 0){
                                                    Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                                                    int size= posts.get(position).getLike().size() - 1;
                                                    holder.mTotalLike.setText("" + size);
                                                }else{
                                                    holder.mTotalLike.setText("" + 0);
                                                }
                                            }else{
                                                holder.mTotalLike.setText("" + 0);
                                            }

                                        }
                                    });
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                            }else{
                                Log.d(TAG, "onComplete: null");
                                likes.add(FirebaseAuth.getInstance().getUid());
                                posts.get(position).setLike(likes);
                                try {
                                    mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).set(posts.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                holder.mClickedLike.setEnabled(true);
                                                holder.mClickedLike.setVisibility(View.VISIBLE);
                                                holder.mLike.setVisibility(View.GONE);
                                                holder.mLike.setEnabled(true);


                                            }else{
                                                holder.mClickedLike.setEnabled(true);
                                                holder.mClickedLike.setVisibility(View.GONE);
                                                holder.mLike.setVisibility(View.VISIBLE);
                                                holder.mLike.setEnabled(true);
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                                if(posts.get(position).getLike() != null){
                                                    Log.d(TAG, "onBindViewHolder: into the like" );
                                                    if(posts.get(position).getLike().size() != 0){
                                                        Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                                                        int size= posts.get(position).getLike().size() - 1;
                                                        holder.mTotalLike.setText("" + size);
                                                    }else{
                                                        holder.mTotalLike.setText("" + 0);
                                                    }
                                                }else{
                                                    holder.mTotalLike.setText("" + 0);
                                                }
                                            }
                                        }
                                    });
                                    mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).set(posts.get(position)).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                            holder.mClickedLike.setEnabled(true);
                                            holder.mClickedLike.setVisibility(View.GONE);
                                            holder.mLike.setVisibility(View.VISIBLE);
                                            holder.mLike.setEnabled(true);
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                            if(posts.get(position).getLike() != null){
                                                Log.d(TAG, "onBindViewHolder: into the like" );
                                                if(posts.get(position).getLike().size() != 0){
                                                    Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                                                    int size= posts.get(position).getLike().size() - 1;
                                                    holder.mTotalLike.setText("" + size);
                                                }else{
                                                    holder.mTotalLike.setText("" + 0);
                                                }
                                            }else{
                                                holder.mTotalLike.setText("" + 0);
                                            }
                                        }
                                    });
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                            }
            }
        });

        holder.mClickedLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mLike.setVisibility(View.VISIBLE);
                holder.mClickedLike.setVisibility(View.GONE);
                holder.mClickedLike.setEnabled(false);
                holder.mLike.setEnabled(false);
                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                if(posts.get(position).getLike() != null){
                    Log.d(TAG, "onBindViewHolder: into the like" );
                    if(posts.get(position).getLike().size() != 0){
                        Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                        int size= posts.get(position).getLike().size() - 1;
                        holder.mTotalLike.setText("" + size);
                    }else{
                        holder.mTotalLike.setText("" + 0);
                    }
                }else{
                    holder.mTotalLike.setText("" + 0);
                }
                            if(posts.get(position).getLike() != null){
                                posts.get(position).getLike().remove(FirebaseAuth.getInstance().getUid());
                                mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).set(posts.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            holder.mLike.setVisibility(View.VISIBLE);
                                            holder.mLike.setEnabled(true);
                                            holder.mClickedLike.setVisibility(View.GONE);
                                            holder.mClickedLike.setEnabled(true);



                                            try {
                                                mDb.collection(mContext.getString(R.string.collection_notification)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            List<DocumentSnapshot> list = Objects.requireNonNull(Objects.requireNonNull(task.getResult())).getDocuments();
                                                            for (int i = 0; i < list.size(); i++){
                                                                Notification notification1 = list.get(i).toObject(Notification.class);
                                                                assert notification1 != null;
                                                                if(notification1.getR_id().equals(posts.get(position).getU_id())
                                                                        && notification1.getC_id().equals(posts.get(position).getP_id())
                                                                        && notification1.getS_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                                        && notification1.getCategory().equals(mContext.getString(R.string.category_post))
                                                                        && notification1.getType().equals(mContext.getString(R.string.notify_type_like))){
                                                                    mDb.collection(mContext.getString(R.string.collection_notification)).document(list.get(i).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }catch (NullPointerException e){
                                                Log.d(TAG, "onComplete: NullPointerException like" + e.getMessage());
                                            }


                                        }else{
                                            holder.mClickedLike.setEnabled(true);
                                            holder.mLike.setVisibility(View.VISIBLE);
                                            holder.mClickedLike.setVisibility(View.GONE);
                                            holder.mClickedLike.setEnabled(true);

                                            if(posts.get(position).getType().equals(mContext.getString(R.string.type_member))){
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                                            }
                                            if(posts.get(position).getType().equals(mContext.getString(R.string.type_relative))){
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                                            }
                                            if(posts.get(position).getType().equals(mContext.getString(R.string.type_friend))){
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                                            }
                                            if(posts.get(position).getType().equals(mContext.getString(R.string.type_knower))){
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                                            }
                                            if(posts.get(position).getType().equals(mContext.getString(R.string.type_hood))){
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                                            }
                                            if(posts.get(position).getType().equals(mContext.getString(R.string.type_other))){
                                                holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                                            }

                                            if(posts.get(position).getLike() != null){
                                                Log.d(TAG, "onBindViewHolder: into the like" );
                                                if(posts.get(position).getLike().size() != 0){
                                                    Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                                                    int size= posts.get(position).getLike().size() + 1;
                                                    holder.mTotalLike.setText("" + size);
                                                }else{
                                                    holder.mTotalLike.setText("" + 1);
                                                }
                                            }else{
                                                holder.mTotalLike.setText("" + 1);
                                            }
                                        }

                                    }
                                });
                                mDb.collection(mContext.getString(R.string.collection_post)).document(posts.get(position).getP_id()).set(posts.get(position)).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        holder.mClickedLike.setEnabled(true);
                                        holder.mLike.setVisibility(View.VISIBLE);
                                        holder.mClickedLike.setVisibility(View.GONE);
                                        holder.mClickedLike.setEnabled(true);

                                        if(posts.get(position).getType().equals(mContext.getString(R.string.type_member))){
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                                        }
                                        if(posts.get(position).getType().equals(mContext.getString(R.string.type_relative))){
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                                        }
                                        if(posts.get(position).getType().equals(mContext.getString(R.string.type_friend))){
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                                        }
                                        if(posts.get(position).getType().equals(mContext.getString(R.string.type_knower))){
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                                        }
                                        if(posts.get(position).getType().equals(mContext.getString(R.string.type_hood))){
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                                        }
                                        if(posts.get(position).getType().equals(mContext.getString(R.string.type_other))){
                                            holder.mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                                        }

                                        if(posts.get(position).getLike() != null){
                                            Log.d(TAG, "onBindViewHolder: into the like" );
                                            if(posts.get(position).getLike().size() != 0){
                                                Log.d(TAG, "onBindViewHolder: " + posts.get(position).getLike());
                                                holder.mTotalLike.setText("" + posts.get(position).getLike().size() + 1);
                                            }else{
                                                holder.mTotalLike.setText("" + 1);
                                            }
                                        }else{
                                            holder.mTotalLike.setText("" + 1);
                                        }
                                    }
                                });
                            }

            }
        });
    }
    //*********************************************** send notification *****************************************************
    private void sendNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.title_post));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_like)) ;
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

    }

    private void viewItem(int position){
        Intent intent = new Intent(mContext, ViewPostActivity.class);
        ListofStrings list = new ListofStrings();
        list.setFriendList(freindList);
        list.setMemberList(memberlist);
        list.setRelativeList(relativeList);

        intent.putExtra(mContext.getString(R.string.intent_type),list);
        intent.putExtra(mContext.getString(R.string.post),posts.get(position));
        mContext.startActivity(intent);

    }
    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class MyOwnHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //widget
        TextView mMemberName;
        ImageView mMemberAvatar;
        ImageView mEllipse;
        TextView mPrivacy;
        TextView time;
        ImageView mType;
        ImageView mLike;
        ImageView mClickedLike;
        ImageView mPost;
        TextView mComment;
        TextView mTotalComment;
        TextView mTotalLike;
        ImageView mImageComment;
        ImageView mImageLike;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            mPrivacy = (TextView) itemView.findViewById(R.id.tv_privacy);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);
            mLike = (ImageView) itemView.findViewById(R.id.iv_like);
            mClickedLike = (ImageView) itemView.findViewById(R.id.iv_clicked_like);
            mPost = (ImageView) itemView.findViewById(R.id.iv_post);
            mComment = (TextView) itemView.findViewById(R.id.tv_comment);
            mTotalComment = (TextView) itemView.findViewById(R.id.tv_number_comment);
            mTotalLike = (TextView) itemView.findViewById(R.id.tv_number_like);
            mImageComment = (ImageView) itemView.findViewById(R.id.iv_total_comment);
            mImageLike = (ImageView) itemView.findViewById(R.id.iv_total_like);


            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            viewItem(getAdapterPosition());
        }
    }
}
