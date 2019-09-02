package com.intouchapp.intouch.Main.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Events.SingleShareEventActivity;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Main.Posts.SingleSharePostActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.DropDownAdapter;
import com.intouchapp.intouch.Utills.MemberRecyclerViewAdapter;
import com.intouchapp.intouch.Utills.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HoodInfoActivity extends AppCompatActivity {

    //variable
    private String n_id;
    private static final String TAG = "HoodInfoActivity";
    private Context mContext;

    //widget
    private ImageView mBack;
    private CircleImageView mHood;
    private TextView mName, mBio;

    private RecyclerView mRecyclerView;

    private List<String> freindList;
    private List<String> memberList;
    private List<String> relativeList;
    private User user;

    //firebase
    FirebaseFirestore mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hood_info);

        mBack = (ImageView) findViewById(R.id.iv_back);
        mHood = (CircleImageView) findViewById(R.id.iv_hood);
        mName = (TextView) findViewById(R.id.tv_user_name);
        mBio = (TextView) findViewById(R.id.tv_user_bio);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mDb = FirebaseFirestore.getInstance();

        mContext = HoodInfoActivity.this;

        freindList = new ArrayList<>();
        memberList = new ArrayList<>();
        relativeList = new ArrayList<>();

        n_id = getString(R.string.empty);
        if(getIntent().getStringExtra(getString(R.string.n_id)) != null)
        n_id = getIntent().getStringExtra(getString(R.string.n_id));
        intentMethod();
        gettingUserList();
    }


    public void postHood(View view) {
        Intent intent = new Intent(mContext, SingleSharePostActivity.class);
        intent.putExtra(getString(R.string.n_id),n_id);
        intent.putExtra(getString(R.string.intent_type),getString(R.string.type_hood));
        startActivity(intent);
    }

    public void eventHood(View view) {
        Intent intent = new Intent(mContext, SingleShareEventActivity.class);
        intent.putExtra(getString(R.string.n_id),n_id);
        intent.putExtra(getString(R.string.intent_type),getString(R.string.type_hood));
        startActivity(intent);
    }

    public void fallowersHood(View view) {
        Intent intent = new Intent(mContext,FallowersActivity.class);
        intent.putExtra(getString(R.string.n_id),n_id);
        startActivity(intent);
    }

    public void complimentHood(View view) {
        Intent intent = new Intent(mContext,ComplimentsActivity.class);
        intent.putExtra(getString(R.string.n_id),n_id);
        startActivity(intent);
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
            mDb.collection(getString(R.string.collection_hoods)).document(n_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                    assert hood != null;
                    mName.setText(hood.getName());
                    UniversalImageLoader.setImage(hood.getImage(), mHood, null, "",mContext);
                    if(!hood.getBio().equals(getString(R.string.empty)))
                        mBio.setText(hood.getBio());
                }
            });
            mDb.collection(getString(R.string.collection_users)).whereEqualTo(getString(R.string.n_id),n_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                    List<String> members = new ArrayList<>();
                    for(int i = 0; i<list.size();i++){
                        members.add(list.get(i).getId());
                        if(i == list.size()-1){
                            settingRecycler(members);
                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }
    //******************************************************** getting userdata ****************************************************
    private void gettingUserList() {
        try {
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            try {

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
                                                                                checkHouseInfo();


                                                                            }
                                                                        }
                                                                    } if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                                        checkHouseInfo();


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
    //***************************************** setting recycler view ********************************************
    private void settingRecycler(List<String> member){
        if(member != null){
            mRecyclerView.setAdapter(new MemberRecyclerViewAdapter(memberList,freindList,relativeList,member, mContext,getString(R.string.type_hood),getString(R.string.intent_member)));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        }
    }
}
