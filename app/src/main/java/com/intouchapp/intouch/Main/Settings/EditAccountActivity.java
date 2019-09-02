package com.intouchapp.intouch.Main.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.Login.LoginActivity;
import com.intouchapp.intouch.Utills.UserClient;

import java.util.Objects;

public class EditAccountActivity extends AppCompatActivity {

    //widget
    EditText mName,mBio,mUsername;
    TextView mChangePassword;

    TextView mUpdate;

    ImageView mBack;

    private User user;
    private Context mContext;

    private ConstraintLayout mMain,mProgress;

    FirebaseAuth auth;
    FirebaseFirestore mDb;

    private static final String TAG = "EditAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        //intialization
        mName = (EditText) findViewById(R.id.et_name);
        mBio = (EditText) findViewById(R.id.et_bio);
        mUsername = (EditText) findViewById(R.id.et_username);
        mUpdate = (TextView) findViewById(R.id.update);
        mChangePassword = (TextView) findViewById(R.id.tv_changepassword);

        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        mBack = (ImageView) findViewById(R.id.iv_back);
        mUpdate.setAlpha(0.5f);
        mUpdate.setEnabled(false);
        mContext = EditAccountActivity.this;

        user =  ((UserClient) (mContext.getApplicationContext())).getUser();

        auth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mName.setText(user.getName());
        mUsername.setText(user.getUsername());
        if(!user.getBio().equals(mContext.getString(R.string.empty))){
            mBio.setText(user.getBio());
        }

        intentMethod();
        changePassword();
        updateAccount();
    }
    //******************************************************** intent method ****************************************************
    private void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = mUsername.getText().toString();
                String bio = mBio.getText().toString();
                String name = mName.getText().toString();
                if((!bio.equals(user.getBio()) || !name.equals(user.getName()) ||  !username.equals(user.getUsername()) ) && !name.equals(getString(R.string.empty)) && name.matches("[a-zA-Z. ]*") && !(name.length() < 3) && !(name.length() > 16) && !username.equals(getString(R.string.empty)) && (username.matches("[a-zA-Z0-9. ]*") && !(username.length() < 3) && !(username.length() > 12))&& !bio.equals(getString(R.string.empty))&& !(bio.length() < 3) && !(bio.length() > 56)){
                    mUpdate.setAlpha(1f);
                    mUpdate.setEnabled(true);
                }else{
                    mUpdate.setAlpha(0.5f);
                    mUpdate.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = mUsername.getText().toString();
                String bio = mBio.getText().toString();
                String name = mName.getText().toString();

                if((!bio.equals(user.getBio()) || !name.equals(user.getName()) ||  !username.equals(user.getUsername()) ) && !name.equals(getString(R.string.empty)) && name.matches("[a-zA-Z. ]*") && !(name.length() < 3) && !(name.length() > 16) && !username.equals(getString(R.string.empty)) && (username.matches("[a-zA-Z0-9. ]*") && !(username.length() < 3) && !(username.length() > 12))&& !bio.equals(getString(R.string.empty))&& !(bio.length() < 3) && !(bio.length() > 56)){
                    mUpdate.setAlpha(1f);
                    mUpdate.setEnabled(true);
                }else{
                    mUpdate.setAlpha(0.5f);
                    mUpdate.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = mUsername.getText().toString();
                String bio = mBio.getText().toString();
                String name = mName.getText().toString();
                if((!bio.equals(user.getBio()) || !name.equals(user.getName()) ||  !username.equals(user.getUsername()) ) && !name.equals(getString(R.string.empty)) && name.matches("[a-zA-Z. ]*") && !(name.length() < 3) && !(name.length() > 16) && !username.equals(getString(R.string.empty)) && (username.matches("[a-zA-Z0-9. ]*") && !(username.length() < 3) && !(username.length() > 12))&& !bio.equals(getString(R.string.empty))&& !(bio.length() < 3) && !(bio.length() > 56)){
                    mUpdate.setAlpha(1f);
                    mUpdate.setEnabled(true);
                }else{
                    mUpdate.setAlpha(0.5f);
                    mUpdate.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    //************************************************ update account ********************************************************
    private void updateAccount(){
        mUpdate.setEnabled(false);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMain.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                user.setName(mName.getText().toString());
                user.setUsername(mUsername.getText().toString());
                user.setBio(mBio.getText().toString());

                UserProfileChangeRequest addHoodStatus = new UserProfileChangeRequest.Builder()
                        .setDisplayName( mName.getText().toString()).build();

                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateProfile(addHoodStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Display name: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                        }
                    }
                });
                mDb.collection(getString(R.string.collection_users)).document(user.getU_id()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                        }else{
                            mMain.setVisibility(View.VISIBLE);
                            mProgress.setVisibility(View.GONE);
                            Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                            mUpdate.setEnabled(true);

                        }
                    }
                });


            }
        });
    }
    //****************************************************change password **********************************************************
    private void changePassword(){
        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.change_password))
                        .setMessage(mContext.getString(R.string.dialog_password))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mMain.setVisibility(View.GONE);
                                mProgress.setVisibility(View.VISIBLE);
                                changePasswordThorughMail();
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

    //*********************************************** changePasswordThorughMail ***********************************************
    private void changePasswordThorughMail(){
        auth.sendPasswordResetEmail(user.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(mContext, "Reset password email send to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            mMain.setVisibility(View.VISIBLE);
                            mProgress.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: exception " + Objects.requireNonNull(task.getException()).getMessage());

                            if(task.getException().getMessage().equals(getString(R.string.error_internet))){
                                Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
