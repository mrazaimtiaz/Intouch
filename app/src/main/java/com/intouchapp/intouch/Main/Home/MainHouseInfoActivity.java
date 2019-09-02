package com.intouchapp.intouch.Main.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.intouchapp.intouch.Main.Events.SingleShareEventActivity;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Main.Posts.SingleSharePostActivity;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.MemberRecyclerViewAdapter;
import com.intouchapp.intouch.Utills.UniversalImageLoader;
import com.intouchapp.intouch.Utills.UserClient;
import com.jakewharton.threetenabp.AndroidThreeTen;

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

public class MainHouseInfoActivity extends AppCompatActivity {

    private static final String TAG = "MainHouseInfoActivity";

    //widget
    private ConstraintLayout mRelativeSection,mFamilySection,mMain,mProgress;
    private RecyclerView mRecyclerView;
    private ImageView mBack;
    private CircleImageView mEllipse,mHouseInfo;

    private Button mSendRequest,mDeclineRequest;

    private TextView mBio,mName;

      //variable
    private String h_id;
    private String n_id;
    private String type;
    private Context mContext;

    //firebase
    FirebaseFirestore mDb;
    //temp
    private boolean friend;

    private User ownUser;

    //notification
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    private boolean showNoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_house_info);

        //intialization
        mRelativeSection = (ConstraintLayout) findViewById(R.id.relative_section);
        mFamilySection = (ConstraintLayout) findViewById(R.id.family_section);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        mBack = (ImageView) findViewById(R.id.iv_back);
        mEllipse = (CircleImageView) findViewById(R.id.iv_ellipse);
        mHouseInfo = (CircleImageView) findViewById(R.id.iv_houseinfo);

        mSendRequest = (Button) findViewById(R.id.btn_relative_request);
        mDeclineRequest = (Button) findViewById(R.id.btn_cancel_request);

        mBio = (TextView) findViewById(R.id.tv_house_bio);
        mName = (TextView) findViewById(R.id.tv_house_name);

        mContext = MainHouseInfoActivity.this;
        mDb = FirebaseFirestore.getInstance();
        friend = false;

        try {
            ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        AndroidThreeTen.init(this);
        showNoRequest = true;
        //notification
        mRequestQue = Volley.newRequestQueue(this);
        showNoRequest = getIntent().getBooleanExtra(getString(R.string.intent_no_request),true);
        String intent = getIntent().getStringExtra(getString(R.string.house_id));
        h_id = getString(R.string.empty);
        n_id = getString(R.string.empty);
        type = getString(R.string.empty);
        if(intent != null){
            h_id =  intent.substring(0, 20);
            n_id =  intent.substring(20, 40);
            type = intent.substring(40, intent.length());
        }


        new HouseInfo().execute();

        Log.d(TAG, "onCreate: " + h_id + " " + n_id + " " + type);
        final int sdk = android.os.Build.VERSION.SDK_INT;


        checkType();
        intentMethod();

        if(!showNoRequest){
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
        }

    }
    //*************************************************** check type ******************************************************************
    private void checkType(){
        if(type.equals(getString(R.string.type_member))){
            mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
            mFamilySection.setVisibility(View.VISIBLE);
            mRelativeSection.setVisibility(View.GONE);
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
        }
        if(type.equals(getString(R.string.type_relative))){
            mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
            mFamilySection.setVisibility(View.GONE);
            mRelativeSection.setVisibility(View.VISIBLE);
            mSendRequest.setVisibility(View.GONE);
            mDeclineRequest.setVisibility(View.GONE);
        }
        if(type.equals(getString(R.string.type_friend))){
            mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
            mFamilySection.setVisibility(View.GONE);
            mRelativeSection.setVisibility(View.GONE);
            mSendRequest.setVisibility(View.VISIBLE);
            friend = true;
        }
        if(type.equals(getString(R.string.type_other))){
            mFamilySection.setVisibility(View.GONE);
            mRelativeSection.setVisibility(View.GONE);
            mSendRequest.setVisibility(View.VISIBLE);
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
    private void checkHouseInfo(){
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
                        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(request.getS_id()) && h_id.equals(request.getR_id()) && request.getType().equals(getString(R.string.type_relative)) && request.getStatus().equals(getString(R.string.request_send))){
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

            Tasks.await(mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                        assert house != null;
                        mName.setText(house.getName());
                        if(!house.getBio().equals(getString(R.string.empty)))
                            mBio.setText(house.getBio());

                        UniversalImageLoader.setImage(house.getImage(), mHouseInfo, null, "",mContext);
                        settingRecycler(house.getMembers());
                    }
                }
            }));




        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    //***************************************** setting recycler view ********************************************
    private void settingRecycler(List<String> member){
        if(member != null){
            mRecyclerView.setAdapter(new MemberRecyclerViewAdapter(null,null,null,member, mContext,type,getString(R.string.intent_member)));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        }
    }
    //***************************************************** send request *************************************************
    private void sendRequest(){
        Log.d(TAG, "sendRequest: called");
        mSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Log.d(TAG, "onClick: ");
                    mSendRequest.setEnabled(false);
                    ZoneId tz =  ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                    int nanos = localDateTime.getNano();
                    Timestamp timestamp = new Timestamp(seconds, nanos);
                    com.intouchapp.intouch.Models.Request request = new com.intouchapp.intouch.Models.Request();

                    String id = mDb.collection(getString(R.string.collection_requests)).document().getId();
                    request.setId(id);
                    request.setS_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    request.setR_id(h_id);
                    request.setStatus(getString(R.string.request_send));
                    request.setType(getString(R.string.type_relative));
                    request.setTimeStamp(timestamp);


                    mDb.collection(getString(R.string.collection_requests)).document(id).set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: ");
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: succesful");
                                mSendRequest.setVisibility(View.GONE);
                                mDeclineRequest.setVisibility(View.VISIBLE);
                                mSendRequest.setEnabled(true);

                                mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Log.d(TAG, "onComplete: for notification");
                                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                        assert house != null;
                                        final List<String> members = house.getMembers();
                                        for(int i = 0; i < members.size() ; i++){

                                            mDb.collection(getString(R.string.collection_users)).document(members.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                    assert user != null;
                                                    sendNotification(user.getDevice_token());
                                                }
                                            });

                                            mDb.collection(getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if(task.isSuccessful()){
                                                        final House ownHouse = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                        for(int i = 0; i < members.size() ; i++){

                                                            mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.s_id),members.get(i)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                    for (int q = 0; q < list.size() ; q++){

                                                                        com.intouchapp.intouch.Models.Request request1 = list.get(q).toObject(com.intouchapp.intouch.Models.Request.class);

                                                                        assert ownHouse != null;
                                                                        for(int j = 0; j < ownHouse.getMembers().size(); j++){
                                                                            assert request1 != null;
                                                                            if(request1.getR_id().equals(ownHouse.getMembers().get(j))){

                                                                                mDb.collection(getString(R.string.collection_requests)).document(list.get(q).getId()).delete();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                        }

                                                        for(int i = 0; i < ownHouse.getMembers().size() ; i++){

                                                            mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.s_id),ownHouse.getMembers().get(i)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                    for (int q = 0; q < list.size() ; q++){

                                                                        com.intouchapp.intouch.Models.Request request1 = list.get(q).toObject(com.intouchapp.intouch.Models.Request.class);

                                                                        assert members != null;
                                                                        for(int j = 0; j < members.size(); j++){
                                                                            assert request1 != null;
                                                                            if(request1.getR_id().equals(members.get(j))){

                                                                                mDb.collection(getString(R.string.collection_requests)).document(list.get(q).getId()).delete();
                                                                            }
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
                                    Log.d(TAG, "onComplete: " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + " " + request.getS_id() + " reciver end " + h_id +" " + request.getR_id() +  " type "  + request.getType() + " " + getString(R.string.type_relative) + " " +request.getStatus() + " " + getString(R.string.request_send));

                                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(request.getS_id()) && h_id.equals(request.getR_id()) && request.getType().equals(getString(R.string.type_relative)) && request.getStatus().equals(getString(R.string.request_send))){
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
                }

            }
        });
    }

    public void mainclickPost(View view) {
        Intent intent = new Intent(mContext, SingleSharePostActivity.class);
        if(!n_id.equals(getString(R.string.empty)) && !h_id.equals(getString(R.string.empty)) && !type.equals(getString(R.string.empty))){
            intent.putExtra(getString(R.string.n_id),n_id);
            intent.putExtra(getString(R.string.h_id),h_id);
            intent.putExtra(getString(R.string.intent_type),type);
            startActivity(intent);
        }
    }

    public void mainclickHoodInfo(View view) {
        if(n_id != null && !n_id.equals(getString(R.string.empty))){
            Intent intent = new Intent(mContext,HoodInfoActivity.class);
            intent.putExtra(getString(R.string.n_id),n_id);
            startActivity(intent);
        }
    }

    public void mainClickEvent(View view) {
        Intent intent = new Intent(mContext, SingleShareEventActivity.class);
        if(!n_id.equals(getString(R.string.empty)) && !h_id.equals(getString(R.string.empty)) && !type.equals(getString(R.string.empty))){
            intent.putExtra(getString(R.string.n_id),n_id);
            intent.putExtra(getString(R.string.h_id),h_id);
            intent.putExtra(getString(R.string.intent_type),type);
            startActivity(intent);
        }
    }

    public void mainClickMessage(View view) {
        if(h_id != null){
            Intent intent = new Intent(mContext, IndividualChatActivity.class);
            intent.putExtra(mContext.getString(R.string.intent_memberid),h_id);
            if(n_id != null)
                intent.putExtra(mContext.getString(R.string.n_id),n_id);
            intent.putExtra(mContext.getString(R.string.intent_type),type);
            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
            mContext.startActivity(intent);
        }
    }

    public void mainClickBroke(View view) {
        settingDialogBox();
    }

    //******************************************** settingDialogBox ******************************************************
    private void settingDialogBox(){
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.broke))
                .setMessage(getString(R.string.dialog_broke))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        try {
                            if(ownUser != null){
                                mMain.setVisibility(View.GONE);
                                mProgress.setVisibility(View.VISIBLE);
                                mDb.collection(getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            final House house = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                            assert house != null;
                                            for(int i = 0; i < house.getMembers().size(); i++){

                                                mDb.collection(getString(R.string.collection_users)).document(house.getMembers().get(i)).collection(getString(R.string.collection_relatives)).whereEqualTo(getString(R.string.h_id),h_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            List<DocumentSnapshot> relList = Objects.requireNonNull(task.getResult()).getDocuments();
                                                            for (int i = 0; i < relList.size(); i++){

                                                                mDb.collection(getString(R.string.collection_users)).document(house.getMembers().get(i)).collection(getString(R.string.collection_relatives)).document(relList.get(i).getId()).delete();


                                                            }
                                                        }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                            mMain.setVisibility(View.VISIBLE);
                                                            mProgress.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });

                                                final int finalI = i;
                                                mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.field_status),getString(R.string.request_accept)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                        List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                        for(int j = 0; j < requestList.size(); j++){
                                                            com.intouchapp.intouch.Models.Request request = requestList.get(j).toObject(com.intouchapp.intouch.Models.Request.class);

                                                            assert request != null;
                                                            if(request.getType().equals(getString(R.string.type_relative)) && request.getS_id().equals(house.getMembers().get(finalI)) && request.getR_id().equals(h_id)){
                                                                mDb.collection(getString(R.string.collection_requests)).document(requestList.get(j).getId()).delete();
                                                            }
                                                        }
                                                    }
                                                });

                                                if(house.getMembers().size() -1 == i){
                                                    Intent intent = new Intent(mContext, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                });



                                try {
                                    mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                final House house = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                assert house != null;
                                                if(house.getMembers() != null){
                                                    for(int i = 0; i < house.getMembers().size(); i++){

                                                        mDb.collection(getString(R.string.collection_users)).document(house.getMembers().get(i)).collection(getString(R.string.collection_relatives)).whereEqualTo(getString(R.string.h_id),ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    List<DocumentSnapshot> relList = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                    for (int i = 0; i < relList.size(); i++){

                                                                        mDb.collection(getString(R.string.collection_users)).document(house.getMembers().get(i)).collection(getString(R.string.collection_relatives)).document(relList.get(i).getId()).delete();

                                                                        final int finalI = i;
                                                                        mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.field_status),getString(R.string.request_accept)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                if(task.isSuccessful()){
                                                                                    List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                    for(int j = 0; j < requestList.size(); j++){
                                                                                        com.intouchapp.intouch.Models.Request request = requestList.get(j).toObject(com.intouchapp.intouch.Models.Request.class);

                                                                                        assert request != null;
                                                                                        if(request.getType().equals(getString(R.string.type_relative)) && request.getS_id().equals(house.getMembers().get(finalI)) && request.getR_id().equals(ownUser.getH_id())){
                                                                                            mDb.collection(getString(R.string.collection_requests)).document(requestList.get(j).getId()).delete();
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }
                                                                                }


                                                                            }
                                                                        });
                                                                    }
                                                                }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                                    mMain.setVisibility(View.VISIBLE);
                                                                    mProgress.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }


                                                    }


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

    //************************************************* getting house info ****************************************
    @SuppressLint("StaticFieldLeak")
    class HouseInfo extends AsyncTask<Void, String, Void> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        HouseInfo(){

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
            checkHouseInfo();
            Log.d(TAG, "doInBackground: n_id " + h_id);
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
            Log.d(TAG, "onpost onComplete: n_id is " + h_id);
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
            notificationObj.put("body", Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName()) + " " + getString(R.string.request_body)) ;
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
