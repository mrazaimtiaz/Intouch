package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Chats.IndividualChatActivity;
import com.intouchapp.intouch.Models.Chat;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Message;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsRecyclerAdapter.MyOwnHolder> {

    //variable
    List<Chat> chats;
    private User ownUser;
    private List<String> relativeList,freindList,memberlist;

    List<String> rel = new ArrayList<>();
    List<String> fri = new ArrayList<>();
    List<String> fam = new ArrayList<>();

    Context mContext;
    boolean publicType;

    private FirebaseFirestore mDb;

    private static final String TAG = "MemberRecyclerViewAdapt";


    public ChatsRecyclerAdapter(List<Chat> chats,List<String> relativeList,List<String> friendList,List<String> memberList, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.chats = chats;
        mContext = cnt;
        this.relativeList = relativeList;
        this.freindList = friendList;
        this.memberlist = memberList;
        publicType = true;
        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();
        mDb = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_chats,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {
        holder.mType.setAlpha(0.5f);
        if(ownUser != null){
            if(chats.get(position).getCategory().equals(mContext.getString(R.string.individual_chat))){
                if(!chats.get(position).getId2().equals(ownUser.getU_id())){

                    mDb.collection(mContext.getString(R.string.collection_users)).document(chats.get(position).getId2()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful()){
                                User user = task.getResult().toObject(User.class);

                                if(user != null){
                                    holder.mMemberName.setText(user.getName());

                                    holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_person_24px));
                                    if(relativeList != null){
                                        Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                                        if(relativeList.size() != 0){
                                            if(relativeList.contains(chats.get(position).getId2())){
                                                publicType = false;
                                                UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));

                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });
                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });
                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });

                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });


                                            }
                                        }
                                    }
                                    if(freindList != null){
                                        Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                                        if(freindList.size() != 0){
                                            if(freindList.contains(chats.get(position).getId2())){
                                                publicType = false;

                                                UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));

                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });
                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });
                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });

                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);
                                                    }
                                                });


                                            }
                                        }
                                    }
                                    if(memberlist != null){
                                        Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                                        if(memberlist.size() != 0){
                                            if(memberlist.contains(chats.get(position).getId2())){
                                                publicType = false;
                                                UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));


                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });

                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });


                                            }
                                        }
                                    }
                                    if(publicType){
                                        UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                                    }
                                }

                            }
                        }
                    });
                }else if(chats.get(position).getId2().equals(ownUser.getU_id())){

                    mDb.collection(mContext.getString(R.string.collection_users)).document(chats.get(position).getId1()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful()){
                                User user = task.getResult().toObject(User.class);

                                if(user != null){
                                    holder.mMemberName.setText(user.getName());
                                    holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_person_24px));
                                    if(relativeList != null){
                                        Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                                        if(relativeList.size() != 0){
                                            if(relativeList.contains(chats.get(position).getId1())){
                                                publicType = false;
                                                UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));

                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });

                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });


                                            }
                                        }
                                    }
                                    if(freindList != null){
                                        Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                                        if(freindList.size() != 0){
                                            if(freindList.contains(chats.get(position).getId1())){
                                                publicType = false;

                                                UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));

                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });

                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });


                                            }
                                        }
                                    }
                                    if(memberlist != null){
                                        Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                                        if(memberlist.size() != 0){
                                            if(memberlist.contains(chats.get(position).getId1())){
                                                publicType = false;
                                                UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));

                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });
                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });

                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.individual_chat));
                                                        mContext.startActivity(intent);

                                                    }
                                                });


                                            }
                                        }
                                    }
                                    if(publicType){
                                        UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                                    }
                                }

                            }
                        }
                    });
                }
            }else if(chats.get(position).getCategory().equals(mContext.getString(R.string.group_chat))){
                if(!chats.get(position).getId2().equals(ownUser.getH_id())){

                    try {
                        mDb.collection(mContext.getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                final List<DocumentSnapshot> hoodList = task.getResult().getDocuments();
                                for (int i = 0; i < hoodList.size(); i++){

                                    Hood hood = hoodList.get(i).toObject(Hood.class);

                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(hoodList.get(i).getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            List<DocumentSnapshot> houseList = task.getResult().getDocuments();
                                            for (int j = 0; j < houseList.size(); j++){
                                                final House house = houseList.get(j).toObject(House.class);

                                                if(house.getH_id().equals(chats.get(position).getId2())){
                                                    holder.mMemberName.setText(house.getName());
                                                    holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_house));
                                                    UniversalImageLoader.setImage(house.getImage(), holder.mMemberAvatar, null, "",mContext);

                                                    if(ownUser.getH_id().equals(chats.get(position).getId2())){

                                                        holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                mContext.startActivity(intent);

                                                            }
                                                        });
                                                        holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                mContext.startActivity(intent);

                                                            }
                                                        });
                                                        holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                mContext.startActivity(intent);

                                                            }
                                                        });

                                                        holder.mType.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                mContext.startActivity(intent);

                                                            }
                                                        });


                                                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                                    }
                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                List<DocumentSnapshot> relativesList = task.getResult().getDocuments();
                                                                Log.d(TAG, "check into relatives task successful " + relativesList);
                                                                for (int i = 0; i < relativesList.size() ; i++){
                                                                    Relative relative = relativesList.get(i).toObject(Relative.class);
                                                                    rel.add(relative.getH_id());
                                                                    Log.d(TAG, "check into relatives single house " + relative.getH_id());

                                                                    if(relativesList.size() -1 == i){
                                                                        if(rel != null){
                                                                            if(rel.contains(chats.get(position).getId2())){

                                                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });
                                                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);
                                                                                    }
                                                                                });
                                                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });

                                                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });

                                                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }
                                                    });
                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                List<DocumentSnapshot> freindsList = task.getResult().getDocuments();
                                                                Log.d(TAG, "check into freinds task succesful " + freindsList);
                                                                for (int i = 0; i < freindsList.size() ; i++){
                                                                    Friend friend = freindsList.get(i).toObject(Friend.class);
                                                                    fri.add(friend.getH_id());
                                                                    Log.d(TAG, "check into friend single house " + friend.getH_id());

                                                                    if(fri.size() -1 == i){
                                                                        if(fri != null){
                                                                            if(fri.contains(chats.get(position).getId2())){

                                                                                holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });
                                                                                holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });
                                                                                holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });

                                                                                holder.mType.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                        intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId2());
                                                                                        intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                        intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                        mContext.startActivity(intent);

                                                                                    }
                                                                                });
                                                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });


                                                    Log.d(TAG, "check into freinds and relatives called" );


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


                }else if(chats.get(position).getId2().equals(ownUser.getH_id())){

                    try {
                        mDb.collection(mContext.getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                List<DocumentSnapshot> hoodList = Objects.requireNonNull(task.getResult()).getDocuments();
                                for (int i = 0; i < hoodList.size(); i++){

                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(hoodList.get(i).getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            List<DocumentSnapshot> houseList = Objects.requireNonNull(task.getResult()).getDocuments();
                                            for (int j = 0; j < houseList.size(); j++){
                                                final House house = houseList.get(j).toObject(House.class);

                                                assert house != null;
                                                if(house.getH_id() != null){
                                                    if(house.getH_id().equals(chats.get(position).getId1())){
                                                        holder.mMemberName.setText(house.getName());
                                                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_house));
                                                        UniversalImageLoader.setImage(house.getImage(), holder.mMemberAvatar, null, "",mContext);

                                                        if(ownUser.getH_id().equals(chats.get(position).getId1())){

                                                            holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                    intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                    intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                    intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                    mContext.startActivity(intent);

                                                                }
                                                            });
                                                            holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                    intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                    intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                    intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                    mContext.startActivity(intent);

                                                                }
                                                            });
                                                            holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                    intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                    intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                    intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                    mContext.startActivity(intent);

                                                                }
                                                            });

                                                            holder.mType.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                    intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                    intent.putExtra(mContext.getString(R.string.n_id),ownUser.getN_id());
                                                                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                    intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                    mContext.startActivity(intent);

                                                                }
                                                            });

                                                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                                        }
                                                        mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    List<DocumentSnapshot> relativesList = task.getResult().getDocuments();
                                                                    Log.d(TAG, "check into relatives task successful " + relativesList);
                                                                    for (int i = 0; i < relativesList.size() ; i++){
                                                                        Relative relative = relativesList.get(i).toObject(Relative.class);
                                                                        rel.add(relative.getH_id());
                                                                        Log.d(TAG, "check into relatives single house " + relative.getH_id());

                                                                        if(relativesList.size() -1 == i){
                                                                            if(rel != null){
                                                                                if(rel.contains(chats.get(position).getId1())){

                                                                                    holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });
                                                                                    holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });
                                                                                    holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });

                                                                                    holder.mType.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });
                                                                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        });
                                                        mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    List<DocumentSnapshot> freindsList = task.getResult().getDocuments();
                                                                    Log.d(TAG, "check into freinds task succesful " + freindsList);
                                                                    for (int i = 0; i < freindsList.size() ; i++){
                                                                        Friend friend = freindsList.get(i).toObject(Friend.class);
                                                                        fri.add(friend.getH_id());
                                                                        Log.d(TAG, "check into friend single house " + friend.getH_id());

                                                                        if(fri.size() -1 == i){
                                                                            if(fri != null){
                                                                                if(fri.contains(chats.get(position).getId1())){
                                                                                    holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });
                                                                                    holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });
                                                                                    holder.mChat.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });

                                                                                    holder.mType.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Intent intent = new Intent(mContext, IndividualChatActivity.class);
                                                                                            intent.putExtra(mContext.getString(R.string.intent_memberid),chats.get(position).getId1());
                                                                                            intent.putExtra(mContext.getString(R.string.n_id),house.getN_id());
                                                                                            intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                                                                            intent.putExtra(mContext.getString(R.string.intent_chat),mContext.getString(R.string.group_chat));
                                                                                            mContext.startActivity(intent);

                                                                                        }
                                                                                    });
                                                                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
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
        }

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection(mContext.getString(R.string.collection_chat)).document(chats.get(position).getId()).collection(mContext.getString(R.string.collection_message))
                .orderBy(mContext.getString(R.string.timestamp), Query.Direction.DESCENDING)
                .limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    List<DocumentSnapshot> list = task.getResult().getDocuments();

                    if(list.size() != 0){
                        Message message = list.get(0).toObject(Message.class);
                        String messageSize;
                        messageSize = message.getMessage();
                        if(message.getMessage().length() > 30){
                            messageSize = message.getMessage().substring(0,30) + "...";
                        }
                        holder.mChat.setText(messageSize);

                        long seconds = (System.currentTimeMillis() / 1000) - message.getTimestamp().getSeconds();
                        Log.d(TAG, "onBindViewHolder: " + seconds);
                        long diffMinutes = seconds / 60;
                        long diffHours = diffMinutes / 60;
                        long diffDays = diffHours / 24;

                        holder.mTimeStamp.setAlpha(0.5f);
                        if(seconds < 60){
                            holder.mTimeStamp.setText (seconds + mContext.getString(R.string.s_ago));
                        }else if(diffMinutes < 60){
                            holder.mTimeStamp.setText(diffMinutes +  mContext.getString(R.string.m_ago));
                        }else if(diffHours < 24){
                            holder.mTimeStamp.setText(diffHours + mContext.getString(R.string.h_ago));
                        }else if(diffHours < 48){
                            holder.mTimeStamp.setText(mContext.getString(R.string.yesterday));
                        }
                        else{
                            holder.mTimeStamp.setText(diffDays + mContext.getString(R.string.d_ago));
                        }
                    }

                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName;
        TextView mChat;
        TextView mTimeStamp;
        ImageView mMemberAvatar;
        ImageView mEllipse,mType;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mChat = (TextView) itemView.findViewById(R.id.tv_chat);
            mTimeStamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
        }
    }
}
