package com.intouchapp.intouch.Main.Posts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Signup.GalleryActivity;
import com.intouchapp.intouch.Signup.SignupActivity;
import com.intouchapp.intouch.Utills.UniversalImageLoader;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;

public class PostPhotoActivity extends AppCompatActivity {

    private static final String TAG = "PostPhotoActivity";

    //widget
    private ImageView mBack,mImage;
    private EditText mWrite;
    private TextView next;

    private String image,write;
    private String activityName;

    private Context mContext;

    private Intent intent;
    private String mAppend = "file://";


    //temp
    private  int count = 1;

    ArrayList<String> userInputs = new ArrayList<String>();
    ArrayList<String> mIntent = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        mBack = (ImageView) findViewById(R.id.iv_back);
        next = (TextView) findViewById(R.id.tv_next);
        mImage = (ImageView) findViewById(R.id.iv_post_image);
        mWrite = (EditText) findViewById(R.id.et_saysomething);
        activityName = getString(R.string.PostPhotoActivity_class);
        mContext = PostPhotoActivity.this;
        image = getString(R.string.empty);
        next.setAlpha(0.5f);
        next.setEnabled(false);

        AndroidThreeTen.init(this);

        intentMethod();
        onTextChange();
        postImage();
        clickNext();
        setImage();

    }
    private void clickNext(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ShareWithActivity.class);
                mIntent.add(mWrite.getText().toString());
                mIntent.add(image);
                intent.putExtra(getString(R.string.intent_photo),mIntent);
                startActivity(intent);
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

                    Log.d(TAG, "onClick: " + mWrite.getText().toString());
                    Intent i = new Intent(PostPhotoActivity.this, GalleryActivity.class);
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

            Intent i = new Intent(PostPhotoActivity.this, GalleryActivity.class);
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
            next.setAlpha(0.5f);
            next.setEnabled(false);
            return false;
        }else {
            next.setAlpha(1f);
            next.setEnabled(true);
            return true;
        }

    }
}
