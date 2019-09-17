package com.intouchapp.intouch.Signup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.Utills.MyFirebaseMessagingService;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.Login.LoginActivity;
import com.intouchapp.intouch.Utills.SharedPreManager;
import com.intouchapp.intouch.Utills.UserClient;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    //widget
    EditText mAvatar,mFirstName,mLastName,mUsername,mEmail,mPassword,mPhonenumber;

    CountryCodePicker mCode;

    TextView mHaveAnAccount;

    ImageView mBack;

    CircleImageView mAvatarImage;

    Button mNext;

    ConstraintLayout mMain,mProgressBar;

    //variables
    Context mContext;
    String name, firstname, lastname, email, password, username,avatar,phonenumber;
    private String mAppend = "file://";
    private String user_id;
    ArrayList<String> userInputs = new ArrayList<String>();
    private String activityName;
    String device_token;

    BroadcastReceiver broadcastReceiver;
    private Intent intent;

    String code;




    //temp
    private  int count = 1;



    //firebase
    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;



    private static final String TAG = "SignupActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //intialization
        mAvatar = (EditText) findViewById(R.id.et_avatar);
        mFirstName = (EditText) findViewById(R.id.et_first_name);
        mLastName = (EditText) findViewById(R.id.et_lastname);
        mUsername = (EditText) findViewById(R.id.et_username);
        mEmail = (EditText) findViewById(R.id.et_email);
        mPassword = (EditText) findViewById(R.id.et_password);
        mPhonenumber = (EditText) findViewById(R.id.et_phonenumber);
        mCode = (CountryCodePicker) findViewById(R.id.countrycode);

        mCode.setAutoDetectedCountry(true);


        mHaveAnAccount = (TextView) findViewById(R.id.tv_haveanaccount);

        mBack = (ImageView) findViewById(R.id.iv_back);
        mAvatarImage = (CircleImageView) findViewById(R.id.iv_avatar);

        mNext = (Button) findViewById(R.id.btn_next);

        mProgressBar = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);

        mContext = SignupActivity.this;
        activityName = getString(R.string.SignupActivity_class);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        AndroidThreeTen.init(this);

        avatar = getString(R.string.empty);

        intentMethod();
        removeSpaceBar();
        profileImage();
        setImage();
        registerSignup();




    }

    //***********************************************intent method *********************************************
    private void intentMethod() {
        mHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    //*******************************************remove space bar****************************************************
    private void removeSpaceBar(){
        /* To restrict Space Bar in Keyboard */
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        mUsername.setFilters(new InputFilter[] { filter });
        mEmail.setFilters(new InputFilter[] { filter });
        mPassword.setFilters(new InputFilter[] { filter });
    }

    //************************************************check error****************************************************
    public boolean checkError() {

        Log.d(TAG, "checkError: checking errors");

        firstname = mFirstName.getText().toString();
        lastname = mLastName.getText().toString();
        name = firstname + " " + lastname;
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        username = mUsername.getText().toString();
        phonenumber = mPhonenumber.getText().toString();


        if ((!firstname.matches("[a-zA-Z. ]*")) || checkStringEmpty(firstname) || (firstname.length() < 3) || (firstname.length() > 8) ||
                (!lastname.matches("[a-zA-Z. ]*")) || checkStringEmpty(lastname) || (lastname.length() < 3) || (lastname.length() > 8) ||
                checkStringEmpty(username) || !username.matches("[a-zA-Z0-9. ]*") || (username.length() < 3) || (username.length() > 12) ||
                checkStringEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher((email)).matches() ||
                checkStringEmpty(password) || characterlength(password) || (password.length() > 30) || (avatar.equals(getString(R.string.empty))) ||
        checkStringEmpty(phonenumber) || characterlength(phonenumber) || (phonenumber.length() > 30))
        {
            if (characterlength(phonenumber)) {
                mPhonenumber.setError(getString(R.string.minimum_length_6));
                mPhonenumber.requestFocus();

            }
            if (phonenumber.length() > 20) {
                mPhonenumber.setError(getString(R.string.maximum_length_20));
                mPhonenumber.requestFocus();
            }
            if (checkStringEmpty(phonenumber)) {
                mPhonenumber.setError("Phonenumber is required");
                mPhonenumber.requestFocus();

            }
            if (!password.matches("[a-zA-Z0-9. ]*")) {
                mPassword.setError(getString(R.string.special_character_not_allowed));
                mPassword.requestFocus();
            }
            if (characterlength(password)) {
                mPassword.setError(getString(R.string.minimum_length_6));
                mPassword.requestFocus();

            }
            if (password.length() > 20) {
                mPassword.setError(getString(R.string.maximum_length_20));
                mPassword.requestFocus();
            }
            if (checkStringEmpty(password)) {
                mPassword.setError(getString(R.string.password_is_required));
                mPassword.requestFocus();

            }

            if (!Patterns.EMAIL_ADDRESS.matcher((email)).matches()) {
                mEmail.setError(getString(R.string.error_badly_formatted));
                mEmail.requestFocus();

            }
            if (checkStringEmpty(email)) {
                mEmail.setError(getString(R.string.email_is_required));
                mEmail.requestFocus();

            }

            if (!username.matches("[a-zA-Z0-9. ]*")) {
                mUsername.setError(getString(R.string.special_character_not_allowed));
                mUsername.requestFocus();
            }
            if (username.length() > 12) {
                mUsername.setError(getString(R.string.maximum_length_12));
                mUsername.requestFocus();
            }
            if (username.length() < 3) {
                mUsername.setError(getString(R.string.minimum_length_3));
                mUsername.requestFocus();

            }
            if (checkStringEmpty(username)) {
                mUsername.setError(getString(R.string.username_is_required));
                mUsername.requestFocus();

            }

            if (!lastname.matches("[a-zA-Z. ]*")) {
                mLastName.setError(getString(R.string.special_character_not_allowed));
                mLastName.requestFocus();

            }
            if (lastname.length() > 8) {
                mLastName.setError(getString(R.string.maximum_length_8));
                mLastName.requestFocus();
            }
            if (lastname.length() < 3) {
                mLastName.setError(getString(R.string.minimum_length_3));
                mLastName.requestFocus();

            }
            if (checkStringEmpty(lastname)) {
                mLastName.setError(getString(R.string.lastname_is_required));
                mLastName.requestFocus();

            }


            if (!firstname.matches("[a-zA-Z. ]*")) {
                mFirstName.setError(getString(R.string.special_character_not_allowed));
                mFirstName.requestFocus();

            }
            if (firstname.length() > 8) {
                mFirstName.setError(getString(R.string.maximum_length_8));
                mFirstName.requestFocus();
            }
            if (firstname.length() < 3) {
                mFirstName.setError(getString(R.string.minimum_length_3));
                mFirstName.requestFocus();

            }
            if (checkStringEmpty(firstname)) {
                mFirstName.setError(getString(R.string.firstname_is_required));
                mFirstName.requestFocus();

            }
            if (avatar.equals(getString(R.string.empty))) {
                mAvatar.setError(getString(R.string.avatar_is_required));
                mAvatar.requestFocus();

            }
            return false;
        }
        return true;
    }

    public boolean characterlength(String string) {
        if (string.length() < 6) {
            return true;
        }
        return false;
    }


    public boolean checkStringEmpty(String string) {

        if (string.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    //********************************************************** profile image *************************************************
    public void profileImage() {

        mAvatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: profile image clicked");



                if (isStoragePermissionGranted()) {
                    // add elements to the array list

                    userInputs.add(mFirstName.getText().toString());
                    userInputs.add(mLastName.getText().toString());
                    userInputs.add(mUsername.getText().toString());
                    userInputs.add(mPassword.getText().toString());
                    userInputs.add(mEmail.getText().toString());
                    userInputs.add(mPhonenumber.getText().toString());

                    Intent i = new Intent(SignupActivity.this, GalleryActivity.class);
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
            Log.d(TAG, "onClick: data settings" + fetchList.get(3));

            mFirstName.setText(fetchList.get(0));
            mLastName.setText(fetchList.get(1));
            mUsername.setText(fetchList.get(2));
            mPassword.setText(fetchList.get(3));
            mEmail.setText(fetchList.get(4));
            mPhonenumber.setText(fetchList.get(5));
        }

        if (intent.hasExtra(getString(R.string.selected_image))) {
            avatar = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: get new imgURL: " + avatar);
            UniversalImageLoader.setImage(avatar, mAvatarImage, null, mAppend,mContext);
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

            //resume tasks needing this permission
            userInputs.add(mFirstName.getText().toString());
            userInputs.add(mLastName.getText().toString());
            userInputs.add(mUsername.getText().toString());
            userInputs.add(mPassword.getText().toString());
            userInputs.add(mEmail.getText().toString());
            userInputs.add(mPhonenumber.getText().toString());

            Intent i = new Intent(SignupActivity.this, GalleryActivity.class);
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
                .setMessage(getString(R.string.storage_permission))

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

    //*********************************************** register signup **********************************************************
    public void registerSignup() {

        Log.d(TAG, "registerSignup: signup starts");
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkError()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mMain.setVisibility(View.GONE);
                    //new CheckEmail().execute(email);

                    checkUserName(username);

                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    //********************************************* check username *************************************************
    public void checkUserName(String string) {

        Log.d(TAG, "checkUserName: started");

        CollectionReference mCollRef = mDb.collection(getString(R.string.collection_users));

        final Query notesQuery = mCollRef.whereEqualTo(getString(R.string.username), string);

        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "onComplete: function started");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.d(TAG, "onComplete:  data get");
                        String user = documentSnapshot.getString(getString(R.string.username));


                        if (user.equals(username)) {
                            Log.d(TAG, "onComplete: matched" + user);
                            mUsername.setError("username is not available");
                            mUsername.requestFocus();
                            mProgressBar.setVisibility(View.GONE);
                            mMain.setVisibility(View.VISIBLE);
                        }
                    }

                }
                if (task.getResult().size() == 0) {
                    Log.d(TAG, "onComplete: not matched");
                    checkPhoneNumber(phonenumber);
                }
            }
        });

    }
    //********************************************* check phno *************************************************
    public void checkPhoneNumber(String string) {

        Log.d(TAG, "checkUserName: started");

        CollectionReference mCollRef = mDb.collection(getString(R.string.collection_users));

        final Query notesQuery = mCollRef.whereEqualTo(getString(R.string.p_no), string);

        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "onComplete: function started");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.d(TAG, "onComplete:  data get");
                        String user = documentSnapshot.getString(getString(R.string.p_no));

                        if(user != null){
                            if (user.equals(phonenumber)) {
                                Log.d(TAG, "onComplete: matched" + user);
                                mPhonenumber.setError("Number is already in use");
                                mPhonenumber.requestFocus();
                                mProgressBar.setVisibility(View.GONE);
                                mMain.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                }
                if (task.getResult().size() == 0) {
                    Log.d(TAG, "onComplete: not matched");
                    registerUser(email, password);
                }
            }
        });

    }
    //**********************************-Firebase********************************************
    private void registerUser(final String email, final String password) {
        Log.d(TAG, "registerUser: autthenication started");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            UserProfileChangeRequest addHoodStatus = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateProfile(addHoodStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Display name: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                                        saavedata();


                                    }
                                }
                            });

                        }else {
                            mProgressBar.setVisibility(View.GONE);
                            mMain.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onComplete: error message" + Objects.requireNonNull(task.getException()).getMessage());
                            if(task.getException().getMessage().equals(getString(R.string.error_badly_formatted))){
                                mEmail.setError(getString(R.string.format_not_correct));
                                mEmail.requestFocus();
                            }
                            if(task.getException().getMessage().equals(getString(R.string.error_email_exist))){
                                Log.d(TAG, "onComplete: into the mail");
                                mEmail.setError(getString(R.string.email_already_exist));
                                mEmail.requestFocus();
                            }
                            if(task.getException().getMessage().equals(getString(R.string.error_internet))){
                                Log.d(TAG, "onComplete: into the intenret");
                                Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure" + task.getException().getMessage() + " " + R.string.error_badly_formatted, task.getException().getCause());


                        }
                    }
                });
    }
    private void sendPhoneVerification(){

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //Getting the code sent by SMS
                code = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                /*    otp.setText(code);
                    //verifying the code
                    verifyVerificationCode(code);*/
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } ;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhonenumber.getText().toString(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    //************************************************* sendVerificationEmail ****************************************************
    private void saavedata()
    {
        user_id = FirebaseAuth.getInstance().getUid();
        ZoneId tz =  ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.now();
        long seconds = localDateTime.atZone(tz).toEpochSecond();
        int nanos = localDateTime.getNano();
        Timestamp timestamp = new Timestamp(seconds, nanos);



        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                device_token =  SharedPreManager.getInstance(SignupActivity.this).getToken();
                Log.d(TAG, "onCreate: shared prefernce token " +  SharedPreManager.getInstance(SignupActivity.this).getToken());
            }
        };
        if(SharedPreManager.getInstance(SignupActivity.this).getToken() != null){
            device_token = SharedPreManager.getInstance(SignupActivity.this).getToken();
            Log.d(TAG, "onCreate: shared prefernce token main " +  SharedPreManager.getInstance(SignupActivity.this).getToken());
        }

//                            registerReceiver(broadcastReceiver,new IntentFilter(MyFirebaseMessagingService.TOKEN_BROADCAST));


        UploadImageFileToFirebaseStorage(avatar);
        //insert user data
        final User user = new User();
        user.setEmail(email);
        user.setU_id(FirebaseAuth.getInstance().getUid());
        user.setBio(getString(R.string.empty));
        user.setName(name);
        user.setCode(getString(R.string.empty));
        user.setUsername(username);
        user.setH_id(getString(R.string.empty));
        user.setN_id(getString(R.string.empty));
        user.setTimestamp(timestamp);
        user.setP_no(getString(R.string.empty));
        user.setDevice_token(device_token);


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_users))
                .document(user_id);

        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ((UserClient) (getApplicationContext())).setUser(user);
                    Log.d(TAG, "onComplete: data saved");
                    Toast.makeText(mContext, getString(R.string.Verification_code_send_to )+ "+" + mCode.getSelectedCountryCode() +mPhonenumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, verifyPhoneActivity.class);
                    intent.putExtra("phoneNumber","+" + mCode.getSelectedCountryCode() +mPhonenumber.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(mContext, "Verification code send to " + mPhonenumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, verifyPhoneActivity.class);
                    intent.putExtra("phoneNumber",mPhonenumber.getText().toString());
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onComplete: something went wrong");
                }
            }
        });
    }
    public void UploadImageFileToFirebaseStorage(String avatar) {

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


                // Creating second StorageReference.
                final StorageReference storageReferenceLocation = mStorageRef.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/public_image");

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

                                            mDb.collection(getString(R.string.collection_users)).document(user_id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Image saved");

                                                    } else {
                                                        finalIntent();
                                                    }
                                                }
                                            });
                                        }else{
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

            }

        }
    }

    //****************************************** finalintent *********************************
    private void finalIntent(){
        Toast.makeText(mContext, getString(R.string.Verification_code_send_to )+ "+" +mCode.getSelectedCountryCode() + mPhonenumber.getText().toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignupActivity.this, verifyPhoneActivity.class);
        intent.putExtra("phoneNumber","+" + mCode.getSelectedCountryCode() + mPhonenumber.getText().toString());
        startActivity(intent);
        finish();
        Log.d(TAG, "onComplete: something went wrong");
    }

}
