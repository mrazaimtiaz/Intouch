package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Models.Comment;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class JoinedRecyclerAdapter extends RecyclerView.Adapter<JoinedRecyclerAdapter.MyOwnHolder> {

    //variable
    private List<String> joined;
    private List<String> relativeList,freindList,memberlist;
    Context mContext;



    boolean publicType;

    private FirebaseFirestore mDb;
    private static final String TAG = "MemberRecyclerViewAdapt";

    public JoinedRecyclerAdapter(List<String> joined, List<String> relativeList, List<String> friendList, List<String> memberList, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");

        mDb = FirebaseFirestore.getInstance();
        this.relativeList = relativeList;
        this.freindList = friendList;
        this.memberlist = memberList;
        this.joined = joined;
        mContext = cnt;
        publicType = true;
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_joined,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(joined.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "onComplete: 1" + joined.get(position));
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                        assert user != null;
                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                House house = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                assert house != null;
                                holder.mHouseName.setText(house.getName());
                            }
                        });
                        holder.mMemberName.setText(user.getName());
                        Log.d(TAG, "onComplete: 1" + user.getName());
                        if (relativeList != null) {
                            Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                            if (relativeList.size() != 0) {
                                if (relativeList.contains(joined.get(position))) {
                                    publicType = false;
                                    UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                }
                            }
                        }
                        if (freindList != null) {
                            Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                            if (freindList.size() != 0) {
                                if (freindList.contains(joined.get(position))) {
                                    publicType = false;

                                    UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                }
                            }
                        }
                        if (memberlist != null) {
                            Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                            if (memberlist.size() != 0) {
                                if (memberlist.contains(joined.get(position))) {
                                    publicType = false;
                                    UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                }
                            }
                        }
                        if (publicType) {
                            UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "", mContext);
                        }

                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }



    }



    @Override
    public int getItemCount() {
        return joined.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName;
        TextView mHouseName;
        ImageView mMemberAvatar;
        ImageView mEllipse;
        TextView mJoined;


        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mHouseName = (TextView) itemView.findViewById(R.id.tv_house_name);
            mJoined = (TextView) itemView.findViewById(R.id.tv_joined);
        }
    }
}
