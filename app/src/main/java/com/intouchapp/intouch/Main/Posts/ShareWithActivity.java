package com.intouchapp.intouch.Main.Posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.SplashActivity;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.FilePaths;
import com.intouchapp.intouch.Utills.ShareWithRecyclerAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShareWithActivity extends AppCompatActivity {

    //widget
    private RecyclerView mRecyclerView;
    private TextView mShare;
    private ImageView mBack,clickedPublic,clickedHood,clickedKnower,clickedRelative,ClickedFriend,clickedFamily;
    private ConstraintLayout mNothing,mMain,mProgress;
    
    //variable
    private static final String TAG = "ShareWithActivity";

    //Firebase
    FirebaseFirestore mDb;
    private StorageReference mStorageRef;

    User user;



    private Context mContext;
    private String image,description;
    private String type;
    ArrayList<String> userInputs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_with);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mContext = ShareWithActivity.this;

        clickedPublic = (ImageView) findViewById(R.id.iv_clicked_public);
        clickedHood = (ImageView) findViewById(R.id.iv_clicked_hood);
        clickedKnower = (ImageView) findViewById(R.id.iv_clicked_knowers);
        clickedRelative = (ImageView) findViewById(R.id.iv_clicked_relative);
        ClickedFriend = (ImageView) findViewById(R.id.iv_clicked_friend);
        clickedFamily = (ImageView) findViewById(R.id.iv_clicked_family);
        mShare = (TextView) findViewById(R.id.tv_share);

        mNothing = (ConstraintLayout) findViewById(R.id.constraintLayoutNothing);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);
        mBack = (ImageView) findViewById(R.id.iv_back);

        user = ((UserClient) (mContext.getApplicationContext())).getUser();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        AndroidThreeTen.init(this);

        mShare.setAlpha(0.5f);
        type = getString(R.string.empty);

        description= getString(R.string.empty);
        image = getString(R.string.empty);
            userInputs = getIntent().getStringArrayListExtra(getString(R.string.intent_photo));
            if(userInputs != null){
                description = userInputs.get(0);
                image = userInputs.get(1);
            }

        Log.d(TAG, "onCreate: " + description + image);
        knowerItem();
        clickShare();
        intentMethod();

    }
    //********************************************* intent method *********************************************
    private void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //****************************************** click share ***************************************************
    public void clickShare(){
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "onClick: " + type + " " + description + " " + image);
                    if(!type.equals(getString(R.string.empty)) && !image.equals(getString(R.string.empty))){
                        mShare.setVisibility(View.GONE);
                        mMain.setVisibility(View.GONE);
                        mProgress.setVisibility(View.VISIBLE);
                        final FilePaths filePaths = new FilePaths();
                        image = "file://" + image;
                        Uri FilePathUri = Uri.parse(new File(image).toString());

                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                        } catch (IOException e) {
                            Log.d(TAG, "onClick: IOException " + e.getMessage());
                            e.printStackTrace();

                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        assert bmp != null;
                        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] data = baos.toByteArray();

                        final String p_id = mDb.collection(getString(R.string.collection_hoods)).document().getId();

                        mStorageRef.child(filePaths.FIREBASE_POST_IMAGE_STORAGE + "/" + p_id + "/post_image").putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                mStorageRef.child(filePaths.FIREBASE_POST_IMAGE_STORAGE + "/" + p_id + "/post_image").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            ZoneId tz =  ZoneId.systemDefault();
                                            LocalDateTime localDateTime = LocalDateTime.now();
                                            long seconds = localDateTime.atZone(tz).toEpochSecond();
                                            int nanos = localDateTime.getNano();
                                            Timestamp timestamp = new Timestamp(seconds, nanos);

                                            Post post = new Post();
                                            post.setU_id(FirebaseAuth.getInstance().getUid());
                                            post.setDescription(description);
                                            post.setImage(Objects.requireNonNull(task.getResult()).toString());
                                            post.setType(type);
                                            post.setShareWith(getString(R.string.empty));
                                            post.setP_id(p_id);
                                            post.setTimestamp(timestamp);
                                            mDb.collection(getString(R.string.collection_post)).document(p_id).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(mContext, MainActivity.class);
                                                        Log.d(TAG, "onComplete: saved");
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                        mMain.setVisibility(View.VISIBLE);
                                                        mProgress.setVisibility(View.GONE);
                                                        mShare.setVisibility(View.VISIBLE);
                                                        Toast.makeText(ShareWithActivity.this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else{
                                            mShare.setVisibility(View.VISIBLE);
                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                            Toast.makeText(ShareWithActivity.this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                        });

                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });
    }
    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<String> member,String type){
        mRecyclerView.setAdapter(new ShareWithRecyclerAdapter(member,type, mContext));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
    }

    public void family(View view) {

        if (clickedFamily.getBackground() == null) {
            type = getString(R.string.type_member);
            mShare.setAlpha(1f);
            mShare.setEnabled(true);
            mRecyclerView.setVisibility(View.VISIBLE);
            clickedFamily.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lightgreen_bg));
            clickedHood.setBackground(null);
            clickedPublic.setBackground(null);
            clickedKnower.setBackground(null);
            clickedRelative.setBackground(null);
            ClickedFriend.setBackground(null);
        }else{
            type = getString(R.string.empty);
            mShare.setAlpha(0.5f);
            mShare.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
            clickedFamily.setBackground(null);
        }
        try {
            mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                        assert house != null;
                        if(house.getMembers() != null && house.getMembers().size() != 0){
                            mNothing.setVisibility(View.GONE);
                            settingRecyclerView(house.getMembers(),getString(R.string.type_member));
                        }else{
                            mNothing.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void knower(View view) {
        knowerItem();

    }
    public void knowerItem(){
        final List<String> members = new ArrayList<>();
        if (clickedKnower.getBackground() == null) {
            type = getString(R.string.type_knower);
            mShare.setAlpha(1f);
            mShare.setEnabled(true);
            mRecyclerView.setVisibility(View.VISIBLE);
            clickedKnower.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lightgreen_bg_max));
            clickedHood.setBackground(null);
            clickedFamily.setBackground(null);
            clickedPublic.setBackground(null);
            clickedRelative.setBackground(null);
            ClickedFriend.setBackground(null);
        }else{
            type = getString(R.string.empty);
            mShare.setAlpha(0.5f);
            mShare.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
            clickedKnower.setBackground(null);
        }
        if(user != null){
            try {
                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                            assert house != null;
                            if (house.getMembers() != null && house.getMembers().size() != 0) {
                                mNothing.setVisibility(View.GONE);
                                members.addAll(house.getMembers());
                            } else {
                                mNothing.setVisibility(View.VISIBLE);
                            }
                            mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                        for(int i = 0; i < list.size();i++){
                                            Relative relative = list.get(i).toObject(Relative.class);
                                            assert relative != null;
                                            relative.getR_id();
                                            if (relative.getR_id() != null && relative.getR_id().size() != 0)
                                                members.addAll(relative.getR_id());
                                        }
                                    }
                                }
                            });
                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                                        for(int i = 0; i < list.size();i++){
                                            Friend friend = list.get(i).toObject(Friend.class);
                                            assert friend != null;
                                            friend.getF_id();
                                            if (friend.getF_id() != null && friend.getF_id().size() != 0)
                                                members.addAll(friend.getF_id());
                                            if(i == list.size() -1){
                                                if(members != null && members.size() != 0){
                                                    mNothing.setVisibility(View.GONE);
                                                    settingRecyclerView(members,getString(R.string.type_knower));
                                                }else{
                                                    mNothing.setVisibility(View.VISIBLE);
                                                }
                                            }

                                        }
                                    }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                        mNothing.setVisibility(View.GONE);
                                        settingRecyclerView(members,getString(R.string.type_knower));
                                    }
                                }
                            });
                        }

                    }

                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }else{
            Intent intent = new Intent(mContext, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }


    public void relative(View view) {
        final List<String> members = new ArrayList<>();
        if (clickedRelative.getBackground() == null) {
            type = getString(R.string.type_relative);
            mShare.setAlpha(1f);
            mShare.setEnabled(true);
            mRecyclerView.setVisibility(View.VISIBLE);
            clickedRelative.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lightgreen_bg));
            clickedHood.setBackground(null);
            clickedFamily.setBackground(null);
            clickedKnower.setBackground(null);
            clickedPublic.setBackground(null);
            ClickedFriend.setBackground(null);
        }else{
            type = getString(R.string.empty);
            mShare.setAlpha(0.5f);
            mShare.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
            clickedRelative.setBackground(null);
        }
        try {
            mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(int i = 0; i < list.size();i++){
                            Relative relative = list.get(i).toObject(Relative.class);
                            assert relative != null;
                            relative.getR_id();
                            if (relative.getR_id() != null && relative.getR_id().size() != 0)
                                members.addAll(relative.getR_id());
                            if(i == list.size() -1){
                                if(members.size() != 0){
                                    mNothing.setVisibility(View.GONE);
                                    settingRecyclerView(members,getString(R.string.type_relative));
                                }else{
                                    mNothing.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }

    public void friend(View view) {
        final List<String> members = new ArrayList<>();
        if (ClickedFriend.getBackground() == null) {
            type = getString(R.string.type_friend);
            mShare.setAlpha(1f);
            mShare.setEnabled(true);
            mRecyclerView.setVisibility(View.VISIBLE);
            ClickedFriend.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lightgreen_bg));
            clickedHood.setBackground(null);
            clickedFamily.setBackground(null);
            clickedKnower.setBackground(null);
            clickedRelative.setBackground(null);
            clickedPublic.setBackground(null);
        }else{
            type = getString(R.string.empty);
            mShare.setAlpha(0.5f);
            mShare.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
            ClickedFriend.setBackground(null);
        }
        try {
            mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(int i = 0; i < list.size();i++){
                            Friend friend = list.get(i).toObject(Friend.class);
                            assert friend != null;
                            friend.getF_id();
                            if (friend.getF_id() != null && friend.getF_id().size() != 0)
                                members.addAll(friend.getF_id());
                            if(i == list.size() -1){
                                if(members.size() != 0){
                                    mNothing.setVisibility(View.GONE);
                                    settingRecyclerView(members,getString(R.string.type_friend));
                                }else{
                                    mNothing.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void hoodClicked(View view) {
        final List<String> members = new ArrayList<>();
        Log.d(TAG, "hood: onclick");
        if (clickedHood.getBackground() == null) {
            type = getString(R.string.type_hood);
            mShare.setAlpha(1f);
            mShare.setEnabled(true);
            mRecyclerView.setVisibility(View.VISIBLE);
            clickedHood.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lightgreen_bg_max1));
            clickedPublic.setBackground(null);
            clickedFamily.setBackground(null);
            clickedKnower.setBackground(null);
            clickedRelative.setBackground(null);
            ClickedFriend.setBackground(null);
        }else{
            type = getString(R.string.empty);
            mShare.setAlpha(0.5f);
            mShare.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
            clickedHood.setBackground(null);
        }
        try {
            mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                    for(int i = 0;i < list.size(); i++){
                        House house = list.get(i).toObject(House.class);
                        assert house != null;
                        Log.d(TAG, "onComplete: house member" + house.getMembers());
                        if (house.getMembers() != null && house.getMembers().size() != 0)
                            members.addAll(house.getMembers());

                        if(i == list.size() -1){
                            settingRecyclerView(members,getString(R.string.type_hood));
                        }
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void clickPublic(View view) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        Log.d(TAG, "clickPublic: public");
        if (clickedPublic.getBackground() == null) {
            type = getString(R.string.type_other);
            mShare.setAlpha(1f);
            mShare.setEnabled(true);
            clickedPublic.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lightgreen_bg_max));
            clickedHood.setBackground(null);
            clickedFamily.setBackground(null);
            clickedKnower.setBackground(null);
            clickedRelative.setBackground(null);
            ClickedFriend.setBackground(null);

        }else{
            type = getString(R.string.empty);
            mShare.setAlpha(0.5f);
            mShare.setEnabled(false);
            clickedPublic.setBackground(null);
        }
    }
}
