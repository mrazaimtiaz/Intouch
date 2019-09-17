package com.intouchapp.intouch.Utills;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.Account.AccountActivity;
import com.intouchapp.intouch.Main.Events.ViewEventActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Main.Posts.ViewPostActivity;
import com.intouchapp.intouch.Models.Event;
import com.intouchapp.intouch.Models.ListofStrings;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.List;
import java.util.Objects;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.MyOwnHolder> {

    //variable
    List<Notification> notifications;
    List<String> memberList,friendList,relativeList;

    boolean publicType;
    Context mContext;

    FirebaseFirestore mDb;

    private static final String TAG = "MemberRecyclerViewAdapt";




    public NotificationRecyclerAdapter(List<Notification> notifications,List<String> memberList,List<String> freindList,List<String> relativeList, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.notifications = notifications;
        this.memberList = memberList;
        this.friendList = freindList;
        this.relativeList = relativeList;

        publicType = true;

        mDb = FirebaseFirestore.getInstance();
        AndroidThreeTen.init(mContext);

        mContext = cnt;
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_notification,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {


        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(notifications.get(position).getS_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                    Log.d(TAG, "onComplete: " + memberList);
                    if(memberList != null){
                        if(memberList.contains(user.getU_id())){
                            Log.d(TAG, "onComplete: into the member");
                            publicType = false;
                            assert user != null;
                            UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));

                            holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(notifications.get(position).getS_id())){
                                        Intent intent = new Intent(mContext, AccountActivity.class);
                                        mContext.startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                                        intent.putExtra(mContext.getString(R.string.intent_memberid),notifications.get(position).getS_id());
                                        mContext.startActivity(intent);

                                    }

                                }
                            });
                        }
                    }
                    if(friendList != null){
                        if(friendList.contains(user.getU_id())){
                            publicType = false;
                            assert user != null;
                            UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));

                            holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(notifications.get(position).getS_id())){
                                        Intent intent = new Intent(mContext, AccountActivity.class);
                                        mContext.startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                                        intent.putExtra(mContext.getString(R.string.intent_memberid),notifications.get(position).getS_id());
                                        mContext.startActivity(intent);

                                    }

                                }
                            });
                        }
                    }
                    if(relativeList != null){
                        assert relativeList != null;
                        if(relativeList.contains(user.getU_id())){
                            publicType = false;
                            assert user != null;
                            UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));

                            holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(notifications.get(position).getS_id())){
                                        Intent intent = new Intent(mContext, AccountActivity.class);
                                        mContext.startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                                        intent.putExtra(mContext.getString(R.string.intent_memberid),notifications.get(position).getS_id());
                                        mContext.startActivity(intent);

                                    }

                                }
                            });
                        }
                    }
                    if(publicType){
                        assert user != null;
                        UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                        holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_black_ellipse));

                        holder.mMemberAvatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(notifications.get(position).getS_id())){
                                    Intent intent = new Intent(mContext, AccountActivity.class);
                                    mContext.startActivity(intent);
                                }else{
                                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_other));
                                    intent.putExtra(mContext.getString(R.string.intent_memberid),notifications.get(position).getS_id());
                                    mContext.startActivity(intent);

                                }

                            }
                        });
                    }


                    assert user != null;
                    SpannableString span1 = new SpannableString(Html.fromHtml("<font color=" + "#000000 " + "face=dosis_semibold "  + ">" + user.getName() + "</font>"));
                    span1.setSpan(new AbsoluteSizeSpan( holder.itemView.getResources().getDimensionPixelSize(R.dimen._13ssp)), 0, span1.length(), SPAN_INCLUSIVE_INCLUSIVE);

                    if(notifications.get(position).getCategory().equals(mContext.getString(R.string.category_post)) && notifications.get(position).getType().equals(mContext.getString(R.string.notify_type_like))){
                        CharSequence finalText = TextUtils.concat(span1," ",mContext.getString(R.string.notification_body_like));
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_24px));
                        holder.mNotification.setText(finalText);
                    }
                    Log.d(TAG, "onComplete: comment " + notifications.get(position).getType());
                    if(notifications.get(position).getCategory().equals(mContext.getString(R.string.category_post)) && notifications.get(position).getType().equals(mContext.getString(R.string.notify_type_comment))){
                        Log.d(TAG, "onComplete: comment matched " + notifications.get(position).getType());
                        CharSequence finalText = TextUtils.concat(span1," ",mContext.getString(R.string.notification_body_comment));
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_post_24px));
                        holder.mNotification.setText(finalText);
                    }
                    if(notifications.get(position).getCategory().equals(mContext.getString(R.string.category_event)) && notifications.get(position).getType().equals(mContext.getString(R.string.notify_type_accept))){
                        CharSequence finalText = TextUtils.concat(span1," ",mContext.getString(R.string.notification_body_accept));
                        holder.mType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_event));
                        holder.mNotification.setText(finalText);
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(notifications.get(position).getCategory().equals(mContext.getString(R.string.category_post))){

            try {
                mDb.collection(mContext.getString(R.string.collection_post)).document(notifications.get(position).getC_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final Post post = Objects.requireNonNull(task.getResult()).toObject(Post.class);
                            assert post != null;
                            UniversalImageLoader.setImage(post.getImage(), holder.mNotificationImage, null, "",mContext);

                            holder.mNotificationImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ViewPostActivity.class);
                                    ListofStrings list = new ListofStrings();
                                    list.setFriendList(friendList);
                                    list.setMemberList(memberList);
                                    list.setRelativeList(relativeList);

                                    intent.putExtra(mContext.getString(R.string.intent_type),list);
                                    intent.putExtra(mContext.getString(R.string.post),post);
                                    mContext.startActivity(intent);

                                }
                            });

                            holder.mNotification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ViewPostActivity.class);
                                    ListofStrings list = new ListofStrings();
                                    list.setFriendList(friendList);
                                    list.setMemberList(memberList);
                                    list.setRelativeList(relativeList);

                                    intent.putExtra(mContext.getString(R.string.intent_type),list);
                                    intent.putExtra(mContext.getString(R.string.post),post);
                                    mContext.startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
        if(notifications.get(position).getCategory().equals(mContext.getString(R.string.category_event))){

            try {
                mDb.collection(mContext.getString(R.string.collection_events)).document(notifications.get(position).getC_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final Event event = task.getResult().toObject(Event.class);
                            UniversalImageLoader.setImage(event.getImage(), holder.mNotificationImage, null, "",mContext);

                            holder.mNotificationImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ViewEventActivity.class);
                                    ListofStrings list = new ListofStrings();
                                    list.setFriendList(friendList);
                                    list.setMemberList(memberList);
                                    list.setRelativeList(relativeList);

                                    intent.putExtra(mContext.getString(R.string.intent_type),list);
                                    intent.putExtra(mContext.getString(R.string.post),event);
                                    mContext.startActivity(intent);

                                }
                            });

                            holder.mNotification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ViewEventActivity.class);
                                    ListofStrings list = new ListofStrings();
                                    list.setFriendList(friendList);
                                    list.setMemberList(memberList);
                                    list.setRelativeList(relativeList);

                                    intent.putExtra(mContext.getString(R.string.intent_type),list);
                                    intent.putExtra(mContext.getString(R.string.post),event);
                                    mContext.startActivity(intent);

                                }
                            });
                        }
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

        notifications.get(position).setStatus(mContext.getString(R.string.status_read));
        if(notifications.get(position).getId() != null)
            try {
                mDb.collection(mContext.getString(R.string.collection_notification)).document(notifications.get(position).getId()).set(notifications.get(position));
            }catch (NullPointerException e){
                e.printStackTrace();
            }




        long seconds = (System.currentTimeMillis() / 1000) - notifications.get(position).getTimestamp().getSeconds();
        Log.d(TAG, "onBindViewHolder: " + seconds);
        long diffMinutes = seconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        if(seconds < 60){
            Log.d(TAG, "onBindViewHolder: datetime s");
            holder.mDateTime.setText (seconds + mContext.getString(R.string.seconds_ago));
        }else if(diffMinutes < 60){
            Log.d(TAG, "onBindViewHolder: datetime s1");
            holder.mDateTime.setText(diffMinutes +  mContext.getString(R.string.minutes_ago));
        }else if(diffHours < 24){
            Log.d(TAG, "onBindViewHolder: datetime s2");
            holder.mDateTime.setText(diffHours + mContext.getString(R.string.hours_ago));
        }else if(diffHours < 48){
            Log.d(TAG, "onBindViewHolder: datetime s3");
            holder.mDateTime.setText(mContext.getString(R.string.yesterday));
        }
        else{
            holder.mDateTime.setText(diffDays + mContext.getString(R.string.days_ago));
        }




    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        //widget
        TextView mNotification;
        ImageView mMemberAvatar;
        ImageView mEllipse;
        TextView mDateTime;
        ImageView mNotificationImage;
        ImageView mType;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (ImageView) itemView.findViewById(R.id.iv_member_avatar);
            mNotification = (TextView) itemView.findViewById(R.id.tv_notification);
            mEllipse = (ImageView) itemView.findViewById(R.id.iv_ellipse);
            mDateTime = (TextView) itemView.findViewById(R.id.tv_dateamdtime);
            mNotificationImage = (ImageView) itemView.findViewById(R.id.iv_notificationimage);
            mType = (ImageView) itemView.findViewById(R.id.iv_type);

            if(getAdapterPosition() != -1){
                if(notifications.get(getAdapterPosition()).getStatus().equals(mContext.getString(R.string.status_unread))){
                    itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.lime_green));
                }
            }
        }
    }
}
