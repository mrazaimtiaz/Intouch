package com.intouchapp.intouch.Main.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Chats.IndividualChatActivity;
import com.intouchapp.intouch.Main.Account.MyEventsActivity;
import com.intouchapp.intouch.Main.Account.MyPostsActivity;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.UniversalImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberInfoActivity extends AppCompatActivity {


    //variable
    private static final String TAG = "MemberInfoActivity";
    private String type,id;
    private Context mContext;
    private String tag;
    private String n_id;

    //widgets
    private ImageView mBack,mBroke;
    private CircleImageView mMemberInfo,mEllipse;
    private TextView mName,mBio,tvBroke;

    private Button mSendRequest,mDeclineRequest;
    private ConstraintLayout memberSection,mMain,mProgress;

    User ownUser;


    //Firebase
    FirebaseFirestore mDb;

    //notification
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";


    private boolean showNoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        mBack = (ImageView) findViewById(R.id.iv_back);
        mBroke = (ImageView) findViewById(R.id.iv_broke);

        mMemberInfo = (CircleImageView) findViewById(R.id.iv_memberinfo);
        mEllipse = (CircleImageView) findViewById(R.id.iv_ellipse);

        mName = (TextView) findViewById(R.id.tv_name);
        mBio = (TextView) findViewById(R.id.tv_member_bio);
        tvBroke = (TextView) findViewById(R.id.tv_broke);

        mSendRequest = (Button) findViewById(R.id.btn_friend_request);
        mDeclineRequest = (Button) findViewById(R.id.btn_cancel_request);
        memberSection = (ConstraintLayout) findViewById(R.id.member_section);

        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        mContext = MemberInfoActivity.this;
        ownUser = ((UserClient) (mContext.getApplicationContext())).getUser();

        showNoRequest = true;
        tag = getString(R.string.empty);
        n_id = getString(R.string.empty);
        if(getIntent().getStringExtra(getString(R.string.intent_type))!= null)
        type = getIntent().getStringExtra(getString(R.string.intent_type));
        if(getIntent().getStringExtra(getString(R.string.intent_memberid)) != null)
        id = getIntent().getStringExtra(getString(R.string.intent_memberid));


        showNoRequest = getIntent().getBooleanExtra(getString(R.string.intent_no_request),true);


        mDb = FirebaseFirestore.getInstance();

        //notification
        mRequestQue = Volley.newRequestQueue(this);

        Log.d(TAG, "onCreate: " + type +" " + id);

        checkType();
        intentMethod();
        new MemberInfo().execute();
        checkTag();

        if(!showNoRequest){
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
        }

    }
    //*************************************************** check type ******************************************************************
    private void checkType(){
        Log.d(TAG, "checkType: called");
        if(type.equals(getString(R.string.type_member))){

            mBroke.setEnabled(false);
            tvBroke.setEnabled(false);

            mBroke.setAlpha(0.5f);
            tvBroke.setAlpha(0.5f);

            mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
            memberSection.setVisibility(View.VISIBLE);
        }
        if(type.equals(getString(R.string.type_relative))){
            mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
            memberSection.setVisibility(View.VISIBLE);
        }
        if(type.equals(getString(R.string.type_friend))){
            mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
            memberSection.setVisibility(View.VISIBLE);
        }
        if(type.equals(getString(R.string.type_other))){
            mDeclineRequest.setVisibility(View.GONE);
            mSendRequest.setVisibility(View.VISIBLE);
            memberSection.setVisibility(View.GONE);
        }
        if(type.equals(getString(R.string.type_hood))){
            Log.d(TAG, "checkType: typehood");

            try {
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "onComplete: into the task");
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                        for(int i = 0; i < list.size(); i++){
                            Relative relative = list.get(i).toObject(Relative.class);
                            assert relative != null;
                            List<String> member = relative.getR_id();
                            Log.d(TAG, "onComplete: first for loop");
                            if(member.contains(id)){
                                Log.d(TAG, "onComplete: if loop matched");
                                type = getString(R.string.type_relative);
                                mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                        assert user != null;
                                        UniversalImageLoader.setImage(user.getRel_avatar(), mMemberInfo, null, "",mContext);
                                    }
                                });

                                checkType();
                            }else{
                                Log.d(TAG, "onComplete: 2nd databasee");
                                mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        Log.d(TAG, "onComplete: into the task2");
                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                        for(int i = 0; i < list.size(); i++) {
                                            Friend friend = list.get(i).toObject(Friend.class);
                                            assert friend != null;
                                            List<String> member = friend.getF_id();
                                            if(member.contains(id)){
                                                Log.d(TAG, "onComplete: mathced");
                                                type = getString(R.string.type_friend);
                                                mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                        assert user != null;
                                                        UniversalImageLoader.setImage(user.getFriend_avatar(), mMemberInfo, null, "",mContext);
                                                    }
                                                });
                                                checkType();
                                            }else{
                                                if(ownUser != null){
                                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                            assert house != null;
                                                            List<String> member = house.getMembers();
                                                            if (member.contains(id)) {
                                                                Log.d(TAG, "onComplete: mathced");
                                                                type = getString(R.string.type_member);
                                                                mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                        assert user != null;
                                                                        UniversalImageLoader.setImage(user.getFamily_avatar(), mMemberInfo, null, "",mContext);
                                                                    }
                                                                });
                                                                checkType();
                                                            } else {
                                                                mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                        assert user != null;
                                                                        UniversalImageLoader.setImage(user.getPublic_avatar(), mMemberInfo, null, "",mContext);
                                                                    }
                                                                });
                                                                type = getString(R.string.type_other);
                                                                checkType();
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(MemberInfoActivity.this, getString(R.string.toast_restart), Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }

                                            }
                                        }

                                    }
                                });
                            }

                        }
                    }
                });
            }catch (NullPointerException e){
                finish();
            }
        }
    }
    //************************************************ intent method ****************************************
    private void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //***************************************************** check house info ************************************
    private void checkMemberInfo(){
        Log.d(TAG, "checkHouseInfo: called");

        try {
            Tasks.await(mDb.collection(getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                    Log.d(TAG, "onComplete: on the database2");

                    for(int i = 0; i < list.size(); i++){
                        Log.d(TAG, "onComplete: on the loop2");
                        com.intouchapp.intouch.Models.Request request = list.get(i).toObject(com.intouchapp.intouch.Models.Request.class);
                        assert request != null;
                        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(request.getS_id()) && id.equals(request.getR_id()) && request.getType().equals(getString(R.string.type_friend)) && request.getStatus().equals(getString(R.string.request_send))){
                            Log.d(TAG, "onComplete: condition matched");
                            if(task.isSuccessful()){
                                mDeclineRequest.setVisibility(View.VISIBLE);
                                mSendRequest.setVisibility(View.GONE);
                            }else{
                                Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }
            }));

            Tasks.await(mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        assert user != null;
                        mName.setText(user.getName());
                        if(!user.getBio().equals(getString(R.string.empty)))
                            mBio.setText(user.getBio());

                        if(type.equals(getString(R.string.type_friend)))
                        UniversalImageLoader.setImage(user.getFriend_avatar(), mMemberInfo, null, "",mContext);

                        if(type.equals(getString(R.string.type_relative)))
                            UniversalImageLoader.setImage(user.getRel_avatar(), mMemberInfo, null, "",mContext);

                        if(type.equals(getString(R.string.type_member)))
                            UniversalImageLoader.setImage(user.getFamily_avatar(), mMemberInfo, null, "",mContext);

                        if(type.equals(getString(R.string.type_other)))
                            UniversalImageLoader.setImage(user.getPublic_avatar(), mMemberInfo, null, "",mContext);
                    }
                }
            }));




        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
            finish();
        }

    }
    //***************************************************** send request *************************************************
    private void sendRequest(){
        Log.d(TAG, "sendRequest: called");
        mSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mSendRequest.setEnabled(false);
                ZoneId tz =  ZoneId.systemDefault();
                LocalDateTime localDateTime = LocalDateTime.now();
                long seconds = localDateTime.atZone(tz).toEpochSecond();
                int nanos = localDateTime.getNano();
                Timestamp timestamp = new Timestamp(seconds, nanos);
                com.intouchapp.intouch.Models.Request request = new com.intouchapp.intouch.Models.Request();
                final String rid = mDb.collection(getString(R.string.collection_requests)).document().getId();
                request.setId(rid);
                request.setS_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                request.setR_id(id);
                request.setStatus(getString(R.string.request_send));
                request.setType(getString(R.string.type_friend));
                request.setTimeStamp(timestamp);

                try {
                    mDb.collection(getString(R.string.collection_requests)).document(rid).set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: ");
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: succesful");
                                mSendRequest.setVisibility(View.GONE);
                                mDeclineRequest.setVisibility(View.VISIBLE);
                                mSendRequest.setEnabled(true);

                                mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Log.d(TAG, "onComplete: for notification");
                                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                        assert user != null;
                                        sendNotification(user.getDevice_token());

                                    }
                                });

                                mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                        for(int i = 0; i < list.size() ; i++){

                                            final Hood hood = list.get(i).toObject(Hood.class);
                                            mDb.collection(getString(R.string.collection_hoods)).document(list.get(i).getId()).collection(getString(R.string.collection_houses)).whereArrayContains(getString(R.string.field_members),id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if(task.isSuccessful()){
                                                         List<DocumentSnapshot> houseList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                         for (int j = 0; j< houseList.size(); j++){

                                                             final House house = houseList.get(j).toObject(House.class);


                                                             assert house != null;
                                                             mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.r_id),house.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                     List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                     for (int j = 0; j< requestList.size(); j++){

                                                                         com.intouchapp.intouch.Models.Request request1 = requestList.get(j).toObject(com.intouchapp.intouch.Models.Request.class);


                                                                         if(request1.getS_id().equals(FirebaseAuth.getInstance().getUid())){
                                                                             mDb.collection(getString(R.string.collection_requests)).document(rid).delete();
                                                                         }
                                                                     }
                                                                 }
                                                             });
                                                         }

                                                    }
                                                }
                                            });
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
    }



    private void cancelRequest(){
        Log.d(TAG, "cancelRequest: called");
        mDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mDeclineRequest.setEnabled(false);
                    mDb.collection(getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: into the database");
                                List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                for(int i = 0; i < list.size(); i++){
                                    Log.d(TAG, "onComplete: into the for loop ");
                                    com.intouchapp.intouch.Models.Request request = list.get(i).toObject(com.intouchapp.intouch.Models.Request.class);
                                    assert request != null;
                                    Log.d(TAG, "onComplete: " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + " " + request.getS_id() + " reciver end " + id +" " + request.getR_id() +  " type "  + request.getType() + " " + getString(R.string.type_friend) + " " +request.getStatus() + " " + getString(R.string.request_send));

                                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(request.getS_id()) && id.equals(request.getR_id()) && request.getType().equals(getString(R.string.type_friend)) && request.getStatus().equals(getString(R.string.request_send))){
                                        Log.d(TAG, "onComplete: condition mathced");
                                        mDb.collection(getString(R.string.collection_requests)).document(list.get(i).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mDeclineRequest.setVisibility(View.GONE);
                                                    mSendRequest.setVisibility(View.VISIBLE);
                                                    mDeclineRequest.setEnabled(true);
                                                }else{
                                                    mDeclineRequest.setEnabled(true);
                                                    Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            }

                        }
                    });
                    mDb.collection(getString(R.string.collection_requests)).get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDeclineRequest.setEnabled(true);
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                    finish();
                }

            }
        });
    }
    //************************************************** check tag ****************************************************************
    private void checkTag(){
        try {
            mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    assert user != null;
                    tag = user.getH_id() + user.getN_id()+ type;
                    n_id = user.getN_id();
                    Log.d(TAG, "onComplete: tag: " + tag);
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void post(View view) {
        if(id != null){
            Intent intent = new Intent(mContext, MyPostsActivity.class);
            intent.putExtra(getString(R.string.u_id),id);
            startActivity(intent);
        }
    }

    public void event(View view) {
        if(id != null){
            Intent intent = new Intent(mContext, MyEventsActivity.class);
            intent.putExtra(getString(R.string.u_id),id);
            startActivity(intent);
        }
    }

    public void broke(View view) {
        settingDialogBox();

    }
    //******************************************** settingDialogBox ******************************************************
    private void settingDialogBox(){
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.broke))
                .setMessage(getString(R.string.dialog_broke_friend))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        try {
                            if(ownUser != null){
                                try {
                                    mMain.setVisibility(View.GONE);
                                    mProgress.setVisibility(View.VISIBLE);
                                    mDb.collection(getString(R.string.collection_users)).document(ownUser.getU_id()).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if(task.isSuccessful()){
                                                List<DocumentSnapshot> friendList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                for (int i = 0; i < friendList.size(); i++){


                                                    Friend friend = friendList.get(i).toObject(Friend.class);
                                                    assert friend != null;
                                                    if(friend.getF_id() != null){
                                                        if(friend.getF_id().contains(id)){
                                                            if(friend.getF_id().size() == 1){
                                                                mDb.collection(getString(R.string.collection_users)).document(ownUser.getU_id()).collection(getString(R.string.collection_friends)).document(friendList.get(i).getId()).delete();
                                                            }else{
                                                                friend.getF_id().remove(id);
                                                                mDb.collection(getString(R.string.collection_users)).document(ownUser.getU_id()).collection(getString(R.string.collection_friends)).document(friendList.get(i).getId()).set(friend);
                                                            }

                                                            Intent intent = new Intent(mContext, MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            finish();

                                                            mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.field_status),getString(R.string.request_accept)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                    List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                    for(int i = 0; i < requestList.size(); i++){
                                                                        com.intouchapp.intouch.Models.Request request = requestList.get(i).toObject(com.intouchapp.intouch.Models.Request.class);

                                                                        assert request != null;
                                                                        if(request.getType().equals(getString(R.string.type_friend)) && request.getS_id().equals(ownUser.getU_id()) && request.getR_id().equals(id)){
                                                                            mDb.collection(getString(R.string.collection_requests)).document(requestList.get(i).getId()).delete();
                                                                            Log.d(TAG, "onComplete: deleted 1");
                                                                            dialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                }
                                            }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                mMain.setVisibility(View.VISIBLE);
                                                mProgress.setVisibility(View.GONE);
                                                Toast.makeText(mContext, getString(R.string.some_thing_went_wrong), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });


                                    mDb.collection(getString(R.string.collection_users)).document(id).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if(task.isSuccessful()){
                                                List<DocumentSnapshot> friendList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                for (int i = 0; i < friendList.size(); i++){

                                                    Friend friend = friendList.get(i).toObject(Friend.class);
                                                    assert friend != null;
                                                    if(friend.getF_id() != null){
                                                        if(friend.getF_id().contains(ownUser.getU_id())){
                                                            if(friend.getF_id().size() == 1){
                                                                mDb.collection(getString(R.string.collection_users)).document(id).collection(getString(R.string.collection_friends)).document(friendList.get(i).getId()).delete();
                                                            }else{
                                                                friend.getF_id().remove(ownUser.getU_id());
                                                                mDb.collection(getString(R.string.collection_users)).document(id).collection(getString(R.string.collection_friends)).document(friendList.get(i).getId()).set(friend);
                                                            }


                                                            mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.field_status),getString(R.string.request_accept)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                    List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                    for(int i = 0; i < requestList.size(); i++){
                                                                        com.intouchapp.intouch.Models.Request request = requestList.get(i).toObject(com.intouchapp.intouch.Models.Request.class);

                                                                        assert request != null;
                                                                        if(request.getType().equals(getString(R.string.type_friend)) && request.getS_id().equals(id) && request.getR_id().equals(ownUser.getU_id())){
                                                                            mDb.collection(getString(R.string.collection_requests)).document(requestList.get(i).getId()).delete();
                                                                            Log.d(TAG, "onComplete: deleted 2");
                                                                            dialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });




                                                        }
                                                    }

                                                }
                                            }if(task.getResult().size() == 0){
                                                mMain.setVisibility(View.VISIBLE);
                                                mProgress.setVisibility(View.GONE);
                                                Toast.makeText(mContext, getString(R.string.some_thing_went_wrong), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });


                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                            }else{
                                Toast.makeText(mContext, getString(R.string.some_thing_went_wrong), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }



                        }catch (NullPointerException e){

                        }
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void message(View view) {
        if(id != null){
            Intent intent = new Intent(mContext, IndividualChatActivity.class);
            intent.putExtra(mContext.getString(R.string.intent_memberid),id);
            intent.putExtra(mContext.getString(R.string.intent_type),type);
            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
            mContext.startActivity(intent);
        }
    }

    public void houseInfo(View view) {
        try {
            mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    assert user != null;
                    tag = user.getH_id() + user.getN_id()+ type;
                    n_id = user.getN_id();
                    Log.d(TAG, "onComplete: tag: " + tag);
                    Intent intent= new Intent(mContext,MainHouseInfoActivity.class);
                    Log.d(TAG, "houseInfo: tag" + tag);
                    intent.putExtra(getString(R.string.house_id),tag);
                    startActivity(intent);
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void hoodInfo(View view) {
        if(!n_id.equals(getString(R.string.empty))){
            Intent intent = new Intent(mContext,HoodInfoActivity.class);
            intent.putExtra(getString(R.string.n_id),n_id);
            startActivity(intent);
        }
    }

    //************************************************* getting house info ****************************************
    @SuppressLint("StaticFieldLeak")
    class MemberInfo extends AsyncTask<Void, String, Void> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        MemberInfo(){

        }
        @Override
        protected void onPreExecute() {


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Void doInBackground(Void... myBundle) {
            Log.d(TAG, "doInBackground: ");
            checkMemberInfo();
            Log.d(TAG, "doInBackground: n_id " + id);
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void createHouse) {
            Log.d(TAG, "onpost onComplete: n_id is " + id);
            sendRequest();
            cancelRequest();

        }
    }
    //*********************************************** send notification *****************************************************
    private void sendNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",getString(R.string.title_request));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + getString(R.string.request_body)) ;
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

}
