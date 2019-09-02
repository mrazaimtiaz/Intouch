package com.intouchapp.intouch.Signup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLng;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Compliment;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.Introduction.IntroductionActivity;
import com.intouchapp.intouch.Register.Login.LoginActivity;
import com.intouchapp.intouch.Register.SplashActivity;
import com.intouchapp.intouch.Utills.FilePaths;
import com.intouchapp.intouch.Utills.UniversalImageLoader;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NewHouseActivity extends AppCompatActivity {

    //widget
    private ImageView mBack, mHouseImage,mHoodImage;
    private EditText mHouseName,mHoodName, mHouseEdit,mHoodEdit;
    private TextView mOptionalName,mOptionalImage;
    private Button mCreate;
    private ConstraintLayout mMain;
    private ConstraintLayout mProgress;
    //variable
    private String houseName,hoodName,houseImage,hoodImage;
    private Context mContext;
    private boolean visible;
    ArrayList<String> userInputs = new ArrayList<String>();
    ArrayList<String> fetchList = new ArrayList<String>();
    private String activityName;
    private Intent intent;
    private String n_id;
    private String mAppend = "file://";

    double longitudeLeft;
    double longitudeRight;
    double latitudeLeft;
    double latitudeRight;

    double currentLatitude;
    double currentLongitude;

    //temp
    private  int count = 1;
    private int intentStatus = 0;
    private boolean hoodCheck;
    private Bundle bundle;

    //firebase
    FirebaseFirestore mDb;
    private StorageReference mStorageRef;

    private static final String TAG = "NewHouseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_house);


        //intialization
        mBack = (ImageView) findViewById(R.id.iv_back);
        mHouseImage = (ImageView) findViewById(R.id.iv_houseimage);
        mHoodImage = (ImageView) findViewById(R.id.iv_hoodimage);
        mHouseName = (EditText) findViewById(R.id.et_housename);
        mHoodName = (EditText) findViewById(R.id.et_hoodname);
        mHouseEdit = (EditText) findViewById(R.id.et_house_image);
        mHoodEdit = (EditText) findViewById(R.id.et_hood_image);

        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        mOptionalName = (TextView) findViewById(R.id.tv_optionalname);
        mOptionalImage = (TextView) findViewById(R.id.tv_optionalimage);

        mCreate = (Button) findViewById(R.id.btn_create);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        AndroidThreeTen.init(this);
        mContext = NewHouseActivity.this;
        houseImage = getString(R.string.empty);
        hoodImage = getString(R.string.empty);
        activityName = getString(R.string.NewHouseActivity_class);
        visible = true;
        hoodCheck = false;
        try{
            if(!getIntent().getExtras().isEmpty()){
                bundle = getIntent().getExtras();

                longitudeLeft = bundle.getDouble(getString(R.string.longitudeLeft));
                longitudeRight = bundle.getDouble(getString(R.string.longitudeRight));
                latitudeLeft = bundle.getDouble(getString(R.string.latitudeLeft));
                latitudeRight = bundle.getDouble(getString(R.string.latitudeRight));

                currentLatitude = bundle.getDouble(getString(R.string.currentLatitude));
                currentLongitude = bundle.getDouble(getString(R.string.currentLongititude));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }




        Log.d(TAG, "onCreate: " + currentLatitude + " " + currentLongitude + " " + longitudeLeft + " " + longitudeRight + " " + latitudeLeft + " " + latitudeRight);


        intentMethod();
        setImage();
        image();
        n_id = getString(R.string.empty);
       new BackgroundHood().execute();


    }
    private void checkNeighbourHood(){
        Log.d(TAG, "checkNeighbourHood: called");
        try {
            Tasks.await(mDb.collection(getString(R.string.collection_hoods))
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                hoodCheck = true;
                                final List<DocumentSnapshot> hood_list;
                                hood_list = task.getResult().getDocuments();
                                for (int j = 0; j < hood_list.size(); j++) {
                                    Hood hood = hood_list.get(j).toObject(Hood.class);
                                    Log.d(TAG, "onComplete: values " + currentLongitude +  "<" + hood.getLongitude().getLongitude() + "&&" + currentLongitude  + ">" + hood.getLongitude().getLatitude() +

                                            "&&" + currentLatitude + "<" +  hood.getLatitude().getLongitude() + "&&" + currentLatitude + ">" + hood.getLatitude().getLatitude());
                                    if(currentLongitude < hood.getLongitude().getLongitude() && currentLongitude > hood.getLongitude().getLatitude()

                                            && currentLatitude < hood.getLatitude().getLongitude() && currentLatitude > hood.getLatitude().getLatitude()){
                                        visible = false;
                                        Log.d(TAG, "onComplete: hood matched" + visible);
                                        n_id = hood_list.get(j).getId();
                                        Log.d(TAG, "onComplete: n_id is " + n_id);
                                        mOptionalImage.setVisibility(View.VISIBLE);
                                        mOptionalName.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    //*********************************************** create house ****************************************
    private void createHouse(){
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkError()){
                            mMain.setVisibility(View.GONE);
                            mProgress.setVisibility(View.VISIBLE);
                        if(!n_id.equals(getString(R.string.empty))){

                            ifPart();

                        }else{

                            elsePart();
                        }

                }
            }
        });
    }
    private void ifPart(){
        try{
            final FilePaths filePaths = new FilePaths();
            final List<String> members = new ArrayList<>();
            houseImage = "file://" + houseImage;
            Uri FilePathUri = Uri.parse(new File(houseImage).toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
            } catch (IOException e) {
                Log.d(TAG, "onClick: IOException " + e.getMessage());
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            members.add(FirebaseAuth.getInstance().getUid());

            final String h_id = mDb.collection(getString(R.string.collection_hoods)).document().getId();

            mStorageRef.child(filePaths.FIREBASE_HOUSE_IMAGE_STORAGE + "/" + h_id + "/house_image").putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    mStorageRef.child(filePaths.FIREBASE_HOUSE_IMAGE_STORAGE + "/" + h_id + "/house_image").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            ZoneId tz =  ZoneId.systemDefault();
                            LocalDateTime localDateTime = LocalDateTime.now();
                            long seconds = localDateTime.atZone(tz).toEpochSecond();
                            int nanos = localDateTime.getNano();
                            Timestamp timestamp = new Timestamp(seconds, nanos);

                            Map<String,Object> map = new HashMap<>();
                            map.put(getString(R.string.n_id),n_id);
                            map.put(getString(R.string.h_id),h_id);
                            final House house = new House();
                            house.setH_id(h_id);
                            house.setName(mHouseName.getText().toString());
                            house.setLocation(new GeoPoint(currentLatitude,currentLongitude));
                            house.setImage(task.getResult().toString());
                            house.setMembers(members);
                            house.setBio(getString(R.string.empty));
                            house.setN_id(n_id);
                            house.setTimestamp(timestamp);
                            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


                            WriteBatch batch = mDb.batch();
                            batch.set(mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(h_id),house);
                            batch.update( mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()),map);
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(NewHouseActivity.this,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
            Intent intent = new Intent(mContext, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }
    private void elsePart(){

        try {
            final FilePaths filePaths = new FilePaths();
            hoodImage = "file://" + hoodImage;
            Uri FilePathUri = Uri.parse(new File(hoodImage).toString());
            final List<String> members = new ArrayList<>();

            members.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
            } catch (IOException e) {
                Log.d(TAG, "onClick: IOException " + e.getMessage());
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            final String ne_id = mDb.collection(getString(R.string.collection_hoods)).document().getId();

            mStorageRef.child(filePaths.FIREBASE_HOOD_IMAGE_STORAGE + "/" + ne_id + "/hood_image").putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    mStorageRef.child(filePaths.FIREBASE_HOOD_IMAGE_STORAGE + "/" + ne_id + "/hood_image").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            hoodImage = task.getResult().toString();

                            final FilePaths filePaths = new FilePaths();
                            houseImage = "file://" + houseImage;
                            Uri FilePathUri = Uri.parse(new File(houseImage).toString());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                            } catch (IOException e) {
                                Log.d(TAG, "onClick: IOException " + e.getMessage());
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();
                            final String h_id = mDb.collection(getString(R.string.collection_hoods)).document().getId();

                            mStorageRef.child(filePaths.FIREBASE_HOUSE_IMAGE_STORAGE + "/" + h_id + "/house_image").putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    mStorageRef.child(filePaths.FIREBASE_HOUSE_IMAGE_STORAGE + "/" + h_id + "/house_image").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            houseImage = task.getResult().toString();

                                            Compliment compliment = new Compliment();
                                            compliment.setComp1(0.0);
                                            compliment.setComp2(0.0);
                                            compliment.setComp3(0.0);
                                            compliment.setComp4(0.0);
                                            compliment.setComp5(0.0);
                                            compliment.setComp6(0.0);

                                            Hood hood = new Hood();

                                            hood.setName(mHoodName.getText().toString());
                                            hood.setId(ne_id);
                                            hood.setLatitude(new GeoPoint(latitudeLeft,latitudeRight));
                                            hood.setLongitude(new GeoPoint(longitudeLeft,longitudeRight));
                                            hood.setImage(hoodImage);
                                            hood.setBio(getString(R.string.empty));
                                            mDb.collection(getString(R.string.collection_compliment)).document(ne_id).set(compliment);

                                            mDb.collection(getString(R.string.collection_hoods)).document(ne_id).set(hood)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            ZoneId tz =  ZoneId.systemDefault();
                                                            LocalDateTime localDateTime = LocalDateTime.now();
                                                            long seconds = localDateTime.atZone(tz).toEpochSecond();
                                                            int nanos = localDateTime.getNano();
                                                            Timestamp timestamp = new Timestamp(seconds, nanos);

                                                            Map<String,Object> map = new HashMap<>();
                                                            map.put(getString(R.string.n_id),ne_id);
                                                            map.put(getString(R.string.h_id),h_id);
                                                            final House house = new House();
                                                            house.setH_id(h_id);
                                                            house.setName(mHouseName.getText().toString());
                                                            house.setLocation(new GeoPoint(currentLatitude,currentLongitude));
                                                            house.setImage(houseImage);
                                                            house.setMembers(members);
                                                            house.setBio(getString(R.string.empty));
                                                            house.setN_id(ne_id);
                                                            house.setTimestamp(timestamp);
                                                            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


                                                            WriteBatch batch = mDb.batch();
                                                            batch.set(mDb.collection(getString(R.string.collection_hoods)).document(ne_id).collection(getString(R.string.collection_houses)).document(h_id),house);
                                                            batch.update( mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()),map);

                                                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Intent intent = new Intent(NewHouseActivity.this,MainActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                    finish();

                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    });
                                }


                            });
                        }

                    });
                }
            });
        }catch (NullPointerException e){
            Intent intent = new Intent(mContext, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


    }

    //***********************************************intent method *********************************************
    private void intentMethod() {

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //********************************************************** profile image *************************************************
    public void image() {

        mHouseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 1;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(mHoodName.getText().toString());
                    userInputs.add(mHouseName.getText().toString());
                    userInputs.add(houseImage);
                    userInputs.add(hoodImage);
                    userInputs.add(getString(R.string.house_image));
                    userInputs.add(String.valueOf(currentLatitude));
                    userInputs.add(String.valueOf(currentLongitude));
                    userInputs.add(String.valueOf(latitudeLeft));
                    userInputs.add(String.valueOf(latitudeRight));
                    userInputs.add(String.valueOf(longitudeLeft));
                    userInputs.add(String.valueOf(longitudeRight));

                    Intent i = new Intent(NewHouseActivity.this, GalleryActivity.class);
                    i.putExtra(getString(R.string.activity_name),activityName);
                    Log.d(TAG, "onClick: activity name" + activityName);
                    i.putExtra(getString(R.string.user_inputs), userInputs);
                    startActivity(i);
                }
            }
        });


        mHoodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");

                intentStatus = 2;

                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(mHoodName.getText().toString());
                    userInputs.add(mHouseName.getText().toString());
                    userInputs.add(houseImage);
                    userInputs.add(hoodImage);
                    userInputs.add(getString(R.string.hood_image));
                    userInputs.add(String.valueOf(currentLatitude));
                    userInputs.add(String.valueOf(currentLongitude));
                    userInputs.add(String.valueOf(latitudeLeft));
                    userInputs.add(String.valueOf(latitudeRight));
                    userInputs.add(String.valueOf(longitudeLeft));
                    userInputs.add(String.valueOf(longitudeRight));

                    Intent i = new Intent(NewHouseActivity.this, GalleryActivity.class);
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
        try{
            Log.d(TAG, "setImage: intent staus" + intentStatus);
            intent = getIntent();
            Log.d(TAG, "setImage: " + intent);
            if (intent.hasExtra(getString(R.string.user_inputs))) {


                fetchList = getIntent().getStringArrayListExtra(getString(R.string.user_inputs));
                Log.d(TAG, "onClick: data settings" + fetchList.get(3));

                mHoodName.setText(fetchList.get(0));
                mHouseName.setText(fetchList.get(1));
                houseImage = fetchList.get(2);
                hoodImage = fetchList.get(3);
                currentLatitude = Double.parseDouble(fetchList.get(5));
                currentLongitude = Double.parseDouble(fetchList.get(6));
                latitudeLeft = Double.parseDouble(fetchList.get(7));
                latitudeRight = Double.parseDouble(fetchList.get(8));
                longitudeLeft = Double.parseDouble(fetchList.get(9));
                longitudeRight = Double.parseDouble(fetchList.get(10));

                if(fetchList.get(4).equals(getString(R.string.house_image)))
                    intentStatus = 1;
                else if(fetchList.get(4).equals(getString(R.string.hood_image)))
                    intentStatus = 2;

            }
            Log.d(TAG, "setImage: intent new " + intentStatus);

            if (intent.hasExtra(getString(R.string.selected_image))) {
                if(intentStatus == 1 ){
                    houseImage = intent.getStringExtra(getString(R.string.selected_image));
                    Log.d(TAG, "setImage: get new imgURL: " + houseImage);
                    UniversalImageLoader.setImage(houseImage, mHouseImage, null, mAppend,mContext);

                    if(!hoodImage.equals(getString(R.string.empty))){
                        Log.d(TAG, "setImage: get new imgURL: " + hoodImage);
                        UniversalImageLoader.setImage(hoodImage, mHoodImage, null, mAppend,mContext);
                    }
                }
            }
            if (intent.hasExtra(getString(R.string.selected_image))) {
                if(intentStatus == 2 ){
                    hoodImage = intent.getStringExtra(getString(R.string.selected_image));
                    Log.d(TAG, "setImage: get new imgURL: " + hoodImage);
                    UniversalImageLoader.setImage(hoodImage, mHoodImage, null, mAppend,mContext);

                    if(!houseImage.equals(getString(R.string.empty))){
                        Log.d(TAG, "setImage: get new imgURL: " + houseImage);
                        UniversalImageLoader.setImage(houseImage, mHouseImage, null, mAppend,mContext);
                    }
                }


            }

        }catch (NullPointerException e){
            finish();
            e.printStackTrace();
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
            if(intentStatus == 1){
                userInputs.add(mHoodName.getText().toString());
                userInputs.add(mHouseName.getText().toString());
                userInputs.add(houseImage);
                userInputs.add(hoodImage);
                userInputs.add(getString(R.string.house_image));
                userInputs.add(String.valueOf(currentLatitude));
                userInputs.add(String.valueOf(currentLongitude));
                userInputs.add(String.valueOf(latitudeLeft));
                userInputs.add(String.valueOf(latitudeRight));
                userInputs.add(String.valueOf(longitudeLeft));
                userInputs.add(String.valueOf(longitudeRight));

            }else if(intentStatus == 2) {
                userInputs.add(mHoodName.getText().toString());
                userInputs.add(mHouseName.getText().toString());
                userInputs.add(houseImage);
                userInputs.add(hoodImage);
                userInputs.add(getString(R.string.hood_image));
                userInputs.add(String.valueOf(currentLatitude));
                userInputs.add(String.valueOf(currentLongitude));
                userInputs.add(String.valueOf(latitudeLeft));
                userInputs.add(String.valueOf(latitudeRight));
                userInputs.add(String.valueOf(longitudeLeft));
                userInputs.add(String.valueOf(longitudeRight));
            }


            Intent i = new Intent(NewHouseActivity.this, GalleryActivity.class);
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

    //***************************************** checkerror ***************************************************************
    public boolean checkError(){
        houseName = mHouseName.getText().toString();
        hoodName = mHoodName.getText().toString();

        if(checkStringEmpty(houseName)||(checkStringEmpty(hoodName) && visible)
                || (houseName.length() < 3) || (houseName.length() > 12)
                 || ((hoodName.length() < 3)&& visible) || ((hoodName.length() > 12)&& visible)
                || (houseImage.equals(getString(R.string.empty))) || (hoodImage.equals(getString(R.string.empty))&& visible) ){


            if (hoodName.length() > 12 && visible) {
                mHoodName.setError(getString(R.string.maximum_length_12));
                mHoodName.requestFocus();
            }
            if (hoodName.length() < 3 && visible) {
                mHoodName.setError(getString(R.string.minimum_length_3));
                mHoodName.requestFocus();

            }
            if(checkStringEmpty(hoodName) && visible){
                mHoodName.setError(getString(R.string.hoodname_is_required));
                mHoodName.requestFocus();
            }
            if((hoodImage.equals(getString(R.string.empty))&& visible) ){
                mHoodEdit.setError(getString(R.string.hoodimage_is_required));
                mHoodEdit.requestFocus();
            }
            if (houseName.length() > 12) {
                mHouseName.setError(getString(R.string.maximum_length_12));
                mHouseName.requestFocus();
            }
            if (houseName.length() < 3) {
                mHouseName.setError(getString(R.string.minimum_length_3));
                mHouseName.requestFocus();

            }
            if(checkStringEmpty(houseName)){
                mHouseName.setError(getString(R.string.housename_is_required));
                mHouseName.requestFocus();
            }
            if(houseImage.equals(getString(R.string.empty)))  {
                mHouseEdit.setError(getString(R.string.houseimage_is_required));
                mHouseEdit.requestFocus();
            }
            return false;
        }

        return true;
    }

    //***************************************** check string is empty ***************************************************************
    public boolean checkStringEmpty(String string){

        return string.isEmpty();
    }
    @SuppressLint("StaticFieldLeak")
    class BackgroundHood extends AsyncTask<Void, String, Void> {


        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        BackgroundHood(){

        }
        @Override
        protected void onPreExecute() {


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Void doInBackground(Void... myBundle) {
            Log.d(TAG, "doInBackground: ");
            checkNeighbourHood();
            Log.d(TAG, "doInBackground: n_id " + n_id);
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void createHouse) {
            Log.d(TAG, "onpost onComplete: n_id is " + n_id);
            createHouse();

            }
        }

    }
