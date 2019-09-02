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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

public class ShareWithRecyclerAdapter extends RecyclerView.Adapter<ShareWithRecyclerAdapter.MyOwnHolder> {

    //variable
    List<String> memberList;
    String type;
    Context mContext;

    FirebaseFirestore mDb;

    User ownUser;

    private static final String TAG = "MemberRecyclerViewAdapt";


    public ShareWithRecyclerAdapter(List<String> member, String type, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.memberList = member;
        this.type = type;
        mContext = cnt;

        mDb = FirebaseFirestore.getInstance();
        ownUser = ((UserClient) (mContext.getApplicationContext())).getUser();
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_privacy,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        try {
            if(memberList.get(position) != null){
                mDb.collection(mContext.getString(R.string.collection_users)).document(memberList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: id of member " + memberList.get(position));
                            try {
                                final User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                assert user != null;
                                holder.mName.setText(user.getName());

                                if (type.equals(mContext.getString(R.string.type_member))) {
                                    holder.mType.setText(mContext.getString(R.string.type_member));
                                    UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                                    holder.mIvtype.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_shelter_green));
                                }
                                if (type.equals(mContext.getString(R.string.type_relative))) {
                                    holder.mType.setText(mContext.getString(R.string.type_relative));
                                    UniversalImageLoader.setImage(user.getRel_avatar(), holder.mAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                                    holder.mIvtype.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_relative_red));
                                }
                                if (type.equals(mContext.getString(R.string.type_friend))) {
                                    holder.mType.setText(mContext.getString(R.string.type_friend));
                                    UniversalImageLoader.setImage(user.getRel_avatar(), holder.mAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                    holder.mIvtype.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_friends_blue));
                                }
                                if (type.equals(mContext.getString(R.string.type_knower))) {
                                    holder.mType.setText(mContext.getString(R.string.type_knower));


                                    mDb.collection(mContext.getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection(mContext.getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            Log.d(TAG, "onComplete: into the task");
                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                            for (int i = 0; i < list.size(); i++) {
                                                Relative relative = list.get(i).toObject(Relative.class);
                                                assert relative != null;
                                                List<String> member = relative.getR_id();
                                                Log.d(TAG, "onComplete: first for loop");
                                                if (member.contains(memberList.get(position))) {
                                                    Log.d(TAG, "onComplete: if loop matched");
                                                    UniversalImageLoader.setImage(user.getRel_avatar(), holder.mAvatar, null, "", mContext);

                                                } else {
                                                    Log.d(TAG, "onComplete: 2nd databasee");
                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            Log.d(TAG, "onComplete: into the task2");
                                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                            for (int i = 0; i < list.size(); i++) {
                                                                Friend friend = list.get(i).toObject(Friend.class);
                                                                assert friend != null;
                                                                List<String> member = friend.getF_id();
                                                                if (member.contains(memberList.get(position))) {
                                                                    Log.d(TAG, "onComplete:friend mathced");
                                                                    UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mAvatar, null, "", mContext);
                                                                    break;
                                                                } else {
                                                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(ownUser.getN_id()).collection(mContext.getString(R.string.collection_houses)).document(ownUser.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                                            assert house != null;
                                                                            List<String> member = house.getMembers();
                                                                            if (member.contains(memberList.get(position))) {
                                                                                Log.d(TAG, "onComplete:member mathced");
                                                                                UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mAvatar, null, "", mContext);
                                                                            } else {
                                                                                Log.d(TAG, "onComplete: others");
                                                                                UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mAvatar, null, "", mContext);
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
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_magenta_ellipse));
                                    holder.mIvtype.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_knower_magenta));
                                }
                                if (type.equals(mContext.getString(R.string.type_hood))) {
                                    holder.mType.setText(mContext.getString(R.string.type_hood));
                                    UniversalImageLoader.setImage(user.getRel_avatar(), holder.mAvatar, null, "", mContext);
                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_yellow_ellipse));
                                    holder.mIvtype.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_castle_with_flag_yellow));
                                }
                            }catch (NullPointerException e){
                                Log.d(TAG, "onComplete: NullPointerException ");
                                notifyItemRemoved(position);
                            }
                        }

                    }
                });
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }

            //UniversalImageLoader.setImage(memberAvatar[position], holder.mMemberAvatar, null, "",mContext);


    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mName,mType;
        ImageView mIvtype,mAvatar,mEllipse;


        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mIvtype = (ImageView) itemView.findViewById(R.id.iv_type);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);

        }
    }
}
