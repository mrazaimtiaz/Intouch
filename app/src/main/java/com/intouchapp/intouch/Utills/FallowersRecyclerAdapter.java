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
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class FallowersRecyclerAdapter extends RecyclerView.Adapter<FallowersRecyclerAdapter.MyOwnHolder> {

    //variable
    private List<String> id;;

    Context mContext;
    User ownUser;

    private FirebaseFirestore mDb;

    private static final String TAG = "FallowersRecyclerAdapte";


    public FallowersRecyclerAdapter(List<String> id, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.id = id;
        mContext = cnt;
        mDb = FirebaseFirestore.getInstance();
        ownUser = ((UserClient) (mContext.getApplicationContext())).getUser();
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_fallowers,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(id.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        final User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        assert user != null;
                        holder.mMemberName.setText(user.getName());

                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(user.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                assert house != null;
                                holder.mHouseName.setText(house.getName());



                                Log.d(TAG, "checkType: typehood");
                                mDb.collection(mContext.getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection(mContext.getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        Log.d(TAG, "onComplete: into the task");
                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                        for(int i = 0; i < list.size(); i++){
                                            Relative relative = list.get(i).toObject(Relative.class);
                                            assert relative != null;
                                            List<String> member = relative.getR_id();
                                            Log.d(TAG, "onComplete: first for loop");
                                            if(member.contains(id.get(position))) {
                                                Log.d(TAG, "onComplete: if loop matched");
                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                                UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);

                                            }else {
                                                Log.d(TAG, "onComplete: 2nd databasee");
                                                mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        Log.d(TAG, "onComplete: into the task2");
                                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                        for(int i = 0; i < list.size(); i++) {
                                                            Friend friend = list.get(i).toObject(Friend.class);
                                                            assert friend != null;
                                                            List<String> member = friend.getF_id();
                                                            if(member.contains(id.get(position))) {
                                                                Log.d(TAG, "onComplete:friend mathced");
                                                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                                                UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                                break;
                                                            }else {
                                                                mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                                        assert house != null;
                                                                        List<String> member = house.getMembers();
                                                                        if(member.contains(id.get(position))){
                                                                            Log.d(TAG, "onComplete:member mathced");
                                                                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                                                            UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                                        }else{
                                                                            Log.d(TAG, "onComplete: others");
                                                                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_black_ellipse));
                                                                            UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                                                                        }
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

                            }
                        });
                    }
                }
            });

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    public void intentMethod(final int position) {
        if(id.get(position).equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            Intent intent = new Intent(mContext, AccountActivity.class);
            mContext.startActivity(intent);
        }
        else {
                Intent intent = new Intent(mContext, MemberInfoActivity.class);
                intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_hood));
                intent.putExtra(mContext.getString(R.string.intent_memberid),id.get(position));
                mContext.startActivity(intent);
        }
    }
    @Override
    public int getItemCount() {
        return id.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //widget
        TextView mMemberName,mHouseName;
        ImageView mMemberAvatar;
        ImageView mEllipse,mIcon;


        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mHouseName = (TextView) itemView.findViewById(R.id.tv_house_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_type);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            intentMethod(getAdapterPosition());
        }
    }
}
