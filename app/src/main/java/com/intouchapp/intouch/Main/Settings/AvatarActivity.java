package com.intouchapp.intouch.Main.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Signup.GalleryActivity;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.FilePaths;
import com.intouchapp.intouch.Utills.UniversalImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AvatarActivity extends AppCompatActivity {

    //widget
    ConstraintLayout mOne,mFour,mProgress,mMain;
    ImageView mDoneOne, mDoneFour;
    TextView mUseOneAvatar,mUseFourAvatar;
    TextView uploadAvatar;

    ImageView mPublicOne,mBack;

    ImageView mPublic,mFamily,mRelative,mFriend;

    //variable
    private User user;
    private Context mContext;

    private static final String TAG = "AvatarActivity";

    private String publicOne,publec,relative,family,friend;

    private String mAppend = "file://";
    private String activityName;

    //temp
    private  int count = 1;
    private int intentStatus = 0;

    ArrayList<String> userInputs = new ArrayList<String>();
    ArrayList<String> fetchList = new ArrayList<String>();

    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        mContext = AvatarActivity.this;

        mOne = (ConstraintLayout) findViewById(R.id.constraintLayoutOneAvatar);
        mFour = (ConstraintLayout) findViewById(R.id.constraintLayoutFourAvatar);

        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);

        mDoneOne = (ImageView) findViewById(R.id.iv_doneoneavatar);
        mDoneFour = (ImageView) findViewById(R.id.iv_donethreeavatar);

        mUseOneAvatar = (TextView) findViewById(R.id.tv_useoneavatar);
        mUseFourAvatar = (TextView) findViewById(R.id.tv_use_three_avatar);
        uploadAvatar = (TextView) findViewById(R.id.uploadAvatar);

        mPublicOne = (ImageView) findViewById(R.id.iv_publicOne);

        mPublic = (ImageView) findViewById(R.id.iv_public);
        mFamily = (ImageView) findViewById(R.id.iv_family);
        mRelative = (ImageView) findViewById(R.id.iv_relative);
        mFriend = (ImageView) findViewById(R.id.iv_friend);

        mBack = (ImageView) findViewById(R.id.iv_back);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        user =  ((UserClient) (mContext.getApplicationContext())).getUser();
        activityName = getString(R.string.AvatarActivity_class);

        if(user != null){
            if (user.getRel_avatar().equals(user.getFamily_avatar()) && user.getRel_avatar().equals(user.getPublic_avatar()) && user.getRel_avatar().equals(user.getFriend_avatar())){
                mDoneOne.setVisibility(View.VISIBLE);
                mDoneFour.setVisibility(View.GONE);

                mOne.setVisibility(View.VISIBLE);
                mFour.setVisibility(View.GONE);
            }else{
                mDoneOne.setVisibility(View.GONE);
                mDoneFour.setVisibility(View.VISIBLE);

                mOne.setVisibility(View.GONE);
                mFour.setVisibility(View.VISIBLE);
            }
            publicOne = user.getPublic_avatar();
            publec = user.getPublic_avatar();
            family = user.getFamily_avatar();
            relative = user.getRel_avatar();
            friend = user.getFriend_avatar();
            if(getIntent().getBooleanExtra(getString(R.string.intent_no_request),true)){
                UniversalImageLoader.setImage(user.getPublic_avatar(), mPublicOne, null, "",mContext);

                UniversalImageLoader.setImage(user.getPublic_avatar(), mPublic, null, "",mContext);
                UniversalImageLoader.setImage(user.getFamily_avatar(), mFamily, null, "",mContext);
                UniversalImageLoader.setImage(user.getRel_avatar(), mRelative, null, "",mContext);
                UniversalImageLoader.setImage(user.getFriend_avatar(), mFriend, null, "",mContext);


            }
        }else{
            publicOne = mContext.getString(R.string.empty);
            publec = mContext.getString(R.string.empty);
            family = mContext.getString(R.string.empty);
            relative = mContext.getString(R.string.empty);
            friend = mContext.getString(R.string.empty);
        }
        image();
        uploadAvatar.setEnabled(false);
        uploadAvatar.setAlpha(0.5f);
        setImage();

        intentMethod();
        changeAvatar();


    }
    public void changeAvatar(){

        uploadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMain.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                if(mDoneOne.getVisibility() == View.VISIBLE){
                    if(!publicOne.equals(user.getPublic_avatar())){
                        uploadImageFileToFirebaseStorage(publicOne);
                    }
                }
                if(mDoneFour.getVisibility() == View.VISIBLE){
                    if(!publec.equals(user.getPublic_avatar())){
                        uploadImageFileToPublicFirebaseStorage(publec);
                    }
                    if(!family.equals(user.getFamily_avatar())){
                        uploadImageFileToFamilyFirebaseStorage(family);
                    }
                    if(!relative.equals(user.getRel_avatar())){
                        uploadImageFileToRelativeFirebaseStorage(relative);
                    }
                    if(!friend.equals(user.getFriend_avatar())){
                        uploadImageFileToFriendFirebaseStorage(friend);
                    }
                }
            }
        });

    }
    //************************************************ intent method *************************************************************
    private void intentMethod(){
        mUseOneAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAvatar.setEnabled(true);
                uploadAvatar.setAlpha(1f);
                mDoneOne.setVisibility(View.VISIBLE);
                mDoneFour.setVisibility(View.GONE);

                mOne.setVisibility(View.VISIBLE);
                mFour.setVisibility(View.GONE);
            }
        });
        mUseFourAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAvatar.setEnabled(true);
                uploadAvatar.setAlpha(1f);
                mDoneOne.setVisibility(View.GONE);
                mDoneFour.setVisibility(View.VISIBLE);
                mOne.setVisibility(View.GONE);
                mFour.setVisibility(View.VISIBLE);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //********************************************************** profile image *************************************************
    public void image() {

        mPublicOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 1;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(publicOne);
                    userInputs.add(publec);
                    userInputs.add(family);
                    userInputs.add(relative);
                    userInputs.add(friend);
                    userInputs.add(String.valueOf(intentStatus));
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    i.putExtra(getString(R.string.intent_no_request),false);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });

        mPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 2;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(publicOne);
                    userInputs.add(publec);
                    userInputs.add(family);
                    userInputs.add(relative);
                    userInputs.add(friend);
                    userInputs.add(String.valueOf(intentStatus));
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    i.putExtra(getString(R.string.intent_no_request),false);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });
        mFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 3;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(publicOne);
                    userInputs.add(publec);
                    userInputs.add(family);
                    userInputs.add(relative);
                    userInputs.add(friend);
                    userInputs.add(String.valueOf(intentStatus));
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    i.putExtra(getString(R.string.intent_no_request),false);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });

        mRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 4;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(publicOne);
                    userInputs.add(publec);
                    userInputs.add(family);
                    userInputs.add(relative);
                    userInputs.add(friend);
                    userInputs.add(String.valueOf(intentStatus));
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    i.putExtra(getString(R.string.intent_no_request),false);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });

        mFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 5;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(publicOne);
                    userInputs.add(publec);
                    userInputs.add(family);
                    userInputs.add(relative);
                    userInputs.add(friend);
                    userInputs.add(String.valueOf(intentStatus));
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    i.putExtra(getString(R.string.intent_no_request),false);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });

    }
    //*********************************************** setimage ***********************************************************
    private void setImage() {
        String image;
        Log.d(TAG, "setImage: intent staus" + intentStatus);
        intent = getIntent();
        Log.d(TAG, "setImage: " + intent);
        if (intent.hasExtra(getString(R.string.user_inputs))) {


            fetchList = getIntent().getStringArrayListExtra(getString(R.string.user_inputs));
            Log.d(TAG, "onClick: data settings" + fetchList.get(3));

            if (!fetchList.get(0).equals(publicOne)) {
                Log.d(TAG, "setImage: o");
                UniversalImageLoader.setImage(fetchList.get(0), mPublicOne, null, mAppend, mContext);
                publicOne = fetchList.get(0);
            }else{
                Log.d(TAG, "setImage: o else");
                UniversalImageLoader.setImage(publicOne, mPublicOne, null, "", mContext);
            }
            if (!fetchList.get(1).equals(publec)) {
                Log.d(TAG, "setImage: 1");
                UniversalImageLoader.setImage(fetchList.get(1), mPublic, null, mAppend, mContext);
                publec = fetchList.get(1);
            }else{
                Log.d(TAG, "setImage: 1 else");
                UniversalImageLoader.setImage(publec, mPublic, null, "", mContext);
            }
            if (!fetchList.get(2).equals(family)) {
                Log.d(TAG, "setImage: 2");
                UniversalImageLoader.setImage(fetchList.get(2), mFamily, null, mAppend, mContext);
                family = fetchList.get(2);
            }else{
                Log.d(TAG, "setImage: 2 else");
                UniversalImageLoader.setImage(family, mFamily, null, "", mContext);
            }
            if (!fetchList.get(3).equals(relative)) {
                Log.d(TAG, "setImage: 3");
                UniversalImageLoader.setImage(fetchList.get(3), mRelative, null, mAppend, mContext);
                relative = fetchList.get(3);

            }else{
                Log.d(TAG, "setImage: 3 else");
                UniversalImageLoader.setImage(relative, mRelative, null, "", mContext);
            }
            if (!fetchList.get(4).equals(friend)) {
                Log.d(TAG, "setImage: 4");
                UniversalImageLoader.setImage(fetchList.get(4), mFriend, null, mAppend, mContext);
                friend = fetchList.get(4);

            }else{
                Log.d(TAG, "setImage: 4 else");
                UniversalImageLoader.setImage(friend, mFriend, null, "", mContext);
            }
        }
            if (intent.hasExtra(getString(R.string.selected_image))) {
                uploadAvatar.setEnabled(true);
                uploadAvatar.setAlpha(1f);
                image = intent.getStringExtra(getString(R.string.selected_image));
                int status = Integer.parseInt(fetchList.get(5));
                if(Integer.parseInt(fetchList.get(5)) == 1){
                    mDoneOne.setVisibility(View.VISIBLE);
                    mDoneFour.setVisibility(View.GONE);

                    mOne.setVisibility(View.VISIBLE);
                    mFour.setVisibility(View.GONE);
                    Log.d(TAG, "setImage: inttent 1 " + fetchList.get(5));
                    UniversalImageLoader.setImage(image, mPublicOne, null, mAppend,mContext);
                    intentStatus = 1;
                    publicOne = image;
                }else if(Integer.parseInt(fetchList.get(5)) == 2){
                    mDoneOne.setVisibility(View.GONE);
                    mDoneFour.setVisibility(View.VISIBLE);

                    mOne.setVisibility(View.GONE);
                    mFour.setVisibility(View.VISIBLE);
                    Log.d(TAG, "setImage: inttent 2 " + fetchList.get(5));
                    intentStatus = 2;
                    UniversalImageLoader.setImage(image, mPublic, null, mAppend,mContext);
                    publec = image;

                }else if(Integer.parseInt(fetchList.get(5)) == 3){
                    mDoneOne.setVisibility(View.GONE);
                    mDoneFour.setVisibility(View.VISIBLE);

                    mOne.setVisibility(View.GONE);
                    mFour.setVisibility(View.VISIBLE);
                    Log.d(TAG, "setImage: inttent 3 " + fetchList.get(5));
                    intentStatus = 3;
                    UniversalImageLoader.setImage(image, mFamily, null, mAppend,mContext);
                    family = image;
                }else if(Integer.parseInt(fetchList.get(5)) == 4){
                    mDoneOne.setVisibility(View.GONE);
                    mDoneFour.setVisibility(View.VISIBLE);

                    mOne.setVisibility(View.GONE);
                    mFour.setVisibility(View.VISIBLE);
                    Log.d(TAG, "setImage: inttent 4 " + fetchList.get(5));
                    intentStatus = 4;
                    UniversalImageLoader.setImage(image, mRelative, null, mAppend,mContext);
                    relative = image;
                }else if(Integer.parseInt(fetchList.get(5)) == 5){
                    mDoneOne.setVisibility(View.GONE);
                    mDoneFour.setVisibility(View.VISIBLE);

                    mOne.setVisibility(View.GONE);
                    mFour.setVisibility(View.VISIBLE);
                    Log.d(TAG, "setImage: inttent 5" + fetchList.get(5));
                    intentStatus = 5;
                    UniversalImageLoader.setImage(image, mFriend, null, mAppend,mContext);
                    friend = image;
                }
            }


    }
    //********************************************************** isPermissionGranted for image ******************************************
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    //*********************************************************** onRequestPermissionsResult *******************************************
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean showRationale = shouldShowRequestPermissionRationale( permissions[0]);

        Log.d(TAG, "onRequestPermissionsResult: " + count);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: granted " + count);
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                 count = 0;

                userInputs.add(publicOne);
                userInputs.add(publec);
                userInputs.add(family);
                userInputs.add(relative);
                userInputs.add(friend);
                userInputs.add(String.valueOf(intentStatus));
                Intent i = new Intent(mContext, GalleryActivity.class);
                i.putExtra(getString(R.string.activity_name),activityName);
                i.putExtra(getString(R.string.intent_no_request),false);
                Log.d(TAG, "onClick: activity name" + activityName);
                i.putExtra(getString(R.string.user_inputs), userInputs);
                startActivity(i);



        } else if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "onRequestPermissionsResult: not granted " + count);
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            if (showRationale)
                count = 0;

        }
        if (! showRationale) {
            Log.d(TAG, "onRequestPermissionsResult: if " + count);
            if(count == 1){
                Log.d(TAG, "onRequestPermissionsResult: setting dialog");
                settingDialogBox();
            }
            count = 1;
        }

    }
    //******************************************** settingDialogBox ******************************************************
    private void settingDialogBox(){
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.permission_required))
                .setMessage("Storage permission required. Open Settings?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //******************************************** open settings ******************************************************
    private void openSettings(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    //***************************************************** update public avatar ******************************************
    public void uploadImageFileToFirebaseStorage(String avatar) {

        final FilePaths filePaths = new FilePaths();
        if (avatar != null) {
            avatar = "file://" + avatar;
            Uri FilePathUri = Uri.parse(new File(avatar).toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();


                // Creating second StorageReference.
                final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/public_image");

                // Adding addOnSuccessListener to second StorageReference.
                storageReferenceLocation.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                storageReferenceLocation.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {


                                            Map<String,Object> map = new HashMap<>();

                                            map.put(getString(R.string.field_public_avatar), Objects.requireNonNull(task.getResult()).toString());
                                            map.put(getString(R.string.field_rel_avatar),task.getResult().toString());
                                            map.put(getString(R.string.field_friend_avatar),task.getResult().toString());
                                            map.put(getString(R.string.field_family_avatar),task.getResult().toString());

                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Image saved");
                                                        finalIntent();
                                                    } else {
                                                        finalIntent();
                                                    }
                                                }
                                            });
                                        }else{
                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                            finalIntent();
                                        }
                                    }
                                });


                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mMain.setVisibility(View.VISIBLE);
                                mProgress.setVisibility(View.GONE);
                                finalIntent();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
                finalIntent();
            }

        }
    }

    private void finalIntent(){
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }
    //***************************************************** update public avatar ******************************************
    public void uploadImageFileToPublicFirebaseStorage(String avatar) {

        final FilePaths filePaths = new FilePaths();
        if (avatar != null) {
            avatar = "file://" + avatar;
            Uri FilePathUri = Uri.parse(new File(avatar).toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();


                // Creating second StorageReference.
                final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/public_image");

                // Adding addOnSuccessListener to second StorageReference.
                storageReferenceLocation.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                storageReferenceLocation.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {


                                            Map<String,Object> map = new HashMap<>();

                                            map.put(getString(R.string.field_public_avatar),task.getResult().toString());

                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Image saved");
                                                        finalIntent();
                                                    } else {
                                                        finalIntent();
                                                    }
                                                }
                                            });
                                        }else{
                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                            finalIntent();
                                        }
                                    }
                                });


                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mMain.setVisibility(View.VISIBLE);
                                mProgress.setVisibility(View.GONE);
                                finalIntent();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
                finalIntent();
            }

        }
    }

    //***************************************************** update public avatar ******************************************
    public void uploadImageFileToFamilyFirebaseStorage(String avatar) {

        final FilePaths filePaths = new FilePaths();
        if (avatar != null) {
            avatar = "file://" + avatar;
            Uri FilePathUri = Uri.parse(new File(avatar).toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();


                // Creating second StorageReference.
                final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/family_image");

                // Adding addOnSuccessListener to second StorageReference.
                storageReferenceLocation.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                storageReferenceLocation.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {


                                            Map<String,Object> map = new HashMap<>();

                                            map.put(getString(R.string.field_family_avatar),task.getResult().toString());

                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Image saved");
                                                        finalIntent();
                                                    } else {
                                                        finalIntent();
                                                    }
                                                }
                                            });
                                        }else{
                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                            finalIntent();
                                        }
                                    }
                                });


                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mMain.setVisibility(View.VISIBLE);
                                mProgress.setVisibility(View.GONE);
                                finalIntent();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
                finalIntent();
            }

        }
    }
    //***************************************************** update public avatar ******************************************
    public void uploadImageFileToRelativeFirebaseStorage(String avatar) {

        final FilePaths filePaths = new FilePaths();
        if (avatar != null) {
            avatar = "file://" + avatar;
            Uri FilePathUri = Uri.parse(new File(avatar).toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();


                // Creating second StorageReference.
                final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/relative_image");

                // Adding addOnSuccessListener to second StorageReference.
                storageReferenceLocation.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                storageReferenceLocation.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {


                                            Map<String,Object> map = new HashMap<>();

                                            map.put(getString(R.string.field_rel_avatar),task.getResult().toString());


                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Image saved");
                                                        finalIntent();
                                                    } else {
                                                        finalIntent();
                                                    }
                                                }
                                            });
                                        }else{
                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                            finalIntent();
                                        }
                                    }
                                });


                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                finalIntent();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
                finalIntent();
            }

        }
    }
    //***************************************************** update public avatar ******************************************
    public void uploadImageFileToFriendFirebaseStorage(String avatar) {

        final FilePaths filePaths = new FilePaths();
        if (avatar != null) {
            avatar = "file://" + avatar;
            Uri FilePathUri = Uri.parse(new File(avatar).toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();


                // Creating second StorageReference.
                final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/friend_image");

                // Adding addOnSuccessListener to second StorageReference.
                storageReferenceLocation.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                storageReferenceLocation.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {


                                            Map<String,Object> map = new HashMap<>();

                                            map.put(getString(R.string.field_friend_avatar),task.getResult().toString());


                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Image saved");
                                                        finalIntent();
                                                    } else {
                                                        finalIntent();
                                                    }
                                                }
                                            });
                                        }else{
                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                            finalIntent();
                                        }
                                    }
                                });


                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                finalIntent();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
                finalIntent();
            }

        }
    }





}
