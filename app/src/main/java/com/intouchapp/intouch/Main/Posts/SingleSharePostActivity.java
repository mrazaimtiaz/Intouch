package com.intouchapp.intouch.Main.Posts;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Signup.GalleryActivity;
import com.intouchapp.intouch.Utills.FilePaths;
import com.intouchapp.intouch.Utills.UniversalImageLoader;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SingleSharePostActivity extends AppCompatActivity {

    private static final String TAG = "SingleSharePostActivity";
    //widget
    private ImageView mBack,mImage;
    private EditText mWrite;
    private TextView post,shareHouseName;

    private ConstraintLayout mMain,mProgress;

    private String image,write;
    private String activityName;

    private Context mContext;

    //variable
    private String n_id,h_id,type;
    private StorageReference mStorageRef;

    private Intent intent;
    private String mAppend = "file://";

    //Firebase
    FirebaseFirestore mDb;


    //temp
    private  int count = 1;

    ArrayList<String> userInputs = new ArrayList<String>();
    ArrayList<String> mIntent = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_share_post);

        mBack = (ImageView) findViewById(R.id.iv_back);
        post = (TextView) findViewById(R.id.tv_post);
        shareHouseName = (TextView) findViewById(R.id.share_house_name);
        mImage = (ImageView) findViewById(R.id.iv_post_image);
        mWrite = (EditText) findViewById(R.id.et_saysomething);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        activityName = getString(R.string.SingleSharePostActivity_class);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mContext = SingleSharePostActivity.this;
        mDb = FirebaseFirestore.getInstance();

        image = getString(R.string.empty);
        n_id = getString(R.string.empty);
        h_id = getString(R.string.empty);
        type = getString(R.string.empty);

        if(getIntent().getStringExtra(getString(R.string.n_id)) != null)
            n_id = getIntent().getStringExtra(getString(R.string.n_id));
        if(getIntent().getStringExtra(getString(R.string.h_id)) != null)
            h_id = getIntent().getStringExtra(getString(R.string.h_id));
        if(getIntent().getStringExtra(getString(R.string.intent_type)) != null)
            type = getIntent().getStringExtra(getString(R.string.intent_type));

        Log.d(TAG, "onCreate: id of " + n_id + h_id);

        setImage();
        intentMethod();
        onTextChange();
        postImage();
        clickNext();
        textType();


    }
    private void textType(){
        if(type.equals(getString(R.string.type_hood))){
            try {
                mDb.collection(getString(R.string.collection_hoods)).document(n_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                            assert hood != null;
                            shareHouseName.setText(hood.getName());
                        }
                    }
                });
            }catch (NullPointerException e){
             e.printStackTrace();
            }

        }if(type.equals(getString(R.string.type_relative)) || type.equals(getString(R.string.type_member))){
            try {
                mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                            assert house != null;
                            shareHouseName.setText(house.getName());
                        }
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }
    private void clickNext(){

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Log.d(TAG, "onClick: " + type + " " + mWrite.getText().toString() + " " + image);
                        if(!type.equals(getString(R.string.empty)) && !image.equals(getString(R.string.empty))){
                            try {
                                post.setEnabled(false);
                                mMain.setVisibility(View.GONE);
                                mProgress.setVisibility(View.VISIBLE);

                                post.setAlpha(0.5f);
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

                                                    final Post postUpload = new Post();
                                                    postUpload.setU_id(FirebaseAuth.getInstance().getUid());
                                                    postUpload.setDescription(mWrite.getText().toString());
                                                    postUpload.setImage(Objects.requireNonNull(task.getResult()).toString());
                                                    postUpload.setType(type);
                                                    postUpload.setP_id(p_id);
                                                    if(type.equals(getString(R.string.type_hood)))
                                                        postUpload.setShareWith(n_id);
                                                    if(type.equals(getString(R.string.type_relative)) || type.equals(getString(R.string.type_member)))
                                                        postUpload.setShareWith(h_id);
                                                    postUpload.setTimestamp(timestamp);

                                                    mDb.collection(getString(R.string.collection_post)).document(p_id).set(postUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Intent intent = new Intent(mContext, MainActivity.class);
                                                                Log.d(TAG, "onComplete: saved");
                                                                startActivity(intent);
                                                                finish();
                                                            }else{
                                                                post.setEnabled(true);
                                                                mMain.setVisibility(View.VISIBLE);
                                                                mProgress.setVisibility(View.GONE);
                                                                Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    post.setEnabled(true);
                                                    mMain.setVisibility(View.VISIBLE);
                                                    mProgress.setVisibility(View.GONE);
                                                    Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }
                                });

                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }

                        }
            }
        });
    }
    private void onTextChange(){
        mWrite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkError();
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkError();
            }
        });
    }
    //********************************************************** profile image *************************************************
    public void postImage() {

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");



                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(mWrite.getText().toString());
                    userInputs.add(type);
                    userInputs.add(n_id);
                    userInputs.add(h_id);

                    Log.d(TAG, "onClick: " + mWrite.getText().toString());
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });
    }
    //*********************************************** setimage ***********************************************************
    private void setImage() {
        intent = getIntent();
        Log.d(TAG, "setImage: " + intent);
        if (intent.hasExtra(getString(R.string.user_inputs))) {

            ArrayList<String> fetchList = new ArrayList<String>();
            fetchList = getIntent().getStringArrayListExtra(getString(R.string.user_inputs));
            Log.d(TAG, "onClick: data settings" + fetchList.get(0));

            mWrite.setText(fetchList.get(0));
            type = fetchList.get(1);
            n_id = fetchList.get(2);
            h_id = fetchList.get(3);

            Log.d(TAG, "setImage: id of " + n_id + h_id);

        }

        if (intent.hasExtra(getString(R.string.selected_image))) {
            image = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: get new imgURL: " + image);
            UniversalImageLoader.setImage(image, mImage, null, mAppend,mContext);
        }
        checkError();


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

            //resume tasks needing this permission
            userInputs.add(mWrite.getText().toString());
            userInputs.add(type);
            userInputs.add(n_id);
            userInputs.add(h_id);

            Intent i = new Intent(mContext, GalleryActivity.class);
            i.putExtra(getString(R.string.activity_name),activityName);
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


    //************************************************** intent method *********************************************
    private void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //************************************************check error****************************************************
    public boolean checkError() {

        Log.d(TAG, "checkError: checking errors");

        write = mWrite.getText().toString();


        if (write.isEmpty() || image.equals(getString(R.string.empty)))
        {
            post.setAlpha(0.5f);
            post.setEnabled(false);
            return false;
        }else {
            post.setAlpha(1f);
            post.setEnabled(true);
            return true;
        }

    }
}
