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
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class KnowersRecyclerAdapter extends RecyclerView.Adapter<KnowersRecyclerAdapter.MyOwnHolder> {

    //variable
    List<String> list;

    String type;
    Context mContext;

    FirebaseFirestore mDb;

    private static final String TAG = "MemberRecyclerViewAdapt";


    public KnowersRecyclerAdapter(List<String> list, String type, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.list = list;
        this.type = type;
        mContext = cnt;

        mDb = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_knowers,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(list.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    if(user != null){
                        if(type.equals(mContext.getString(R.string.type_relative))){
                            Log.d(TAG, "onComplete: userid " + user.getU_id() +user.getPublic_avatar() + user.getRel_avatar());
                            UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                            holder.mMemberName.setText(user.getName());
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_relative_red));
                            holder.tv_type.setText(mContext.getString(R.string.type_relative));
                        }
                        else if(type.equals(mContext.getString(R.string.type_friend))){
                            UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                            holder.mMemberName.setText(user.getName());
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_friends_blue));
                            holder.tv_type.setText(mContext.getString(R.string.type_friend));
                        }
                        else if(type.equals(mContext.getString(R.string.type_member))){
                            UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                            holder.mMemberName.setText(user.getName());
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));
                            holder.tv_type.setText(mContext.getString(R.string.type_friend));
                        }
                    }

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        holder.mMemberName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).equals(FirebaseAuth.getInstance().getUid())){
                    Intent intent = new Intent(mContext, AccountActivity.class);
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),list.get(position));
                    intent.putExtra(mContext.getString(R.string.intent_type),type);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).equals(FirebaseAuth.getInstance().getUid())){
                    Intent intent = new Intent(mContext, AccountActivity.class);
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),list.get(position));
                    intent.putExtra(mContext.getString(R.string.intent_type),type);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.mType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).equals(FirebaseAuth.getInstance().getUid())){
                    Intent intent = new Intent(mContext, AccountActivity.class);
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),list.get(position));
                    intent.putExtra(mContext.getString(R.string.intent_type),type);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.tv_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).equals(FirebaseAuth.getInstance().getUid())){
                    Intent intent = new Intent(mContext, AccountActivity.class);
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_memberid),list.get(position));
                    intent.putExtra(mContext.getString(R.string.intent_type),type);
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName,tv_type;
        ImageView mMemberAvatar;
        ImageView mEllipse,mType;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }
}
