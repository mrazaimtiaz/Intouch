package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Home.HoodInfoActivity;
import com.intouchapp.intouch.Main.Home.MainHouseInfoActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchingRecyclerAdapter extends RecyclerView.Adapter<SearchingRecyclerAdapter.MyOwnHolder> {

    //variable
    List<Hood> hood;
    String type;
    Context mContext;

    List<String> relative;
    List<String> friend;
    List<String> member;

    List<House> house;
    List<User> user;

    String personType;

    User ownUser;

    boolean check;


    private static final String TAG = "MemberRecyclerViewAdapt";


    public SearchingRecyclerAdapter(List<Hood> hood, List<House> house, List<User> user, List<String> relative, List<String> friend, List<String> member,String type, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.hood = hood;
        this.type = type;
        this.relative = relative;
        this.friend = friend;
        this.member = member;
        this.house = house;
        this.user = user;
        this.mContext = cnt;
        personType = mContext.getString(R.string.empty);

        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();

        check = true;
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_searching,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOwnHolder holder, final int position) {

        if(type.equals(mContext.getString(R.string.type_hood))){
            holder.mMemberName.setText(hood.get(position).getName());
            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_yellow_ellipse));
            UniversalImageLoader.setImage(hood.get(position).getImage(), holder.mMemberAvatar, null, "",mContext);
            holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_castle_with_flag_yellow));
            holder.type.setText(mContext.getString(R.string.type_hood));
            personType = mContext.getString(R.string.type_hood);
        }
        if(type.equals(mContext.getString(R.string.type_people))){
            holder.mMemberName.setText(user.get(position).getName());
            if(user != null){
                if(relative != null){
                    if (relative.contains(user.get(position).getU_id())){
                        check = false;
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                        UniversalImageLoader.setImage(user.get(position).getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                        personType = mContext.getString(R.string.type_relative);
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_person_red_24px));

                    }
                }
                if(friend != null){
                    if (friend.contains(user.get(position).getU_id())){
                        check = false;
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                        UniversalImageLoader.setImage(user.get(position).getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                        personType = mContext.getString(R.string.type_friend);
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_person_blue_24px));
                    }
                }
                if(member != null){
                    if (member.contains(user.get(position).getU_id())){
                        check = false;
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                        UniversalImageLoader.setImage(user.get(position).getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                        personType = mContext.getString(R.string.type_member);
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_person_green_24px));
                    }
                    if(check){
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_black_ellipse));
                        UniversalImageLoader.setImage(user.get(position).getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                        personType = mContext.getString(R.string.type_other);
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_person_24px));
                    }
                }
                holder.type.setText(mContext.getString(R.string.type_people));
            }

        }
        if(type.equals(mContext.getString(R.string.type_house))){
            holder.mMemberName.setText(house.get(position).getName());
            UniversalImageLoader.setImage(house.get(position).getImage(), holder.mMemberAvatar, null, "",mContext);
            if(house != null){
                if(relative != null){
                    if (relative.contains(house.get(position).getH_id())){
                        check = false;
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                        personType = mContext.getString(R.string.type_relative);
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_house));

                    }
                }
                if(friend != null){
                    if (friend.contains(house.get(position).getH_id())){
                        check = false;
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                        personType = mContext.getString(R.string.type_friend);
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_house));
                    }
                }
                Log.d(TAG, "onBindViewHolder: " + ownUser.getH_id().equals(house.get(position).getH_id()));
                if(ownUser != null){
                        Log.d(TAG, "onBindViewHolder: called");
                            if(ownUser.getH_id().equals(house.get(position).getH_id())) {
                                check = false;
                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                personType = mContext.getString(R.string.type_member);
                                holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));
                            }else{
                                if(check){
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_black_ellipse));
                                    personType = mContext.getString(R.string.type_other);
                                    holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_house));
                                }
                            }
                    }


                }
                holder.type.setText(mContext.getString(R.string.type_house));
            }

        holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals(mContext.getString(R.string.type_hood))){
                    if(hood != null){
                        Intent intent = new Intent(mContext, HoodInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.n_id),hood.get(position).getId());
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_people))){
                    if(user != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_memberid),user.get(position).getU_id());
                        intent.putExtra(mContext.getString(R.string.intent_type),personType);
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_house))){
                    if(house != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.house_id),house.get(position).getH_id()+house.get(position).getN_id()+personType);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        holder.mMemberName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals(mContext.getString(R.string.type_hood))){
                    if(hood != null){
                        Intent intent = new Intent(mContext, HoodInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.n_id),hood.get(position).getId());
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_people))){
                    if(user != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_memberid),user.get(position).getU_id());
                        intent.putExtra(mContext.getString(R.string.intent_type),personType);
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_house))){
                    if(house != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.house_id),house.get(position).getH_id()+house.get(position).getN_id()+personType);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        holder.mType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals(mContext.getString(R.string.type_hood))){
                    if(hood != null){
                        Intent intent = new Intent(mContext, HoodInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.n_id),hood.get(position).getId());
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_people))){
                    if(user != null && !personType.equals(mContext.getString(R.string.empty))){
                        if(user.get(position).getU_id().equals(FirebaseAuth.getInstance().getUid())){
                            Intent intent = new Intent(mContext, AccountActivity.class);
                            mContext.startActivity(intent);
                        }else{
                            Intent intent = new Intent(mContext, MemberInfoActivity.class);
                            intent.putExtra(mContext.getString(R.string.intent_memberid),user.get(position).getU_id());
                            intent.putExtra(mContext.getString(R.string.intent_type),personType);
                            mContext.startActivity(intent);
                        }
                    }
                }
                if(type.equals(mContext.getString(R.string.type_house))){
                    if(house != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.house_id),house.get(position).getH_id()+house.get(position).getN_id()+personType);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        holder.type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals(mContext.getString(R.string.type_hood))){
                    if(hood != null){
                        Intent intent = new Intent(mContext, HoodInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.n_id),hood.get(position).getId());
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_people))){
                    if(user != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_memberid),user.get(position).getU_id());
                        intent.putExtra(mContext.getString(R.string.intent_type),personType);
                        mContext.startActivity(intent);
                    }
                }
                if(type.equals(mContext.getString(R.string.type_house))){
                    if(house != null && !personType.equals(mContext.getString(R.string.empty))){
                        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.house_id),house.get(position).getH_id()+house.get(position).getN_id()+personType);
                        mContext.startActivity(intent);
                    }
                }
            }
        });



        }


    @Override
    public int getItemCount() {
        if(hood != null){
            return hood.size();
        }else if(house != null){
            return  house.size();
        }else if(user != null){
            return  user.size();
        }else{
            return 0;
        }

    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName,type;
        ImageView mMemberAvatar;
        ImageView mEllipse,mType;


        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);
            type = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }
}
