package com.intouchapp.intouch.Main.Posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.Comment;
import com.intouchapp.intouch.Models.ListofStrings;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.CommentsRecyclerAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostActivity extends AppCompatActivity {

    //widget
    private RecyclerView mRecyclerView;

    //widget
    private TextView mMemberName;
    private ImageView mMemberAvatar;
    private  ImageView mEllipse;
    private  TextView mPrivacy;
    private   TextView time;
    private  ImageView mType;
    private   ImageView mLike;
    private  ImageView mClickedLike;
    private   ImageView mPost;
    private  TextView mComment;
    private  TextView mTotalComment;
    private TextView mTotalLike;
    private  ImageView mImageComment;
    private  ImageView mImageLike;
    private  ImageView mBack;

    private CircleImageView mAvatar;
    private EditText mWriteComment;
    private ImageView mSendMessage;

    private String typeMember;



    ConstraintLayout mConstraintLayout;

    //variable
    private ListofStrings list;
    private Post posts;
    private Context mContext;
    private List<String> relativeList,freindList,memberlist;
    private List<Comment> comment;
    private boolean publicType;
    Drawable colorType;

    User ownUser;

    private RequestQueue mRequestQue;

    CommentsRecyclerAdapter commentsRecyclerAdapter;

    private String URL = "https://fcm.googleapis.com/fcm/send";


    //Firebase
    FirebaseFirestore mDb;

    private static final String TAG = "ViewPostActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.constraint_recycler);

        mMemberAvatar = (ImageView) findViewById(R.id.iv_member_avatar);
        mMemberName = (TextView) findViewById(R.id.tv_name);
        mEllipse = (ImageView) findViewById(R.id.iv_ellipse);
        time = (TextView) findViewById(R.id.tv_time);
        mPrivacy = (TextView) findViewById(R.id.tv_privacy);
        mType = (ImageView) findViewById(R.id.iv_type);
        mLike = (ImageView) findViewById(R.id.iv_like);
        mClickedLike = (ImageView) findViewById(R.id.iv_clicked_like);
        mPost = (ImageView) findViewById(R.id.iv_post);
        mComment = (TextView) findViewById(R.id.tv_comment);
        mTotalComment = (TextView) findViewById(R.id.tv_number_comment);
        mTotalLike = (TextView) findViewById(R.id.tv_number_like);
        mImageComment = (ImageView) findViewById(R.id.iv_total_comment);
        mImageLike = (ImageView) findViewById(R.id.iv_total_like);
        mBack = (ImageView) findViewById(R.id.iv_back);

        mAvatar = (CircleImageView) findViewById(R.id.iv_avatar);
        mWriteComment = (EditText) findViewById(R.id.et_comment);
        mSendMessage = (ImageView) findViewById(R.id.iv_sendmessage);


        mDb = FirebaseFirestore.getInstance();

        comment = new ArrayList<>();
        mContext = ViewPostActivity.this;
        publicType = true;

        typeMember = getString(R.string.empty);

        AndroidThreeTen.init(this);
        mRequestQue = Volley.newRequestQueue(mContext);



        mConstraintLayout.setVisibility(View.VISIBLE);


        list =  Objects.requireNonNull(getIntent().getExtras()).getParcelable(getString(R.string.intent_type));
        if(list != null){
            relativeList = list.getRelativeList();
            freindList = list.getFriendList();
            memberlist = list.getMemberList();
        }
        posts =  getIntent().getExtras().getParcelable(getString(R.string.post));
        Log.d(TAG, "onCreate: " + list.getFriendList() + list.getMemberList() + list.getRelativeList());

        mSendMessage.setEnabled(false);
        mSendMessage.setAlpha(0.5f);

        ownUser = ((UserClient) (mContext.getApplicationContext())).getUser();


        intentMethod();
        init();
        sendMessage();
        onTextChange();
        getComments();

        if(ownUser.getPublic_avatar() != null){
            UniversalImageLoader.setImage(ownUser.getRel_avatar(), mAvatar, null, "", mContext);
        }

    }
    //*************************************************** getComments *********************************************
    private void getComments(){
        try {
            mDb.collection(getString(R.string.collection_post)).document(posts.getP_id()).collection(getString(R.string.collection_comment)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                    for(int i = 0; i < list.size() ; i++){
                        comment.add(list.get(i).toObject(Comment.class));
                        if(i == list.size()-1){
                            settingRecylerView(comment);
                        }

                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //*************************************************** settingrecylerview **************************************
    private void  settingRecylerView(List<Comment> comment){
        if(comment != null){
            if(comment.size() != 0){
                Log.d(TAG, "settingRecyclerView: " + comment);
                commentsRecyclerAdapter =  new CommentsRecyclerAdapter(comment,relativeList,freindList,memberlist,mContext);
                mRecyclerView.setAdapter(commentsRecyclerAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            }
        }
    }
    //*************************************************** onTextChange *********************************************
    private void onTextChange(){
        mWriteComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mWriteComment.getText().toString().isEmpty()){
                    mSendMessage.setEnabled(false);
                    mSendMessage.setAlpha(0.5f);
                }else{
                    mSendMessage.setEnabled(true);
                    mSendMessage.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mWriteComment.getText().toString().isEmpty()){
                    mSendMessage.setEnabled(false);
                    mSendMessage.setAlpha(0.5f);
                }else{
                    mSendMessage.setEnabled(true);
                    mSendMessage.setAlpha(1f);
                }
            }
        });
    }
    //****************************************************************** send message **************************************************
    private void sendMessage(){

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mWriteComment.getText().toString().isEmpty()){
                    hideKeyboard(ViewPostActivity.this);
                    ZoneId tz =  ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                    int nanos = localDateTime.getNano();
                    Timestamp timestamp = new Timestamp(seconds, nanos);

                    final Comment newcomment = new Comment();
                    newcomment.setComment(mWriteComment.getText().toString());
                    newcomment.setP_id(posts.getP_id());
                    newcomment.setTimestamp(timestamp);
                    newcomment.setU_id(FirebaseAuth.getInstance().getUid());

                    try {
                        mDb.collection(getString(R.string.collection_post)).document(posts.getP_id()).collection(getString(R.string.collection_comment)).add(newcomment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                mWriteComment.setText("");
                                mDb.collection(getString(R.string.collection_post)).document(posts.getP_id()).collection(getString(R.string.collection_comment)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> list;
                                        list = Objects.requireNonNull(task.getResult()).getDocuments();
                                        comment.add(newcomment);
                                        int newMsgPosition = comment.size() - 1;

                                        commentsRecyclerAdapter.notifyItemInserted(newMsgPosition);
                                        mRecyclerView.scrollToPosition(newMsgPosition);


                                        final Notification notification = new Notification();
                                        ZoneId tz =  ZoneId.systemDefault();
                                        LocalDateTime localDateTime = LocalDateTime.now();
                                        long seconds = localDateTime.atZone(tz).toEpochSecond();
                                        int nanos = localDateTime.getNano();
                                        Timestamp timestamp = new Timestamp(seconds, nanos);
                                        String n_id = mDb.collection(getString(R.string.collection_notification)).document().getId();
                                        notification.setId(n_id);
                                        notification.setC_id(posts.getP_id());
                                        notification.setCategory(mContext.getString(R.string.category_post));
                                        notification.setS_id(FirebaseAuth.getInstance().getUid());
                                        notification.setR_id(posts.getU_id());
                                        notification.setType(mContext.getString(R.string.notify_type_comment));
                                        notification.setStatus(mContext.getString(R.string.status_unread));
                                        notification.setTimestamp(timestamp);

                                        mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDb.collection(mContext.getString(R.string.collection_users)).document(posts.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                        assert user != null;
                                                        sendNotificationComment(user.getDevice_token());
                                                    }
                                                });
                                            }
                                        });

                                    }


                                });
                            }
                        });
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    //****************************************************************** intent method ***************************************************
    private void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //********************************************************************* init ***************************************************************
    @SuppressLint("SetTextI18n")
    private void init(){
        if(list != null && posts != null){

            try {
                mDb.collection(mContext.getString(R.string.collection_users)).document(posts.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: 1" + posts);
                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            assert user != null;
                            mMemberName.setText(user.getName());
                            Log.d(TAG, "onComplete: 1" + user.getName());

                            if(relativeList != null){
                                Log.d(TAG, "onComplete: relative not null" + relativeList);
                                if(relativeList.size() != 0){
                                    if(relativeList.contains(posts.getU_id())){
                                        Log.d(TAG, "onComplete:viewpost relative contatins");
                                        publicType = false;
                                        UniversalImageLoader.setImage(user.getRel_avatar(), mMemberAvatar, null, "",mContext);
                                        mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                        typeMember = getString(R.string.type_relative);
                                    }
                                }
                            }
                            if(freindList != null){
                                if(freindList.size() != 0){
                                    Log.d(TAG, "onComplete:viewpost friend not null" + freindList);
                                    if(freindList.contains(posts.getU_id())){
                                        publicType = false;
                                        Log.d(TAG, "onComplete: viewpost friend contatins");
                                        UniversalImageLoader.setImage(user.getFriend_avatar(),mMemberAvatar, null, "",mContext);
                                        mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                        typeMember = getString(R.string.type_friend);
                                    }
                                }
                            }
                            if(memberlist != null){
                                Log.d(TAG, "onComplete:viewpost member not null" + memberlist);

                                if(memberlist.size() != 0){
                                    if(memberlist.contains(posts.getU_id())){
                                        publicType = false;
                                        Log.d(TAG, "onComplete:viewpost member contatins");
                                        UniversalImageLoader.setImage(user.getFamily_avatar(),mMemberAvatar, null, "",mContext);
                                        mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                        typeMember = getString(R.string.type_member);
                                    }
                                }
                            }
                            if(publicType){
                                Log.d(TAG, "onComplete:viewpost others");
                                typeMember = getString(R.string.type_other);
                                UniversalImageLoader.setImage(user.getPublic_avatar(), mMemberAvatar, null, "",mContext);
                            }
                        }
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            Log.d(TAG, "onBindViewHolder: " + posts.getType() + posts.getShareWith());
            if(posts.getType().equals(mContext.getString(R.string.type_member))){
                mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));
                mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px);
            }
            if(posts.getType().equals(mContext.getString(R.string.type_relative))){
                mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_relative_red));
                mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px);
            }
            if(posts.getType().equals(mContext.getString(R.string.type_friend))){
                mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_friends_blue));
                mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px);

            }
            if(posts.getType().equals(mContext.getString(R.string.type_knower))){
                mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_knower_magenta));
                mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.magenta));
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px);

            }
            if(posts.getType().equals(mContext.getString(R.string.type_hood))){
                mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_castle_with_flag_yellow));
                mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px);

            }
            if(posts.getType().equals(mContext.getString(R.string.type_other))){
                mClickedLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_public_black));
                mPrivacy.setTextColor(ContextCompat.getColor(mContext,R.color.black));
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                colorType = ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px);

            }
            if(posts.getLike() != null){
                if(posts.getLike().size() != 0){
                    if(posts.getLike().contains(FirebaseAuth.getInstance().getUid())){
                        mImageLike.setVisibility(View.VISIBLE);
                        mClickedLike.setVisibility(View.VISIBLE);
                        mLike.setVisibility(View.GONE);
                    }else {
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
                        mImageLike.setVisibility(View.VISIBLE);
                        mLike.setVisibility(View.VISIBLE);
                        mClickedLike.setVisibility(View.GONE);
                    }
                }else{
                    mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
                    mImageLike.setVisibility(View.VISIBLE);
                    mLike.setVisibility(View.VISIBLE);
                    mClickedLike.setVisibility(View.GONE);
                }
            }else {
                mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
                mLike.setVisibility(View.VISIBLE);
                mClickedLike.setVisibility(View.GONE);


            }
            mPrivacy.setText(posts.getType());
            long seconds = (System.currentTimeMillis() / 1000) - posts.getTimestamp().getSeconds();
            Log.d(TAG, "onBindViewHolder: " + seconds);
            long diffMinutes = seconds / 60;
            long diffHours = diffMinutes / 60;
            long diffDays = diffHours / 24;

            if(seconds < 60){
                time.setText (seconds + mContext.getString(R.string.seconds_ago));
            }else if(diffMinutes < 60){
                time.setText(diffMinutes +  mContext.getString(R.string.minutes_ago));
            }else if(diffHours < 24){
                time.setText(diffHours + mContext.getString(R.string.hours_ago));
            }else if(diffHours < 48){
                time.setText(mContext.getString(R.string.yesterday));
            }
            else{
                time.setText(diffDays + mContext.getString(R.string.days_ago));
            }

            try {
                mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).collection(mContext.getString(R.string.collection_comment)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            if(list.size() != 0){
                                Log.d(TAG, "onComplete: comment size not 0");
                                mTotalComment.setText("" + list.size());
                            }else {
                                Log.d(TAG, "onComplete: comment size is 0");
                                mTotalComment.setText("" + 0);
                            }
                        }else {
                            Log.d(TAG, "onComplete: comment task is unsuccessful");
                            mTotalComment.setText("" + 0);
                        }
                    }
                });
                mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).collection(mContext.getString(R.string.collection_comment)).get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: comment faliure");
                        mTotalComment.setText("" + 0);
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }


            UniversalImageLoader.setImage(posts.getImage(),mPost, null, "",mContext);
            mComment.setText(posts.getDescription());
            if(posts.getLike() != null){
                Log.d(TAG, "onBindViewHolder: into the like" );
                if(posts.getLike().size() != 0){
                    Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                    mTotalLike.setText("" + posts.getLike().size());
                }else{
                    mTotalLike.setText("" + 0);
                }
            }else{
                mTotalLike.setText("" + 0);
            }

            mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> likes = new ArrayList<>();
                    mClickedLike.setVisibility(View.VISIBLE);
                    mLike.setEnabled(false);
                    mImageLike.setBackground(colorType);
                    mClickedLike.setEnabled(false);


                    if(posts.getType().equals(mContext.getString(R.string.type_member))){
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                    }
                    if(posts.getType().equals(mContext.getString(R.string.type_relative))){
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                    }
                    if(posts.getType().equals(mContext.getString(R.string.type_friend))){
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                    }
                    if(posts.getType().equals(mContext.getString(R.string.type_knower))){
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                    }
                    if(posts.getType().equals(mContext.getString(R.string.type_hood))){
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                    }
                    if(posts.getType().equals(mContext.getString(R.string.type_other))){
                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                    }


                    if(posts.getLike() != null){
                        Log.d(TAG, "onBindViewHolder: into the like" );
                        if(posts.getLike().size() != 0){
                            Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                            int size= posts.getLike().size() + 1;
                            mTotalLike.setText("" + size);
                        }else{
                            mTotalLike.setText("" + 1);
                        }
                    }else{
                        mTotalLike.setText("" + 1);
                    }


                    if(posts.getLike() != null){
                        Log.d(TAG, "onComplete: not null" + posts.getLike().size());
                        posts.getLike().add(FirebaseAuth.getInstance().getUid());
                        mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).set(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mClickedLike.setEnabled(true);
                                    mClickedLike.setVisibility(View.VISIBLE);
                                    mLike.setVisibility(View.GONE);
                                    mLike.setEnabled(true);


                                    final Notification notification = new Notification();
                                    ZoneId tz =  ZoneId.systemDefault();
                                    LocalDateTime localDateTime = LocalDateTime.now();
                                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                                    int nanos = localDateTime.getNano();
                                    Timestamp timestamp = new Timestamp(seconds, nanos);
                                    String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                    notification.setId(n_id);
                                    notification.setC_id(posts.getP_id());
                                    notification.setCategory(mContext.getString(R.string.category_post));
                                    notification.setS_id(FirebaseAuth.getInstance().getUid());
                                    notification.setR_id(posts.getU_id());
                                    notification.setType(mContext.getString(R.string.notify_type_like));
                                    notification.setStatus(mContext.getString(R.string.status_unread));
                                    notification.setTimestamp(timestamp);

                                    try {
                                        mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDb.collection(mContext.getString(R.string.collection_users)).document(posts.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    mClickedLike.setEnabled(true);
                                    mClickedLike.setVisibility(View.GONE);
                                    mLike.setVisibility(View.VISIBLE);
                                    mLike.setEnabled(true);
                                    mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                    if(posts.getLike() != null){
                                        Log.d(TAG, "onBindViewHolder: into the like" );
                                        if(posts.getLike().size() != 0){
                                            Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                                            int size= posts.getLike().size() - 1;
                                            mTotalLike.setText("" + size);
                                        }else{
                                            mTotalLike.setText("" + 0);
                                        }
                                    }else{
                                        mTotalLike.setText("" + 0);
                                    }
                                }
                            }
                        });
                        try {
                            mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).set(posts).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                    mClickedLike.setEnabled(true);
                                    mClickedLike.setVisibility(View.GONE);
                                    mLike.setVisibility(View.VISIBLE);
                                    mLike.setEnabled(true);
                                    mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                    if(posts.getLike() != null){
                                        Log.d(TAG, "onBindViewHolder: into the like" );
                                        if(posts.getLike().size() != 0){
                                            Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                                            int size= posts.getLike().size() - 1;
                                            mTotalLike.setText("" + size);
                                        }else{
                                            mTotalLike.setText("" + 0);
                                        }
                                    }else{
                                        mTotalLike.setText("" + 0);
                                    }

                                }
                            });
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                    }else{
                        Log.d(TAG, "onComplete: null");
                        likes.add(FirebaseAuth.getInstance().getUid());
                        posts.setLike(likes);
                        try {
                            mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).set(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mClickedLike.setEnabled(true);
                                        mClickedLike.setVisibility(View.VISIBLE);
                                        mLike.setVisibility(View.GONE);
                                        mLike.setEnabled(true);


                                    }else{
                                        mClickedLike.setEnabled(true);
                                        mClickedLike.setVisibility(View.GONE);
                                        mLike.setVisibility(View.VISIBLE);
                                        mLike.setEnabled(true);
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                        if(posts.getLike() != null){
                                            Log.d(TAG, "onBindViewHolder: into the like" );
                                            if(posts.getLike().size() != 0){
                                                Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                                                int size= posts.getLike().size() - 1;
                                                mTotalLike.setText("" + size);
                                            }else{
                                                mTotalLike.setText("" + 0);
                                            }
                                        }else{
                                            mTotalLike.setText("" + 0);
                                        }
                                    }
                                }
                            });
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                       try {
                           mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).set(posts).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.d(TAG, "onFailure: " + e.getMessage());
                                   mClickedLike.setEnabled(true);
                                   mClickedLike.setVisibility(View.GONE);
                                   mLike.setVisibility(View.VISIBLE);
                                   mLike.setEnabled(true);
                                   mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                                   if(posts.getLike() != null){
                                       Log.d(TAG, "onBindViewHolder: into the like" );
                                       if(posts.getLike().size() != 0){
                                           Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                                           int size= posts.getLike().size() - 1;
                                           mTotalLike.setText("" + size);
                                       }else{
                                           mTotalLike.setText("" + 0);
                                       }
                                   }else{
                                       mTotalLike.setText("" + 0);
                                   }
                               }
                           });
                       }catch (NullPointerException e){
                           e.printStackTrace();
                       }
                    }
                }
            });

            mClickedLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLike.setVisibility(View.VISIBLE);
                    mClickedLike.setVisibility(View.GONE);
                    mClickedLike.setEnabled(false);
                    mLike.setEnabled(false);
                    mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));

                    if(posts.getLike() != null){
                        Log.d(TAG, "onBindViewHolder: into the like" );
                        if(posts.getLike().size() != 0){
                            Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                            int size= posts.getLike().size() - 1;
                            mTotalLike.setText("" + size);
                        }else{
                            mTotalLike.setText("" + 0);
                        }
                    }else{
                        mTotalLike.setText("" + 0);
                    }
                    if(posts.getLike() != null){
                        posts.getLike().remove(FirebaseAuth.getInstance().getUid());

                        try {
                            mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).set(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mLike.setVisibility(View.VISIBLE);
                                        mLike.setEnabled(true);
                                        mClickedLike.setVisibility(View.GONE);
                                        mClickedLike.setEnabled(true);



                                        try {
                                            mDb.collection(mContext.getString(R.string.collection_notification)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                        for (int i = 0; i < list.size(); i++){
                                                            Notification notification1 = list.get(i).toObject(Notification.class);
                                                            assert notification1 != null;
                                                            if(notification1.getR_id().equals(posts.getU_id())
                                                                    && notification1.getC_id().equals(posts.getP_id())
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
                                        mClickedLike.setEnabled(true);
                                        mLike.setVisibility(View.VISIBLE);
                                        mClickedLike.setVisibility(View.GONE);
                                        mClickedLike.setEnabled(true);

                                        if(posts.getType().equals(mContext.getString(R.string.type_member))){
                                            mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                                        }
                                        if(posts.getType().equals(mContext.getString(R.string.type_relative))){
                                            mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                                        }
                                        if(posts.getType().equals(mContext.getString(R.string.type_friend))){
                                            mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                                        }
                                        if(posts.getType().equals(mContext.getString(R.string.type_knower))){
                                            mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                                        }
                                        if(posts.getType().equals(mContext.getString(R.string.type_hood))){
                                            mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                                        }
                                        if(posts.getType().equals(mContext.getString(R.string.type_other))){
                                            mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                                        }

                                        if(posts.getLike() != null){
                                            Log.d(TAG, "onBindViewHolder: into the like" );
                                            if(posts.getLike().size() != 0){
                                                Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                                                int size= posts.getLike().size() + 1;
                                                mTotalLike.setText("" + size);
                                            }else{
                                                mTotalLike.setText("" + 1);
                                            }
                                        }else{
                                            mTotalLike.setText("" + 1);
                                        }
                                    }

                                }
                            });
                            mDb.collection(mContext.getString(R.string.collection_post)).document(posts.getP_id()).set(posts).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                    mClickedLike.setEnabled(true);
                                    mLike.setVisibility(View.VISIBLE);
                                    mClickedLike.setVisibility(View.GONE);
                                    mClickedLike.setEnabled(true);

                                    if(posts.getType().equals(mContext.getString(R.string.type_member))){
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritegreen_24px));
                                    }
                                    if(posts.getType().equals(mContext.getString(R.string.type_relative))){
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritered_24px));
                                    }
                                    if(posts.getType().equals(mContext.getString(R.string.type_friend))){
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblue_24px));
                                    }
                                    if(posts.getType().equals(mContext.getString(R.string.type_knower))){
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoritemagenta_24px));
                                    }
                                    if(posts.getType().equals(mContext.getString(R.string.type_hood))){
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteyellow_24px));
                                    }
                                    if(posts.getType().equals(mContext.getString(R.string.type_other))){
                                        mImageLike.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favoriteblack_24px));
                                    }

                                    if(posts.getLike() != null){
                                        Log.d(TAG, "onBindViewHolder: into the like" );
                                        if(posts.getLike().size() != 0){
                                            Log.d(TAG, "onBindViewHolder: " + posts.getLike());
                                            mTotalLike.setText("" + posts.getLike().size() + 1);
                                        }else{
                                            mTotalLike.setText("" + 1);
                                        }
                                    }else{
                                        mTotalLike.setText("" + 1);
                                    }
                                }
                            });
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                }
            });


        }
    }


    public void memberInfo(View view) {
        if (posts.getU_id() != null) {
            try {
                if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(posts.getU_id())){
                    Intent intent = new Intent(mContext, AccountActivity.class);
                    startActivity(intent);
                }else{
                    if(!typeMember.equals(getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);

                        intent.putExtra(getString(R.string.intent_type),typeMember);
                        intent.putExtra(getString(R.string.intent_memberid),posts.getU_id());
                        startActivity(intent);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

    }
    //*********************************************** send notification *****************************************************
    private void sendNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.title_post));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_like)) ;
            json.put("notification",notificationObj);
            notificationObj.put("sound","default");



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
    //*********************************************** send notification *****************************************************
    private void sendNotificationComment(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.title_post));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_comment)) ;
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
}
