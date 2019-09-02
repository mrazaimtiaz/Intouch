package com.intouchapp.intouch.Utills;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Account.Requests.RequestsActivity;
import com.intouchapp.intouch.Main.Home.MainHouseInfoActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecievedRequestsRecyclerAdapter extends RecyclerView.Adapter<RecievedRequestsRecyclerAdapter.MyOwnHolder> {

    //variable
    List<Request> requests;
    List<String> friendList;

    Context mContext;

    private User ownUser;

    private RequestQueue mRequestQue;

    private String URL = "https://fcm.googleapis.com/fcm/send";

    private FirebaseFirestore mDb;
    private static final String TAG = "MemberRecyclerViewAdapt";


    public RecievedRequestsRecyclerAdapter(List<Request> requests,List<String> friendList, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");

        this.requests = requests;
        this.friendList = friendList;
        mContext = cnt;
        mDb = FirebaseFirestore.getInstance();

        mRequestQue = Volley.newRequestQueue(mContext);

        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_recievedrequest,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {


        if(requests.get(position).getType().equals(mContext.getString(R.string.type_relative))){

            try {
                mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        assert user != null;
                        holder.mMemberName.setText(Objects.requireNonNull(user).getName());
                        UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);

                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                final House house =  Objects.requireNonNull(task.getResult()).toObject(House.class);
                                assert house != null;
                                holder.mHouseName.setText(Objects.requireNonNull(house).getName());
                                holder.mTvRequest.setText(mContext.getString(R.string.type_relative));
                                holder.mRequestType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_relative_red));
                                holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));

                                holder.mHouseName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(friendList.contains(requests.get(position).getS_id())){
                                            Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                            intent.putExtra(mContext.getString(R.string.intent_no_request),false);
                                            intent.putExtra(mContext.getString(R.string.house_id),house.getH_id()+house.getN_id()+mContext.getString(R.string.type_friend));
                                            mContext.startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                            intent.putExtra(mContext.getString(R.string.intent_no_request),false);
                                            intent.putExtra(mContext.getString(R.string.house_id),house.getH_id()+house.getN_id()+mContext.getString(R.string.type_other));
                                            mContext.startActivity(intent);
                                        }
                                    }
                                });

                                holder.accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        holder.accept.setVisibility(View.GONE);
                                        holder.decline.setVisibility(View.GONE);
                                        holder.declineClicked.setVisibility(View.GONE);
                                        holder.acceptClicked.setVisibility(View.VISIBLE);

                                        if(friendList.contains(requests.get(position).getS_id())){
                                            mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).collection(mContext.getString(R.string.collection_friends)).whereEqualTo(mContext.getString(R.string.h_id),ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                        for(int i = 0; i <list.size() ; i++){
                                                            mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).collection(mContext.getString(R.string.collection_friends)).document(list.get(i).getId()).delete();
                                                        }
                                                    }
                                                }
                                            });
                                            mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).whereEqualTo(mContext.getString(R.string.h_id),user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                        for(int i = 0; i <list.size() ; i++){
                                                            mDb.collection(mContext.getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(mContext.getString(R.string.collection_friends)).document(list.get(i).getId()).delete();
                                                        }
                                                    }
                                                }
                                            });
                                            mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        House house1 = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                        final Relative relative = new Relative();
                                                        assert house1 != null;
                                                        relative.setH_id(house1.getH_id());
                                                        relative.setR_id(house1.getMembers());

                                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                House house2 = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                                assert house2 != null;
                                                                for (int i = 0; i < Objects.requireNonNull(house2).getMembers().size(); i++){
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(house2.getMembers().get(i)).collection(mContext.getString(R.string.collection_relatives)).add(relative);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        House house1 = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                        final Relative relative = new Relative();
                                                        assert house1 != null;
                                                        relative.setH_id(house1.getH_id());
                                                        relative.setR_id(house1.getMembers());

                                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                House house2 = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                                assert house2 != null;
                                                                for (int i = 0; i < house2.getMembers().size(); i++){
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(house2.getMembers().get(i)).collection(mContext.getString(R.string.collection_relatives)).add(relative);
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(house2.getMembers().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            User user3 = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                            assert user3 != null;
                                                                            sendRelativeNotification(user3.getDevice_token());
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            Map<String,Object> map = new HashMap<>();
                                            map.put(mContext.getString(R.string.field_status),mContext.getString(R.string.request_accept));
                                            mDb.collection(mContext.getString(R.string.collection_requests)).document(requests.get(position).getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d(TAG, "onComplete: successful");
                                                }
                                            });
                                        }else{
                                            mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        House house1 = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                        final Relative relative = new Relative();
                                                        assert house1 != null;
                                                        relative.setH_id(house1.getH_id());
                                                        relative.setR_id(house1.getMembers());

                                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                House house2 = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                                assert house2 != null;
                                                                for (int i = 0; i < house2.getMembers().size(); i++){
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(house2.getMembers().get(i)).collection(mContext.getString(R.string.collection_relatives)).add(relative);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        House house1 = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                        final Relative relative = new Relative();
                                                        assert house1 != null;
                                                        relative.setH_id(house1.getH_id());
                                                        relative.setR_id(house1.getMembers());

                                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                House house2 = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                                assert house2 != null;
                                                                for (int i = 0; i < house2.getMembers().size(); i++){
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(house2.getMembers().get(i)).collection(mContext.getString(R.string.collection_relatives)).add(relative);
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(house2.getMembers().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            User user3 = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                            assert user3 != null;
                                                                            sendRelativeNotification(user3.getDevice_token());
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            Map<String,Object> map = new HashMap<>();
                                            map.put(mContext.getString(R.string.field_status),mContext.getString(R.string.request_accept));
                                            mDb.collection(mContext.getString(R.string.collection_requests)).document(requests.get(position).getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d(TAG, "onComplete: successful");
                                                    Intent intent = new Intent(mContext, RequestsActivity.class);
                                                    mContext.startActivity(intent);
                                                    ((Activity)mContext).finish();
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });
                    }
                });

            }catch (NullPointerException e){
                e.printStackTrace();
            }



        }else if(requests.get(position).getType().equals(mContext.getString(R.string.type_friend))){
            try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    assert user != null;
                    holder.mMemberName.setText(Objects.requireNonNull(user).getName());
                    UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);


                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                final House house =  Objects.requireNonNull(task.getResult()).toObject(House.class);
                                assert house != null;
                                holder.mHouseName.setText(house.getName());
                                holder.mTvRequest.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                                holder.mTvRequest.setText(mContext.getString(R.string.type_friend));
                                holder.mRequestType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_friends_blue));
                                holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));

                                holder.mHouseName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                        intent.putExtra(mContext.getString(R.string.intent_no_request),false);
                                        intent.putExtra(mContext.getString(R.string.house_id),house.getH_id()+house.getN_id()+mContext.getString(R.string.type_other));
                                        mContext.startActivity(intent);

                                    }
                                });
                            }
                        });


                }
            });
            }catch (NullPointerException e){
                e.printStackTrace();
            }


            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.accept.setVisibility(View.GONE);
                        holder.decline.setVisibility(View.GONE);
                        holder.declineClicked.setVisibility(View.GONE);
                        holder.acceptClicked.setVisibility(View.VISIBLE);

                        mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).collection(mContext.getString(R.string.collection_friends)).whereEqualTo(mContext.getString(R.string.h_id),ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){
                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                    for(int i = 0; i < list.size(); i++){
                                        Friend friend = list.get(i).toObject(Friend.class);
                                        assert friend != null;
                                        friend.getF_id().add(ownUser.getU_id());

                                        mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).collection(mContext.getString(R.string.collection_friends)).document(list.get(i).getId()).set(friend);

                                        mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                User user3 = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                assert user3 != null;
                                                sendFriendNotification(user3.getDevice_token());
                                            }
                                        });


                                        mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(task.isSuccessful()){
                                                    final User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(ownUser.getU_id()).collection(mContext.getString(R.string.collection_friends)).whereEqualTo(mContext.getString(R.string.h_id),user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                for(int i = 0; i < list.size(); i++) {
                                                                    Friend friend = list.get(i).toObject(Friend.class);
                                                                    assert friend != null;
                                                                    friend.getF_id().add(requests.get(position).getS_id());
                                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(ownUser.getU_id()).collection(mContext.getString(R.string.collection_friends)).document(list.get(i).getId()).set(friend);

                                                                }
                                                            }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                                List<String> f_id = new ArrayList<>();
                                                                f_id.add(user.getU_id());
                                                                Friend addFriend = new Friend();
                                                                addFriend.setH_id(user.getH_id());
                                                                addFriend.setF_id(f_id);
                                                                mDb.collection(mContext.getString(R.string.collection_users)).document(ownUser.getU_id()).collection(mContext.getString(R.string.collection_friends)).document().set(addFriend);


                                                            }
                                                        }
                                                    });

                                                }

                                            }
                                        });

                                    }
                                }
                                if(Objects.requireNonNull(task.getResult()).size() == 0){
                                    List<String> id = new ArrayList<>();
                                    id.add(ownUser.getU_id());
                                    Friend userFreind = new Friend();
                                    userFreind.setH_id(ownUser.getH_id());
                                    userFreind.setF_id(id);
                                    mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).collection(mContext.getString(R.string.collection_friends)).document().set(userFreind);
                                    mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if(task.isSuccessful()){
                                                List<String> id = new ArrayList<>();
                                                User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                assert user != null;
                                                sendFriendNotification(user.getDevice_token());
                                                id.add(user.getU_id());
                                                Friend friend = new Friend();
                                                friend.setH_id(user.getH_id());
                                                friend.setF_id(id);
                                                mDb.collection(mContext.getString(R.string.collection_users)).document(ownUser.getU_id()).collection(mContext.getString(R.string.collection_friends)).document().set(friend);

                                            }
                                        }
                                    });

                                }

                                Map<String,Object> map = new HashMap<>();
                                map.put(mContext.getString(R.string.field_status),mContext.getString(R.string.request_accept));
                                mDb.collection(mContext.getString(R.string.collection_requests)).document(requests.get(position).getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(TAG, "onComplete: successful");
                                        Intent intent = new Intent(mContext, RequestsActivity.class);
                                        mContext.startActivity(intent);
                                        ((Activity)mContext).finish();
                                    }
                                });
                            }
                        });

                    }catch (NullPointerException e){

                    }

                }
            });

        }else if(requests.get(position).getType().equals(mContext.getString(R.string.type_member))){
            Log.d(TAG, "onBindViewHolder: type member");

            try {
                mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        final User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        assert user != null;
                        holder.mMemberName.setText(user.getName());
                        UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                        holder.mHouseName.setVisibility(View.GONE);
                        holder.mHouseName.setText("");
                        holder.mTvRequest.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                        holder.mTvRequest.setText(mContext.getString(R.string.type_member));
                        holder.mRequestType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));


                        holder.accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                holder.accept.setVisibility(View.GONE);
                                holder.decline.setVisibility(View.GONE);
                                holder.declineClicked.setVisibility(View.GONE);
                                holder.acceptClicked.setVisibility(View.VISIBLE);
                                mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                        List<String> id = new ArrayList<>();
                                        id.add(user.getU_id());

                                        assert house != null;
                                        if(house.getMembers() != null){
                                            if(house.getMembers().size() != 0){
                                                house.getMembers().add(requests.get(position).getS_id());

                                            }else{
                                                house.setMembers(id);
                                            }
                                        }
                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).set(house);

                                        Map<String,Object> updateUser = new HashMap<>();
                                        updateUser.put(mContext.getString(R.string.h_id),ownUser.getH_id());
                                        updateUser.put(mContext.getString(R.string.n_id),ownUser.getN_id());
                                        mDb.collection(mContext.getString(R.string.collection_users)).document(user.getU_id()).update(updateUser);

                                        Map<String,Object> map = new HashMap<>();
                                        map.put(mContext.getString(R.string.field_status),mContext.getString(R.string.request_accept));
                                        mDb.collection(mContext.getString(R.string.collection_requests)).document(requests.get(position).getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "onComplete: successful");
                                                Intent intent = new Intent(mContext, RequestsActivity.class);
                                                mContext.startActivity(intent);
                                                ((Activity)mContext).finish();
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
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    holder.accept.setVisibility(View.GONE);
                    holder.decline.setVisibility(View.GONE);
                    holder.declineClicked.setVisibility(View.VISIBLE);
                    holder.acceptClicked.setVisibility(View.GONE);

                    Map<String,Object> map = new HashMap<>();
                    map.put(mContext.getString(R.string.field_status),mContext.getString(R.string.request_declined));
                    mDb.collection(mContext.getString(R.string.collection_requests)).document(requests.get(position).getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: successful");
                            Intent intent = new Intent(mContext, RequestsActivity.class);
                            mContext.startActivity(intent);
                            ((Activity)mContext).finish();
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });
        holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendList.contains(requests.get(position).getS_id())){
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getS_id());
                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getS_id());
                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_other));
                    intent.putExtra(mContext.getString(R.string.intent_no_request),false);
                    mContext.startActivity(intent);
                }

            }
        });

        holder.mMemberName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendList.contains(requests.get(position).getS_id())){
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getS_id());
                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getS_id());
                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_other));
                    intent.putExtra(mContext.getString(R.string.intent_no_request),false);
                    mContext.startActivity(intent);
                }
            }
        });


        //UniversalImageLoader.setImage(memberAvatar[position], holder.mMemberAvatar, null, "",mContext);

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
    //*********************************************** send notification *****************************************************
    private void sendRelativeNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.title_request));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_relative_request)) ;
            notificationObj.put("sound","default");

            json.put("notification",notificationObj);




            JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {

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
    private void sendFriendNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.title_request));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_friend_request)) ;
            notificationObj.put("sound","default");

            json.put("notification",notificationObj);



            JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {

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

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName,mHouseName,mTvRequest;
        ImageView mMemberAvatar;
        ImageView mEllipse,mType,mRequestType;

        Button acceptClicked,declineClicked;
        Button accept,decline;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mHouseName = (TextView) itemView.findViewById(R.id.tv_type);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);
            mTvRequest = (TextView) itemView.findViewById(R.id.tv_request_type);
            mRequestType = (ImageView) itemView.findViewById(R.id.iv_request_type);

            accept = (Button) itemView.findViewById(R.id.btn_accept);
            decline = (Button) itemView.findViewById(R.id.btn_decline);

            acceptClicked = (Button) itemView.findViewById(R.id.btn_accept_clicked);
            declineClicked = (Button) itemView.findViewById(R.id.btn_decline_checked);

        }
    }
}
