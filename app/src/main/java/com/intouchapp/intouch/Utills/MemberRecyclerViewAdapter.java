package com.intouchapp.intouch.Utills;

import android.content.BroadcastReceiver;
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
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberRecyclerViewAdapter  extends RecyclerView.Adapter<MemberRecyclerViewAdapter.MyOwnHolder> {

    //variable
    List<String> id;

    Context mContext;

    String type,member;

    FirebaseFirestore mDb;

    List<String> friendList = new ArrayList<>();


    private List<String> freindLists;
    private List<String> memberList;
    private List<String> relativeList;

    boolean checkFriend;

    private static final String TAG = "MemberRecyclerViewAdapt";


    public MemberRecyclerViewAdapter(List<String> memberList,List<String> friendList,List<String> relativeList, List<String> id, Context cnt,String type,String member){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");
        this.memberList = memberList;
        this.relativeList = relativeList;
        this.freindLists = friendList;
        this.id = id;
        this.type = type;
        mContext = cnt;
        this.member = member;
        mDb = FirebaseFirestore.getInstance();
        checkFriend = false;

    }

    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_housemembers,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {
        final boolean[] publicCheck = {true};
        if(type.equals(mContext.getString(R.string.type_member))){
            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
        }
        if(type.equals(mContext.getString(R.string.type_relative))){
            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));

        }
        if(type.equals(mContext.getString(R.string.type_other))){
            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_black_ellipse));

        }
        if(type.equals(mContext.getString(R.string.type_hood))){
            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_yellow_ellipse));

        }

        try {
            mDb.collection(mContext.getString(R.string.collection_users)).document(id.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                    assert user != null;
                    holder.mMemberName.setText(user.getName());

                    if(type.equals(mContext.getString(R.string.type_friend)))
                        UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);

                    if(type.equals(mContext.getString(R.string.type_relative)))
                        UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);

                    if(type.equals(mContext.getString(R.string.type_member)))
                        UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);

                    if(type.equals(mContext.getString(R.string.type_other)) || type.equals(mContext.getString(R.string.type_hood)))

                    if(memberList != null){
                        if(memberList.contains(id.get(position))){
                            publicCheck[0] = false;
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_green_ellipse));
                            UniversalImageLoader.setImage(user.getFamily_avatar(), holder.mMemberAvatar, null, "",mContext);
                        }
                    }
                    if(relativeList != null){
                        if(relativeList.contains(id.get(position))){
                            publicCheck[0] = false;
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_red_ellipse));
                            UniversalImageLoader.setImage(user.getRel_avatar(), holder.mMemberAvatar, null, "",mContext);
                        }
                    }
                    if(friendList != null){
                        if(friendList.contains(id.get(position))){
                            publicCheck[0] = false;
                            holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                            UniversalImageLoader.setImage(user.getFriend_avatar(), holder.mMemberAvatar, null, "",mContext);
                        }
                    }
                    if(publicCheck[0]){
                        UniversalImageLoader.setImage(user.getPublic_avatar(), holder.mMemberAvatar, null, "",mContext);
                    }




                    if(type.equals(mContext.getString(R.string.type_friend))){
                        mDb.collection(mContext.getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    try {
                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                        for (int i = 0; i < list.size(); i++){
                                            Log.d(TAG, "onComplete: into the friend loop");
                                            Friend friend = list.get(i).toObject(Friend.class);
                                            assert friend != null;
                                            List<String> member = friend.getF_id();
                                            for (int j = 0; j < member.size() ; j++){
                                                if(member.get(j).equals(id.get(position))){
                                                    Log.d(TAG, "msthced" + member.get(j) + id.get(position));
                                                    friendList.add(member.get(j));
                                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_blue_ellipse));
                                                    break;
                                                }
                                                if(j == member.size()-1){
                                                    holder.mEllipse.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_black_ellipse));
                                                }
                                            }
                                            if(i == list.size()-1){
                                                checkFriend = true;
                                            }
                                        }
                                    }catch (NullPointerException e){
                                        Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                    }
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

    @Override
    public int getItemCount() {
        return id.size();
    }
    public void intentMethod(final int position) {

        if(id.get(position).equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            Intent intent = new Intent(mContext, AccountActivity.class);
            mContext.startActivity(intent);
        }
        else{
            if(type.equals(mContext.getString(R.string.type_friend))){
                if(checkFriend) {
                    if (friendList != null && friendList.size() != 0) {

                        Log.d(TAG, "intentMethod: friendlist not null " + id.get(position));
                        for (int i = 0; i < friendList.size(); i++) {
                            if (friendList.get(i).equals(id.get(position))) {
                                Log.d(TAG, "intentMethod: friendlist matched " + id.get(position));
                                Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                intent.putExtra(mContext.getString(R.string.intent_type), mContext.getString(R.string.type_friend));
                                intent.putExtra(mContext.getString(R.string.intent_memberid), id.get(position));
                                mContext.startActivity(intent);
                                break;

                            }
                            if (i == friendList.size() - 1) {
                                Log.d(TAG, "intentMethod: friendlist size - 1 " + id.get(position));
                                Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                intent.putExtra(mContext.getString(R.string.intent_type), mContext.getString(R.string.type_other));
                                intent.putExtra(mContext.getString(R.string.intent_memberid), id.get(position));
                                mContext.startActivity(intent);
                            }
                        }
                    } else {
                        Log.d(TAG, "intentMethod: friendlist  null " + id.get(position));
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_type), mContext.getString(R.string.type_other));
                        intent.putExtra(mContext.getString(R.string.intent_memberid), id.get(position));
                        mContext.startActivity(intent);

                }
            }
        }else if(type.equals(mContext.getString(R.string.type_hood))){
                Log.d(TAG, "intentMethod: into type hood");
                boolean publicCheck = true;
                if(memberList != null){
                    if(memberList.contains(id.get(position))){
                        Log.d(TAG, "intentMethod: into type hood0 member");
                        publicCheck = false;
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_member));
                        intent.putExtra(mContext.getString(R.string.intent_memberid),id.get(position));
                        mContext.startActivity(intent);
                    }
                }
                if(relativeList != null){
                    if(relativeList.contains(id.get(position))){
                        Log.d(TAG, "intentMethod: into type hood0 rel");
                        publicCheck = false;
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_relative));
                        intent.putExtra(mContext.getString(R.string.intent_memberid),id.get(position));
                        mContext.startActivity(intent);
                    }
                }
                if(friendList != null){
                    if(friendList.contains(id.get(position))){
                        Log.d(TAG, "intentMethod: into type hood0 fri");
                        publicCheck = false;
                        Intent intent = new Intent(mContext, MemberInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_friend));
                        intent.putExtra(mContext.getString(R.string.intent_memberid),id.get(position));
                        mContext.startActivity(intent);
                    }
                }
                if(publicCheck){
                    Log.d(TAG, "intentMethod: into type hood0 pub");
                    Intent intent = new Intent(mContext, MemberInfoActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_type),mContext.getString(R.string.type_other));
                    intent.putExtra(mContext.getString(R.string.intent_memberid),id.get(position));
                    mContext.startActivity(intent);
                }
            }
            else{
                Log.d(TAG, "intentMethod: into type hood0 othrt");
                Intent intent = new Intent(mContext, MemberInfoActivity.class);
                intent.putExtra(mContext.getString(R.string.intent_type),type);
                intent.putExtra(mContext.getString(R.string.intent_memberid),id.get(position));
                mContext.startActivity(intent);
            }
        }


    }

    public class MyOwnHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widget
        TextView mMemberName;
        CircleImageView mMemberAvatar;
        CircleImageView mEllipse;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mMemberAvatar = (CircleImageView) itemView.findViewById(R.id.iv_member_avatar);
            mMemberName = (TextView) itemView.findViewById(R.id.tv_member_name);
            mEllipse = (CircleImageView) itemView.findViewById(R.id.iv_ellipse);

            if(mContext.getString(R.string.intent_member).equals(member)){
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            intentMethod(getAdapterPosition());
        }
    }
}
