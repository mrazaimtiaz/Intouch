package com.intouchapp.intouch.Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.MemberRecyclerViewAdapter;
import com.intouchapp.intouch.Utills.UniversalImageLoader;
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

public class HouseInfoActivity extends AppCompatActivity {

    //widget
    private ImageView mBack;
    private CircleImageView mHouseInfo;
    private TextView mHouseTitle,mHouseBio;
    private Button mSendRequest,mCancelRequest;
    private RecyclerView mRecyclerView;



    //variable
    private String h_id;
    private String n_id;
    private Context mContext;
    private String doc_id;

    //firebase
    FirebaseFirestore mDb;




    private RequestQueue mRequestQue;

    private String URL = "https://fcm.googleapis.com/fcm/send";




    private static final String TAG = "HouseInfoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_info);

        mBack = (ImageView) findViewById(R.id.iv_back);
        mHouseInfo = (CircleImageView) findViewById(R.id.iv_houseinfo);
        mHouseTitle = (TextView) findViewById(R.id.tv_house_name);
        mHouseBio = (TextView) findViewById(R.id.tv_house_bio);
        mSendRequest = (Button) findViewById(R.id.btn_member_request);
        mCancelRequest = (Button) findViewById(R.id.btn_cancel_request);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mDb = FirebaseFirestore.getInstance();
        mContext = HouseInfoActivity.this;
        AndroidThreeTen.init(this);

        mRequestQue = Volley.newRequestQueue(this);

        FirebaseMessaging.getInstance().subscribeToTopic("request");



        intentMethod();
        h_id = getString(R.string.empty);
        n_id = getString(R.string.empty);
        h_id =  getIntent().getStringExtra(getString(R.string.house_id)).substring(0, 20);
        n_id =  getIntent().getStringExtra(getString(R.string.house_id)).substring(20, 40);


        new HouseInfo().execute();


    }
    //***********************************************intent method *********************************************
    private void intentMethod() {

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //***************************************************** send request *************************************************
    private void sendRequest(){
        mSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mSendRequest.setEnabled(false);
                    ZoneId tz =  ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                    int nanos = localDateTime.getNano();
                    Timestamp timestamp = new Timestamp(seconds, nanos);
                    com.intouchapp.intouch.Models.Request request = new com.intouchapp.intouch.Models.Request();
                    String rid = mDb.collection(getString(R.string.collection_requests)).document().getId();
                    request.setId(rid);
                    request.setS_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    request.setR_id(h_id);
                    request.setStatus(getString(R.string.request_send));
                    request.setType(getString(R.string.type_member));
                    request.setTimeStamp(timestamp);

                    mDb.collection(getString(R.string.collection_requests)).document(rid).set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mSendRequest.setVisibility(View.GONE);
                                mCancelRequest.setVisibility(View.VISIBLE);
                                mSendRequest.setEnabled(true);
                                Toast.makeText(HouseInfoActivity.this, getString(R.string.wait_for_request), Toast.LENGTH_SHORT).show();

                                mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                        assert house != null;
                                        List<String> members = house.getMembers();
                                        for(int i = 0; i < members.size() ; i++){
                                            mDb.collection(getString(R.string.collection_users)).document(members.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                    assert user != null;
                                                    sendNotification(user.getDevice_token());
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                        }
                    });
                }catch (NullPointerException e){
                    finish();
                    e.printStackTrace();
                }



                        }
                    });
        }



    private void cancelRequest(){
        mCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mCancelRequest.setEnabled(false);
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
                                    Log.d(TAG, "onComplete: " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + " " + request.getS_id() + " reciver end " + h_id +" " + request.getR_id() +  " type "  + request.getType() + " " + getString(R.string.type_member) + " " +request.getStatus() + " " + getString(R.string.request_send));

                                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(request.getS_id()) && h_id.equals(request.getR_id()) && request.getType().equals(getString(R.string.type_member)) && request.getStatus().equals(getString(R.string.request_send))){
                                        Log.d(TAG, "onComplete: condition mathced");
                                        mDb.collection(getString(R.string.collection_requests)).document(list.get(i).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mCancelRequest.setVisibility(View.GONE);
                                                    mSendRequest.setVisibility(View.VISIBLE);
                                                    mCancelRequest.setEnabled(true);
                                                }else{
                                                    mCancelRequest.setEnabled(true);
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
                            mCancelRequest.setEnabled(true);
                        }
                    });
                }catch (NullPointerException e){
                    finish();
                    e.printStackTrace();
                }

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
                        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(request.getS_id()) && h_id.equals(request.getR_id()) && request.getType().equals(getString(R.string.type_member)) && request.getStatus().equals(getString(R.string.request_send))){
                            Log.d(TAG, "onComplete: condition matched");
                                    if(task.isSuccessful()){
                                        mCancelRequest.setVisibility(View.VISIBLE);
                                        mSendRequest.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(HouseInfoActivity.this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                                    }


                    }
                }
            }
            }));

            Tasks.await(mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        House house = task.getResult().toObject(House.class);
                        mHouseTitle.setText(house.getName());
                        if(!house.getBio().equals(getString(R.string.empty)))
                            mHouseBio.setText(house.getBio());

                        UniversalImageLoader.setImage(house.getImage(), mHouseInfo, null, "",mContext);
                        settingRecycler(house.getMembers());
                    }
                }
            }));




        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            finish();
            e.printStackTrace();
        }

    }
    //***************************************** setting recycler view ********************************************
    private void settingRecycler(List<String> member){
        if(member != null){
            mRecyclerView.setAdapter(new MemberRecyclerViewAdapter(null,null,null,member, mContext,getString(R.string.type_other),getString(R.string.empty)));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        }


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
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + getString(R.string.request_body)) ;
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
        }catch (NullPointerException e){
            finish();
            e.printStackTrace();
        }

    }

}
