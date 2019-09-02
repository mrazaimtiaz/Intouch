package com.intouchapp.intouch.Utills;

import android.annotation.SuppressLint;
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
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.MyOwnHolder> {

    //variable
    private List<Comment> comments;
    private List<String> relativeList,freindList,memberlist;
    Context mContext;



    boolean publicType;

    private FirebaseFirestore mDb;
    private static final String TAG = "MemberRecyclerViewAdapt";


    public CommentsRecyclerAdapter(List<Comment> comments, List<String> relativeList, List<String> friendList, List<String> memberList, Context cnt){

        mDb = FirebaseFirestore.getInstance();
        this.relativeList = relativeList;
        this.freindList = friendList;
        this.memberlist = memberList;
        this.comments = comments;
        mContext = cnt;
        publicType = true;



        Log.d(TAG, "PostRecyclerAdapter: " + relativeList + friendList + memberList);
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_comments,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {
    try {

        mDb.collection(mContext.getString(R.string.collection_users)).document(comments.get(position).getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Log.d(TAG, "onComplete: 1" + comments);
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    assert user != null;
                    holder.mMemberName.setText(user.getName());
                    Log.d(TAG, "onComplete: 1" + user.getName());
                    if (relativeList != null) {
                        Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                        if (relativeList.size() != 0) {
                            if (relativeList.contains(comments.get(position).getU_id())) {
                                publicType = false;
                                UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "", mContext);
                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                            }
                        }
                    }
                    if (freindList != null) {
                        Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                        if (freindList.size() != 0) {
                            if (freindList.contains(comments.get(position).getU_id())) {
                                publicType = false;

                                UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "", mContext);
                                holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                            }
                        }
                    }
                    if (memberlist != null) {
                        Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                        if (memberlist.size() != 0) {
                            if (memberlist.contains(comments.get(position).getU_id())) {
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
        holder.mComment.setText(comments.get(position).getComment());
        long seconds = (System.currentTimeMillis() / 1000) - comments.get(position).getTimestamp().getSeconds();
        Log.d(TAG, "onBindViewHolder: " + seconds);
        long diffMinutes = seconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        if (seconds < 60) {
            holder.mDateTime.setText(seconds + mContext.getString(R.string.seconds_ago));
        } else if (diffMinutes < 60) {
            holder.mDateTime.setText(diffMinutes + mContext.getString(R.string.minutes_ago));
        } else if (diffHours < 24) {
            holder.mDateTime.setText(diffHours + mContext.getString(R.string.hours_ago));
        } else if (diffHours < 48) {
            holder.mDateTime.setText(mContext.getString(R.string.yesterday));
        } else {
            holder.mDateTime.setText(diffDays + mContext.getString(R.string.days_ago));
        }
    }catch (NullPointerException e){
        Log.d(TAG, "onBindViewHolder: NullPointerException " + e.getMessage());
    }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mMemberName;
        ImageView mMemberAvatar;
        ImageView mEllipse;
        TextView mDateTime;
        TextView mComment;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_name);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mDateTime = (TextView) itemView.findViewById(R.id.tv_dateamdtime);
            mComment = (TextView) itemView.findViewById(R.id.tv_comment);

        }
    }
}
