package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.intouchapp.intouch.Main.Home.MainHouseInfoActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendRequestsRecyclerAdapter extends RecyclerView.Adapter<SendRequestsRecyclerAdapter.MyOwnHolder> {

    //variable
    List<Request> requests;
    Context mContext;

    //firebase
    private FirebaseFirestore mDb;

    private static final String TAG = "MemberRecyclerViewAdapt";


    public SendRequestsRecyclerAdapter(List<Request> requests, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");

        this.requests = requests;
        mDb = FirebaseFirestore.getInstance();
        mContext = cnt;

    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_sent_requests,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

       if(requests.get(position).getType().equals(mContext.getString(R.string.type_relative))){

           try {
               mDb.collection(mContext.getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {

                       if(task.isSuccessful()){
                           List<DocumentSnapshot> list =  task.getResult().getDocuments();

                           for(int i = 0; i < list.size() ; i++){
                               mDb.collection(mContext.getString(R.string.collection_hoods)).document(list.get(i).getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                   @Override
                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       List<DocumentSnapshot> list1 = task.getResult().getDocuments();

                                       for(int i = 0; i < list1.size() ; i++){
                                           final House house = list1.get(i).toObject(House.class);

                                           if(house.getH_id().equals(requests.get(position).getR_id())){
                                               holder.mMemberName.setText(house.getName());
                                               UniversalImageLoader.setImage(house.getImage(), holder.mMemberAvatar, null, "",mContext);
                                               holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                               holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_relative_red));



                                               holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                                       intent.putExtra(mContext.getString(R.string.house_id),house.getH_id()+house.getN_id()+mContext.getString(R.string.type_relative));
                                                       mContext.startActivity(intent);
                                                   }
                                               });

                                               holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                                       intent.putExtra(mContext.getString(R.string.house_id),house.getH_id()+house.getN_id()+mContext.getString(R.string.type_relative));
                                                       mContext.startActivity(intent);
                                                   }
                                               });

                                               holder.mType.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                                       intent.putExtra(mContext.getString(R.string.house_id),house.getH_id()+house.getN_id()+mContext.getString(R.string.type_relative));
                                                       mContext.startActivity(intent);
                                                   }
                                               });

                                           }
                                       }
                                   }
                               });
                           }

                       }
                   }
               });
           }catch (NullPointerException e){
               e.printStackTrace();
           }

       }
       if(requests.get(position).getType().equals(mContext.getString(R.string.type_friend))){
           try {
               mDb.collection(mContext.getString(R.string.collection_users)).document(requests.get(position).getR_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.isSuccessful()){
                           User user = task.getResult().toObject(User.class);

                           holder.mMemberName.setText(user.getName());
                           UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                           holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                           holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_friends_blue));


                           holder.mMemberName.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if(requests.get(position).getType().equals(mContext.getString(R.string.type_friend))) {
                                       Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                       intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getR_id());
                                       intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                       mContext.startActivity(intent);
                                   }
                               }
                           });
                           holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if(requests.get(position).getType().equals(mContext.getString(R.string.type_friend))) {
                                       Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                       intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getR_id());
                                       intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                       mContext.startActivity(intent);
                                   }
                               }
                           });
                           holder.mType.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if(requests.get(position).getType().equals(mContext.getString(R.string.type_friend))) {
                                       Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                       intent.putExtra(mContext.getString(R.string.intent_memberid),requests.get(position).getR_id());
                                       intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                       mContext.startActivity(intent);
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


    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName;
        ImageView mMemberAvatar;
        ImageView mEllipse;
        ImageView mType;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);
        }
    }
}
