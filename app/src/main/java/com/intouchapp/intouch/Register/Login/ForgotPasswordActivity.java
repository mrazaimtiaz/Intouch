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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Signup.ChooseHouseActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    //widgets
    EditText mEmail;
    TextView mHaveAnAccount;
    Button mReset;
    ImageView mBack;
    ConstraintLayout mMain,mProgressBar;

    //variable
    String email;
    Context mContext;

    //firebase
    FirebaseAuth auth;

    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //intialization
        mEmail = (EditText) findViewById(R.id.et_email);
        mHaveAnAccount = (TextView) findViewById(R.id.tv_haveanaccount);
        mReset = (Button) findViewById(R.id.btn_reset);
        mBack = (ImageView) findViewById(R.id.iv_back);

        mProgressBar = (ConstraintLayout) findViewById(R.id.constraintLayoutprogress);
        mMain = (ConstraintLayout) findViewById(R.id.constraintLayoutmain);

        mContext = ForgotPasswordActivity.this;

        auth = FirebaseAuth.getInstance();

        intentMethod();
        removeSpaceBar();

        forgotPassword();
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
        mEmail.setFilters(new InputFilter[] { filter });
    }

    //************************************************check error****************************************************
    public boolean checkError() {

        Log.d(TAG, "checkError: checking errors");

        email = mEmail.getText().toString();

        if ((checkStringEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher((email)).matches()))
        {

            if (!Patterns.EMAIL_ADDRESS.matcher((email)).matches()) {
                mEmail.setError("please enter a valid email");
                mEmail.requestFocus();

            }
            if (checkStringEmpty(email)) {
                mEmail.setError("email is required");
                mEmail.requestFocus();

            }

            return false;
        }
        return true;
    }

    public boolean checkStringEmpty(String string) {

        if (string.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //*************************************************** forgot password *****************************************
    private void forgotPassword(){

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkError()){
                    mMain.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(mEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(mContext, "Reset password email send to " + mEmail.getText().toString(), Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(mContext, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        mMain.setVisibility(View.VISIBLE);
                                        mProgressBar.setVisibility(View.GONE);
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
                                            Toast.makeText(mContext, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

}
