package com.intouchapp.intouch.Main.Chats;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Models.Chat;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.ChatsRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    //widget
    private RecyclerView mRecyclerView;
    private ImageView mBack;
    private FloatingActionButton mFloat;
    private ConstraintLayout mNothing,mMain,mProgress;

    //variable
    private List<String> freindList;
    private List<String> memberList;
    private List<String> relativeList;

    Context mContext;
    private User ownUser;
    private List<Chat> chats = new ArrayList<>();

    private static final String TAG = "FallowersActivity";

    //firebase
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        //intialization
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mBack = (ImageView) findViewById(R.id.iv_back);

        mFloat = (FloatingActionButton) findViewById(R.id.floating_add_post);
        mNothing = (ConstraintLayout) findViewById(R.id.constraintLayoutNothing);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);

        mContext = ChatsActivity.this;

        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();

        mDb = FirebaseFirestore.getInstance();

        mMain.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        freindList = new ArrayList<>();
        memberList = new ArrayList<>();
        relativeList = new ArrayList<>();


        intentMethod();

        gettingUserList();
    }
    //******************************************************** getting userdata ****************************************************
    private void gettingUserList() {
        mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    try {
                            mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        House house = task.getResult().toObject(House.class);
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
                                                List<DocumentSnapshot> list = task.getResult().getDocuments();
                                                for (int i = 0; i < list.size(); i++) {
                                                    Relative relative = list.get(i).toObject(Relative.class);
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
                                                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                                                        for (int i = 0; i < list.size(); i++) {
                                                            Friend friend = list.get(i).toObject(Friend.class);
                                                            if (friend.getF_id() != null) {
                                                                if (friend.getF_id().size() != 0) {
                                                                    Log.d(TAG, "onComplete: get friend list");
                                                                    freindList.addAll(friend.getF_id());
                                                                    Log.d(TAG, "onComplete:freindList " + freindList);
                                                                }
                                                            }
                                                            if (i == list.size() - 1) {
                                                                settingChatList();
                                                            }
                                                        }
                                                    } if(task.getResult().size() == 0) {
                                                        settingChatList();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });


                    } catch (NullPointerException e) {
                        Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                        Intent intent = new Intent(mContext, ChatsActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });
    }
    //***************************************** setting chat list **********************************************
    private void settingChatList(){

        mDb.collection(getString(R.string.collection_chat)).orderBy(getString(R.string.timestamp)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    List<DocumentSnapshot> list = task.getResult().getDocuments();

                    for(int i = 0; i < list.size(); i++){

                        Chat chat = list.get(i).toObject(Chat.class);

                        if(ownUser != null){
                            if(chat.getId2().equals(ownUser.getU_id()) || chat.getId2().equals(ownUser.getH_id()) ||
                                    chat.getId1().equals(ownUser.getU_id()) || chat.getId1().equals(ownUser.getH_id())){

                                chats.add(chat);

                            }
                        }
                        if(list.size() -1 == i){
                            settingRecyclerView(chats);
                        }
                    }

                }if(task.getResult().size() == 0){
                    settingRecyclerView(chats);
                }
            }
        });
    }
    //******************************************** intent method ***********************************************
    private void intentMethod(){

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectChatActivity.class);
                startActivity(intent);
            }
        });
    }

    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<Chat> chats){

        if(chats != null){
            if(chats.size() != 0){
                mNothing.setVisibility(View.GONE);
                Collections.reverse(chats);
                mRecyclerView.setAdapter(new ChatsRecyclerAdapter(chats,relativeList,freindList,memberList, mContext));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                mMain.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
            }else{
                mMain.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                mNothing.setVisibility(View.VISIBLE);
            }
        }else{
            mMain.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mNothing.setVisibility(View.VISIBLE);
        }
    }
}
