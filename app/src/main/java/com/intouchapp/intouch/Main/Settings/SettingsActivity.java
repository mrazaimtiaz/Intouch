package com.intouchapp.intouch.Main.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Models.Comment;
import com.intouchapp.intouch.Models.Compliment;
import com.intouchapp.intouch.Models.Event;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Notification;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.Login.LoginActivity;
import com.intouchapp.intouch.Utills.UserClient;

import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    //variable
    private User user;

    private Context mContext;

    private TextView mUsername,mAvatar,mLogout,mHouseInfo,mHoodInfo,mDelete;

    private ImageView mBack;

    FirebaseAuth auth;

    FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUsername = (TextView) findViewById(R.id.tv_username);
        mAvatar = (TextView) findViewById(R.id.tv_avatar);
        mLogout = (TextView) findViewById(R.id.tv_logout);
        mDelete = (TextView) findViewById(R.id.tv_delete);
        mHouseInfo = (TextView) findViewById(R.id.tv_house_info);
        mHoodInfo = (TextView) findViewById(R.id.tv_hood_info);

        mBack = (ImageView) findViewById(R.id.iv_back);

        mContext = SettingsActivity.this;
        user =  ((UserClient) (mContext.getApplicationContext())).getUser();

        mUsername.setText(user.getName());
        auth = FirebaseAuth.getInstance();

        mDb = FirebaseFirestore.getInstance();

        intentMethod();
    }

    private void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,EditAccountActivity.class);
                startActivity(intent);
            }
        });

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,AvatarActivity.class);
                startActivity(intent);
            }
        });

        mHouseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ChangeHouseInfoActivity.class);
                startActivity(intent);
            }
        });

        mHoodInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ChangeHoodInfoActivity.class);
                startActivity(intent);
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.logout))
                        .setMessage(mContext.getString(R.string.dialog_logout))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .show();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.delete_account))
                        .setMessage(mContext.getString(R.string.dialog_delete))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {

                                try {
                                    Objects.requireNonNull(auth.getCurrentUser()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                try {
                                                    mDb.collection(getString(R.string.collection_users)).document(user.getU_id()).delete();
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                House house = Objects.requireNonNull(task.getResult()).toObject(House.class);

                                                                assert house != null;
                                                                if(house.getMembers() != null){
                                                                    if(house.getMembers().size() == 1){
                                                                        mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).delete();


                                                                        mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.r_id),user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                for(int i = 0; i < requestList.size(); i++){

                                                                                    if(task.isSuccessful()){
                                                                                        mDb.collection(getString(R.string.collection_requests)).document(requestList.get(i).getId()).delete();

                                                                                    }
                                                                                }

                                                                            }
                                                                        });



                                                                    }else{
                                                                        house.getMembers().remove(user.getU_id());
                                                                        mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).set(house);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }



                                                try {
                                                    mDb.collection(getString(R.string.collection_notification)).whereEqualTo(getString(R.string.r_id),user.getU_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            List<DocumentSnapshot> notificationList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                            for(int i = 0; i < notificationList.size(); i++){

                                                                if(task.isSuccessful()){
                                                                    mDb.collection(getString(R.string.collection_notification)).document(notificationList.get(i).getId()).delete();

                                                                }
                                                            }

                                                        }
                                                    });
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    mDb.collection(getString(R.string.collection_notification)).whereEqualTo(getString(R.string.s_id),user.getU_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){

                                                                List<DocumentSnapshot> notificationList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < notificationList.size(); i++){
                                                                    mDb.collection(getString(R.string.collection_notification)).document(notificationList.get(i).getId()).delete();

                                                                }

                                                            }

                                                        }
                                                    });
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    mDb.collection(getString(R.string.collection_post)).whereEqualTo(getString(R.string.u_id),user.getU_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                List<DocumentSnapshot> postList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < postList.size(); i++){
                                                                    mDb.collection(getString(R.string.collection_post)).document(postList.get(i).getId()).delete();
                                                                }
                                                            }


                                                        }
                                                    });

                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    mDb.collection(getString(R.string.collection_post)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                final List<DocumentSnapshot> postList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < postList.size(); i++){
                                                                    mDb.collection(getString(R.string.collection_post)).document(postList.get(i).getId()).collection(getString(R.string.collection_comment)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if(task.isSuccessful()){
                                                                                List<DocumentSnapshot> commentList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                for(int i = 0; i < commentList.size(); i++){

                                                                                    Comment comment = commentList.get(i).toObject(Comment.class);

                                                                                    assert comment != null;
                                                                                    if(comment.getU_id() != null){
                                                                                        if(comment.getU_id().equals(user.getU_id())){
                                                                                            mDb.collection(getString(R.string.collection_post)).document(postList.get(i).getId()).collection(getString(R.string.collection_comment)).document(commentList.get(i).getId()).delete();
                                                                                        }
                                                                                    }


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


                                                try {
                                                    mDb.collection(getString(R.string.collection_post)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                final List<DocumentSnapshot> postList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < postList.size(); i++){

                                                                    Post post = postList.get(i).toObject(Post.class);

                                                                    assert post != null;
                                                                    if(post.getLike() != null){
                                                                        if(post.getLike().contains(user.getU_id())){
                                                                            post.getLike().remove(user.getU_id());

                                                                            mDb.collection(getString(R.string.collection_post)).document(postList.get(i).getId()).set(post);
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    });

                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    mDb.collection(getString(R.string.collection_users)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                final List<DocumentSnapshot> userList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < userList.size(); i++){

                                                                    mDb.collection(getString(R.string.collection_users)).document(userList.get(i).getId()).collection(getString(R.string.collection_relatives)).whereEqualTo(getString(R.string.h_id),user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if(task.isSuccessful()){

                                                                                List<DocumentSnapshot> relativelist = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                for(int i = 0; i < relativelist.size(); i++){

                                                                                    Relative relative = relativelist.get(i).toObject(Relative.class);

                                                                                    assert relative != null;
                                                                                    if(relative.getR_id() != null){
                                                                                        if(relative.getR_id().size() == 1){
                                                                                            mDb.collection(getString(R.string.collection_users)).document(userList.get(i).getId()).collection(getString(R.string.collection_relatives)).document(relativelist.get(i).getId()).delete();
                                                                                        }else{
                                                                                            relative.getR_id().remove(user.getU_id());
                                                                                            mDb.collection(getString(R.string.collection_users)).document(userList.get(i).getId()).collection(getString(R.string.collection_relatives)).document(relativelist.get(i).getId()).set(relative);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });

                                                                    mDb.collection(getString(R.string.collection_users)).document(userList.get(i).getId()).collection(getString(R.string.collection_friends)).whereEqualTo(getString(R.string.h_id),user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if(task.isSuccessful()){

                                                                                List<DocumentSnapshot> friendList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                for(int i = 0; i < friendList.size(); i++){

                                                                                    Friend friend = friendList.get(i).toObject(Friend.class);

                                                                                    assert friend != null;
                                                                                    if(friend.getF_id() != null){
                                                                                        if(friend.getF_id().size() == 1){
                                                                                            mDb.collection(getString(R.string.collection_users)).document(userList.get(i).getId()).collection(getString(R.string.collection_friends)).document(friendList.get(i).getId()).delete();
                                                                                        }else{
                                                                                            friend.getF_id().remove(user.getU_id());
                                                                                            mDb.collection(getString(R.string.collection_users)).document(userList.get(i).getId()).collection(getString(R.string.collection_friends)).document(friendList.get(i).getId()).set(friend);
                                                                                        }
                                                                                    }
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


                                                try {
                                                    mDb.collection(getString(R.string.collection_events)).whereEqualTo(getString(R.string.u_id),user.getU_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                List<DocumentSnapshot> eventList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < eventList.size(); i++){
                                                                    mDb.collection(getString(R.string.collection_post)).document(eventList.get(i).getId()).delete();
                                                                }
                                                            }


                                                        }
                                                    });

                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    mDb.collection(getString(R.string.collection_events)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                final List<DocumentSnapshot> eventList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < eventList.size(); i++){

                                                                    Event event = eventList.get(i).toObject(Event.class);

                                                                    assert event != null;
                                                                    if(event.getJoined() != null){
                                                                        if(event.getJoined().contains(user.getU_id())){
                                                                            event.getJoined().remove(user.getU_id());

                                                                            mDb.collection(getString(R.string.collection_post)).document(eventList.get(i).getId()).set(event);
                                                                        }
                                                                    }
                                                                    if(event.getRejected() != null){
                                                                        if(event.getRejected().contains(user.getU_id())){
                                                                            event.getRejected().remove(user.getU_id());

                                                                            mDb.collection(getString(R.string.collection_post)).document(eventList.get(i).getId()).set(event);
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    });

                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    mDb.collection(getString(R.string.collection_compliment)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){
                                                                List<DocumentSnapshot> compliementList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                for(int i = 0; i < compliementList.size(); i++){

                                                                    Compliment compliment = compliementList.get(i).toObject(Compliment.class);

                                                                    assert compliment != null;
                                                                    if(compliment.getU_id() != null){
                                                                        if(compliment.getU_id().contains(user.getU_id())){
                                                                            compliment.getU_id().remove(user.getU_id());
                                                                            mDb.collection(getString(R.string.collection_compliment)).document(compliementList.get(i).getId()).set(compliment);
                                                                        }
                                                                    }
                                                                }
                                                            }


                                                        }
                                                    });

                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.s_id),user.getU_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                            for(int i = 0; i < requestList.size(); i++){

                                                                if(task.isSuccessful()){
                                                                    mDb.collection(getString(R.string.collection_requests)).document(requestList.get(i).getId()).delete();

                                                                }
                                                            }

                                                        }
                                                    });
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    mDb.collection(getString(R.string.collection_requests)).whereEqualTo(getString(R.string.r_id),user.getU_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            List<DocumentSnapshot> requestList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                            for(int i = 0; i < requestList.size(); i++){

                                                                if(task.isSuccessful()){
                                                                    mDb.collection(getString(R.string.collection_requests)).document(requestList.get(i).getId()).delete();

                                                                }
                                                            }

                                                        }
                                                    });
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }
                                                dialog.dismiss();
                                                auth.signOut();
                                                Intent intent = new Intent(mContext, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();

                                            }

                                        }
                                    });

                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }





                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .show();
            }
        });
    }
}
