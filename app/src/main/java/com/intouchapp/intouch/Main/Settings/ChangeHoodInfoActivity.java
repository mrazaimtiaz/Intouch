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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
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

public class ChangeHoodInfoActivity extends AppCompatActivity {

    ImageView mAvatar,mBack;

    EditText mName,mBio;

    String name,bio,image;

    TextView mUpdate;

    //variable
    private User user;
    private Hood hood = new Hood();

    private Context mContext;

    private Intent intent;
    private String mAppend = "file://";

    private ConstraintLayout mMain,mProgress;

    private String activityName;

    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;

    private static final String TAG = "ChangeHoodInfoActivity";


    //temp
    private  int count = 1;

    ArrayList<String> userInputs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_hood_info);


        mAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mName = (EditText) findViewById(R.id.et_housename);
        mBio = (EditText) findViewById(R.id.et_bio);

        mUpdate = (TextView) findViewById(R.id.update);

        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mContext = ChangeHoodInfoActivity.this;
        activityName = getString(R.string.ChangeHoodInfo_class);
        user =  ((UserClient) (mContext.getApplicationContext())).getUser();

        hood.setName("");
        hood.setBio("");


        mUpdate.setAlpha(0.5f);
        mUpdate.setEnabled(false);

        image = getString(R.string.empty);
        if(user != null){
            try {
                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);

                            if(getIntent().getBooleanExtra(getString(R.string.intent_no_request),true)) {
                                mName.setText(hood.getName());
                                if(!hood.getBio().equals(getString(R.string.empty)))
                                    mBio.setText(hood.getBio());
                                else
                                    mBio.setText("");
                                UniversalImageLoader.setImage(hood.getImage(), mAvatar, null, "", mContext);
                            }

                        }
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }


        }


        checkText();
        intentMethod();
        postImage();
        setImage();
        updateHood();
    }
    //******************************************* updateHouse ****************************************************
    private void updateHood(){
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdate.setEnabled(false);
                try {
                    mMain.setVisibility(View.GONE);
                    mProgress.setVisibility(View.VISIBLE);
                    mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                            Log.d(TAG, "onComplete: house condition" + image);
                            assert hood != null;
                            if(!hood.getImage().equals(image) && !image.equals(getString(R.string.empty))){
                                uploadImageFileToFirebaseStorage(image);
                            }else{
                                Log.d(TAG, "onComplete: else conditin called" + image);
                                hood.setName(mName.getText().toString());
                                hood.setBio(mBio.getText().toString());

                                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).set(hood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finalIntent();
                                        mUpdate.setEnabled(true);
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
    //********************************************* check text *****************************************************
    private void checkText(){
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkError();
            }
        });

        mBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkError();
            }
        });
    }

    //************************************************check error****************************************************
    public boolean checkError() {

        Log.d(TAG, "checkError: checking errors");

        name = mName.getText().toString();
        bio = mBio.getText().toString();


        if (name.isEmpty()  || name.length() < 3 || name.length() > 12 || bio.isEmpty()   || bio.length() > 56 || (name.equals(hood.getName()) && bio.equals(hood.getBio())))
        {
            mUpdate.setAlpha(0.5f);
            mUpdate.setEnabled(false);
            return false;


        }else {
            mUpdate.setAlpha(1f);
            mUpdate.setEnabled(true);
            return true;
        }

    }
    //********************************************************** profile image *************************************************
    public void postImage() {

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");



                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(mName.getText().toString());
                    userInputs.add(mBio.getText().toString());

                    Log.d(TAG, "onClick: " + mName.getText().toString());
                    Intent i = new Intent(mContext, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.intent_no_request),false);
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

            mName.setText(fetchList.get(0));
            mBio.setText(fetchList.get(1));

        }

        if (intent.hasExtra(getString(R.string.selected_image))) {
            mUpdate.setAlpha(1f);
            mUpdate.setEnabled(true);
            image = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: get new imgURL: " + image);
            UniversalImageLoader.setImage(image, mAvatar, null, mAppend,mContext);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean showRationale = shouldShowRequestPermissionRationale( permissions[0]);

        Log.d(TAG, "onRequestPermissionsResult: " + count);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: granted " + count);
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

            count = 0;

            //resume tasks needing this permission
            userInputs.add(mName.getText().toString());
            userInputs.add(mBio.getText().toString());

            Intent i = new Intent(mContext, GalleryActivity.class);
            i.putExtra(getString(R.string.activity_name),activityName);
            Log.d(TAG, "onClick: activity name" + activityName);
            i.putExtra(getString(R.string.intent_no_request),false);
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
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data = baos.toByteArray();

                if(user != null){
                    // Creating second StorageReference.
                    final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_HOOD_IMAGE_STORAGE + "/" + user.getN_id() + "/hood_image");

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

                                                map.put(getString(R.string.field_house_image),task.getResult().toString());
                                                map.put(getString(R.string.field_house_name),mName.getText().toString());
                                                map.put(getString(R.string.field_house_bio),mBio.getText().toString());

                                                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "onComplete: Image saved");
                                                            finalIntent();
                                                        } else {
                                                            finalIntent();
                                                            mMain.setVisibility(View.VISIBLE);
                                                            mProgress.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                            }else{
                                                finalIntent();
                                                mMain.setVisibility(View.VISIBLE);
                                                mProgress.setVisibility(View.GONE);
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
                }


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
}
