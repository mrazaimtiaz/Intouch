package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.Events.ViewEventActivity;
import com.intouchapp.intouch.Main.Posts.ViewPostActivity;
import com.intouchapp.intouch.Models.Event;
import com.intouchapp.intouch.Models.ListofStrings;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyOwnHolder> {

    //variable
    private List<Event> events;
    private List<String> relativeList,freindList,memberlist;

    Context mContext;
    boolean publicType;
    Drawable colorType;

    private RequestQueue mRequestQue;

    private String URL = "https://fcm.googleapis.com/fcm/send";

    private static final String TAG = "EventRecyclerAdapter";


    FirebaseFirestore mDb;



    public EventRecyclerAdapter(List<Event> events, List<String> relativeList, List<String> friendList, List<String> memberList, Context cnt){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");

        mDb = FirebaseFirestore.getInstance();
        this.relativeList = relativeList;
        this.freindList = friendList;
        this.memberlist = memberList;
        this.events = events;
        mContext = cnt;
        publicType = true;
        Log.d(TAG, "PostRecyclerAdapter: " + relativeList + friendList + memberList);

        mRequestQue = Volley.newRequestQueue(mContext);
        AndroidThreeTen.init(mContext);

    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_event,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        holder.mCategory.bringToFront();

        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(events.get(position).getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: 1" + events);
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        assert user != null;
                        holder.mName.setText(user.getName());
                        Log.d(TAG, "onComplete: 1" + user.getName());
                        if(relativeList != null){
                            Log.d(TAG, "onComplete: post recycler relativelist" + relativeList);
                            if(relativeList.size() != 0){
                                if(relativeList.contains(events.get(position).getU_id())){
                                    publicType = false;
                                    holder.mName.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                                }
                            }
                        }
                        if(freindList != null){
                            Log.d(TAG, "onComplete: post recycler freindList" + freindList);
                            if(freindList.size() != 0){
                                if(freindList.contains(events.get(position).getU_id())){
                                    publicType = false;
                                    holder.mName.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                                }
                            }
                        }
                        if(memberlist != null){
                            Log.d(TAG, "onComplete: post recycler memberlist" + memberlist);
                            if(memberlist.size() != 0){
                                if(memberlist.contains(events.get(position).getU_id())){
                                    publicType = false;
                                    holder.mName.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                                }
                            }
                        }
                        if(publicType){
                            holder.mName.setTextColor(ContextCompat.getColor(mContext,R.color.black));
                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        UniversalImageLoader.setImage(events.get(position).getImage(), holder.mEvent, null, "",mContext);

        if(events.get(position).getType().equals(mContext.getString(R.string.type_member))){
            holder.mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
           holder.mAttendent.setText(mContext.getString(R.string.family));
        }
        if(events.get(position).getType().equals(mContext.getString(R.string.type_relative))){
            holder.mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            holder.mAttendent.setText(mContext.getString(R.string.relative));
        }
        if(events.get(position).getType().equals(mContext.getString(R.string.type_friend))){
            holder.mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
            holder.mAttendent.setText(mContext.getString(R.string.friend));
        }
        if(events.get(position).getType().equals(mContext.getString(R.string.type_knower))){
            holder.mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.magenta));
            holder.mAttendent.setText(mContext.getString(R.string.knowers));
        }
        if(events.get(position).getType().equals(mContext.getString(R.string.type_hood))){
            holder.mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
            holder.mAttendent.setText(mContext.getString(R.string.hood));
        }
        if(events.get(position).getType().equals(mContext.getString(R.string.type_other))){
            holder.mAttendent.setTextColor(ContextCompat.getColor(mContext,R.color.black));
            holder.mAttendent.setText(mContext.getString(R.string.public_));
        }

        holder.mCategory.setText(events.get(position).getCategory());
        holder.mPlace.setText(events.get(position).getPlace());
        holder.mComment.setText(events.get(position).getDescription());
        holder.mDate.setText(events.get(position).getDate());
        holder.mTime.setText(events.get(position).getTime());

        try {
            mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Event event = Objects.requireNonNull(task.getResult()).toObject(Event.class);

                    assert event != null;
                    if(event.getRejected() != null){
                        if(event.getRejected().size() != 0){
                            if(event.getRejected().contains(FirebaseAuth.getInstance().getUid())){
                                holder.mAccept.setVisibility(View.GONE);
                                holder.mReject.setVisibility(View.GONE);
                                holder.mJoined.setVisibility(View.GONE);
                            }else{
                                if(event.getJoined() != null){
                                    if(event.getJoined().size() != 0){
                                        if(event.getJoined().contains(FirebaseAuth.getInstance().getUid())){
                                            holder.mJoined.setVisibility(View.VISIBLE);
                                            holder.mReject.setVisibility(View.GONE);
                                            holder.mAccept.setVisibility(View.GONE);
                                        }else{
                                            holder.mReject.setVisibility(View.VISIBLE);
                                            holder.mAccept.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        holder.mReject.setVisibility(View.VISIBLE);
                                        holder.mAccept.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    holder.mReject.setVisibility(View.VISIBLE);
                                    holder.mAccept.setVisibility(View.VISIBLE);
                                }
                            }
                        }else{
                            if(event.getJoined() != null){
                                if(event.getJoined().size() != 0){
                                    if(event.getJoined().contains(FirebaseAuth.getInstance().getUid())){
                                        holder.mJoined.setVisibility(View.VISIBLE);
                                        holder.mReject.setVisibility(View.GONE);
                                        holder.mAccept.setVisibility(View.GONE);
                                    }else{
                                        holder.mReject.setVisibility(View.VISIBLE);
                                        holder.mAccept.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    holder.mReject.setVisibility(View.VISIBLE);
                                    holder.mAccept.setVisibility(View.VISIBLE);
                                }
                            }else{
                                holder.mReject.setVisibility(View.VISIBLE);
                                holder.mAccept.setVisibility(View.VISIBLE);
                            }
                        }
                    }else{
                        if(event.getJoined() != null){
                            if(event.getJoined().size() != 0){
                                if(event.getJoined().contains(FirebaseAuth.getInstance().getUid())){
                                    holder.mJoined.setVisibility(View.VISIBLE);
                                    holder.mReject.setVisibility(View.GONE);
                                    holder.mAccept.setVisibility(View.GONE);
                                }else{
                                    holder.mReject.setVisibility(View.VISIBLE);
                                    holder.mAccept.setVisibility(View.VISIBLE);
                                }
                            }else{
                                holder.mReject.setVisibility(View.VISIBLE);
                                holder.mAccept.setVisibility(View.VISIBLE);
                            }
                        }else{
                            holder.mReject.setVisibility(View.VISIBLE);
                            holder.mAccept.setVisibility(View.VISIBLE);
                        }
                    }


                }
            });

        }catch (NullPointerException e){
            e.printStackTrace();
        }


        holder.mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mAccept.setVisibility(View.GONE);
                holder.mReject.setVisibility(View.GONE);
                holder.mJoined.setVisibility(View.VISIBLE);
                mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        List<String> id = new ArrayList<>();
                        final Event event = Objects.requireNonNull(task.getResult()).toObject(Event.class);
                        assert event != null;
                        if(event.getJoined() != null){
                            if(event.getJoined().size() != 0){
                                event.getJoined().add(FirebaseAuth.getInstance().getUid());

                                try {
                                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                final Notification notification = new Notification();
                                                ZoneId tz =  ZoneId.systemDefault();
                                                LocalDateTime localDateTime = LocalDateTime.now();
                                                long seconds = localDateTime.atZone(tz).toEpochSecond();
                                                int nanos = localDateTime.getNano();
                                                Timestamp timestamp = new Timestamp(seconds, nanos);
                                                String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                                notification.setId(n_id);
                                                notification.setC_id(events.get(position).getE_id());
                                                notification.setCategory(mContext.getString(R.string.category_event));
                                                notification.setS_id(FirebaseAuth.getInstance().getUid());
                                                notification.setR_id(events.get(position).getU_id());
                                                notification.setType(mContext.getString(R.string.notify_type_accept));
                                                notification.setStatus(mContext.getString(R.string.status_unread));
                                                notification.setTimestamp(timestamp);

                                                mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDb.collection(mContext.getString(R.string.collection_users)).document(event.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                assert user != null;
                                                                sendNotification(user.getDevice_token());
                                                            }
                                                        });
                                                    }
                                                });
                                            }else{
                                                holder.mAccept.setVisibility(View.VISIBLE);
                                                holder.mReject.setVisibility(View.VISIBLE);
                                                holder.mJoined.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                            }else{
                                id.add(FirebaseAuth.getInstance().getUid());
                                event.setJoined(id);
                                try {
                                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                final Notification notification = new Notification();
                                                ZoneId tz =  ZoneId.systemDefault();
                                                LocalDateTime localDateTime = LocalDateTime.now();
                                                long seconds = localDateTime.atZone(tz).toEpochSecond();
                                                int nanos = localDateTime.getNano();
                                                Timestamp timestamp = new Timestamp(seconds, nanos);

                                                String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                                notification.setId(n_id);
                                                notification.setC_id(events.get(position).getE_id());
                                                notification.setCategory(mContext.getString(R.string.category_event));
                                                notification.setS_id(FirebaseAuth.getInstance().getUid());
                                                notification.setR_id(events.get(position).getU_id());
                                                notification.setType(mContext.getString(R.string.notify_type_accept));
                                                notification.setStatus(mContext.getString(R.string.status_unread));
                                                notification.setTimestamp(timestamp);

                                                mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDb.collection(mContext.getString(R.string.collection_users)).document(event.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                                assert user != null;
                                                                sendNotification(user.getDevice_token());
                                                            }
                                                        });
                                                    }
                                                });

                                            }else{
                                                holder.mAccept.setVisibility(View.VISIBLE);
                                                holder.mReject.setVisibility(View.VISIBLE);
                                                holder.mJoined.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                            }
                        }else{
                            id.add(FirebaseAuth.getInstance().getUid());
                            event.setJoined(id);
                            mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        final Notification notification = new Notification();
                                        ZoneId tz =  ZoneId.systemDefault();
                                        LocalDateTime localDateTime = LocalDateTime.now();
                                        long seconds = localDateTime.atZone(tz).toEpochSecond();
                                        int nanos = localDateTime.getNano();
                                        Timestamp timestamp = new Timestamp(seconds, nanos);

                                        String n_id =  mDb.collection(mContext.getString(R.string.collection_notification)).document().getId();
                                        notification.setId(n_id);
                                        notification.setC_id(events.get(position).getE_id());
                                        notification.setCategory(mContext.getString(R.string.category_event));
                                        notification.setS_id(FirebaseAuth.getInstance().getUid());
                                        notification.setR_id(events.get(position).getU_id());
                                        notification.setType(mContext.getString(R.string.notify_type_accept));
                                        notification.setStatus(mContext.getString(R.string.status_unread));
                                        notification.setTimestamp(timestamp);

                                        try {
                                            mDb.collection(mContext.getString(R.string.collection_notification)).document(n_id).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mDb.collection(mContext.getString(R.string.collection_users)).document(event.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                            assert user != null;
                                                            sendNotification(user.getDevice_token());
                                                        }
                                                    });
                                                }
                                            });
                                        }catch (NullPointerException e){
                                            e.printStackTrace();
                                        }

                                    }else{
                                        holder.mAccept.setVisibility(View.VISIBLE);
                                        holder.mReject.setVisibility(View.VISIBLE);
                                        holder.mJoined.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        holder.mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mAccept.setVisibility(View.GONE);
                holder.mReject.setVisibility(View.GONE);

                try {
                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            List<String> id = new ArrayList<>();
                            Event event = Objects.requireNonNull(task.getResult()).toObject(Event.class);
                            assert event != null;
                            if(event.getRejected() != null){
                                if(event.getRejected().size() != 0){
                                    event.getRejected().add(FirebaseAuth.getInstance().getUid());
                                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }else{
                                                holder.mAccept.setVisibility(View.VISIBLE);
                                                holder.mReject.setVisibility(View.VISIBLE);
                                                holder.mJoined.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }else{
                                    id.add(FirebaseAuth.getInstance().getUid());
                                    event.setRejected(id);
                                    mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }else{
                                                holder.mAccept.setVisibility(View.VISIBLE);
                                                holder.mReject.setVisibility(View.VISIBLE);
                                                holder.mJoined.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }else{
                                id.add(FirebaseAuth.getInstance().getUid());
                                event.setRejected(id);
                                mDb.collection(mContext.getString(R.string.collection_events)).document(events.get(position).getE_id()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }else{
                                            holder.mAccept.setVisibility(View.VISIBLE);
                                            holder.mReject.setVisibility(View.VISIBLE);
                                            holder.mJoined.setVisibility(View.GONE);
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
        });

    }
    //*********************************************** send notification *****************************************************
    private void sendNotification(String token) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",mContext.getString(R.string.event));
            notificationObj.put("body", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + " " + mContext.getString(R.string.notification_body_accept)) ;
            notificationObj.put("sound","default");

            json.put("notification",notificationObj);



            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {

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

    private void viewItem(int position){
        Intent intent = new Intent(mContext, ViewEventActivity.class);
        ListofStrings list = new ListofStrings();
        list.setFriendList(freindList);
        list.setMemberList(memberlist);
        list.setRelativeList(relativeList);

        intent.putExtra(mContext.getString(R.string.intent_type),list);
        intent.putExtra(mContext.getString(R.string.post),events.get(position));
        mContext.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //widget
        TextView mCategory;
        TextView mPlace,mAttendent,mName,mComment,mDate,mTime;
        ImageView mEvent;
        Button mAccept,mReject,mJoined;


        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mCategory = (TextView) itemView.findViewById(R.id.tv_category);
            mPlace = (TextView) itemView.findViewById(R.id.tv_place);
            mAttendent = (TextView) itemView.findViewById(R.id.tv_attendent);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mComment = (TextView) itemView.findViewById(R.id.tv_description);
            mDate = (TextView) itemView.findViewById(R.id.event_date);
            mTime = (TextView) itemView.findViewById(R.id.tv_event_time);
            mEvent = (ImageView) itemView.findViewById(R.id.iv_event);
            mAccept = (Button) itemView.findViewById(R.id.btn_accept);
            mReject = (Button) itemView.findViewById(R.id.btn_reject);
            mJoined = (Button) itemView.findViewById(R.id.btn_joined);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            viewItem(getAdapterPosition());
        }
    }
}
