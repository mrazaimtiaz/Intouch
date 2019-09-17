package com.intouchapp.intouch.Register.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.Introduction.IntroductionActivity;
import com.intouchapp.intouch.Register.SplashActivity;
import com.intouchapp.intouch.Signup.AddPhoneNumberActivity;
import com.intouchapp.intouch.Utills.SharedPreManager;
import com.intouchapp.intouch.Signup.ChooseHouseActivity;
import com.intouchapp.intouch.Signup.SignupActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //widget
    ImageView mBack;
    EditText mEmail, mPassword;
    Button mLogin;
    TextView mForgetPassword, mSignup;
    ConstraintLayout mMain,mProgress;


    //intialization
    private Context mcontext;
    String email, password;
    String device_token;

    //firebase
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";


    //***************************************** oncreate ***************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mBack = (ImageView) findViewById(R.id.iv_back);
        mEmail = (EditText) findViewById(R.id.et_mail);
        mPassword = (EditText) findViewById(R.id.et_password);

        mLogin = (Button) findViewById(R.id.btn_login);

        mForgetPassword = (TextView) findViewById(R.id.tv_forgetpass);
        mSignup = (TextView) findViewById(R.id.tv_movetosignup);

        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);
        mProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mcontext = LoginActivity.this;

        removeSpaceBar();
        intentMethod();
        clickSignin();

    }

    //***************************************** checkerror ***************************************************************
    public boolean checkError(){
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        if(checkStringEmpty(email)||checkStringEmpty(password)){
            if(checkStringEmpty(password)){
                mPassword.setError(getString(R.string.password_is_required));
                mPassword.requestFocus();
            }
            if(checkStringEmpty(email)){
                mEmail.setError(getString(R.string.email_is_required));
                mEmail.requestFocus();
            }
            return false;
        }

        return true;
    }

    //***************************************** check string is empty ***************************************************************
    public boolean checkStringEmpty(String string){

        if(string.isEmpty()){
            return true;
        }else {
            return false;
        }
    }


    //***************************************** sigin method ***************************************************************
    public void clickSignin(){
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin.isHovered();
                if(checkError()){
                    signin(email,password);
                }
            }
        });
    }

    //***************************************** check into firbase ***************************************************************
    public void signin(String email,String password){
        mProgress.setVisibility(View.VISIBLE);
        mMain.setVisibility(View.GONE);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");



                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){
                                        User user = task.getResult().toObject(User.class);

                                        if(user != null){
                                            if(user.getP_no().equals(getString(R.string.empty))){
                                                Intent intent = new Intent(mcontext, AddPhoneNumberActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if(user.getH_id().equals(getString(R.string.empty))){
                                                Intent intent = new Intent(LoginActivity.this, ChooseHouseActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }else{
                                            Intent intent = new Intent(LoginActivity.this,IntroductionActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                    if(task.isCanceled()){
                                        Intent intent = new Intent(LoginActivity.this,IntroductionActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });


                        } else {
                            mProgress.setVisibility(View.GONE);
                            mMain.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onComplete: exception " + task.getException().getMessage());
                            if(task.getException().getMessage().equals(getString(R.string.error_badly_formatted))){
                                mEmail.setError(getString(R.string.format_not_correct));
                                mEmail.requestFocus();
                            }
                            if(task.getException().getMessage().equals(getString(R.string.error_user_not_exist))){
                                mEmail.setError(getString(R.string.email_not_exist));
                                mEmail.requestFocus();
                            }
                            if(task.getException().getMessage().equals(getString(R.string.error_internet))){
                                Toast.makeText(mcontext, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                            }
                            if(task.getException().getMessage().equals(getString(R.string.error_password))){
                                mPassword.setError(getString(R.string.password_not_matched));
                                mPassword.requestFocus();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure" + task.getException().getMessage() + " " + R.string.error_badly_formatted, task.getException().getCause());


                        }
                    }
                });
    }
    //************************************************ checkIfEmailVerified ****************************************************************8
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user.isEmailVerified())
        {


            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        User user = task.getResult().toObject(User.class);

                        if(user!= null){
                            if(user.getH_id().equals(getString(R.string.empty))){
                                Intent intent = new Intent(LoginActivity.this, ChooseHouseActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Intent intent = new Intent(LoginActivity.this,IntroductionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                    }
                    if(task.isCanceled()){
                        Intent intent = new Intent(LoginActivity.this,IntroductionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }
            });

        }
        else{
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Log.d(TAG, "checkIfEmailVerified: email is not verifed");
            Toast.makeText(mcontext, "Email is not verified please verify your email", Toast.LENGTH_SHORT).show();
            mProgress.setVisibility(View.GONE);
            mMain.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signOut();

            //            //restart this activity

        }
    }


    //***********************************************intent method *********************************************
    private void intentMethod() {
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, SignupActivity.class);
                startActivity(intent);
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, IntroductionActivity.class);
                startActivity(intent);
               finish();
            }
        });

    }
    //*************************************remove space bar****************************************************
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
        mEmail.setFilters(new InputFilter[] { filter });
        mPassword.setFilters(new InputFilter[] { filter });
    }
}
