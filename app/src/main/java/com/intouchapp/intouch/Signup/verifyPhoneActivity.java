package com.intouchapp.intouch.Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class verifyPhoneActivity extends AppCompatActivity {

    String phonenumber;

    EditText mEnterCode;
    Button procced;

    TextView mChangeNumber;

    TextView reSend;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private String mVerificationId;

    PhoneAuthProvider.ForceResendingToken resendToken;

    private static final String TAG = "verifyPhoneActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        phonenumber = getIntent().getStringExtra("phoneNumber");
        Log.d(TAG, "onCreate: phone number is " + phonenumber);


        mEnterCode = (EditText) findViewById(R.id.et_code);
        mChangeNumber = (TextView) findViewById(R.id.chng_phone);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();


        sendVerificationCode(phonenumber);

        procced = (Button) findViewById(R.id.btn_proceed);
        reSend = (TextView) findViewById(R.id.resend);

        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + resendToken);
                if(resendToken != null){
                    resendVerificationCode(phonenumber,resendToken);
                }

            }
        });

        procced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEnterCode.getText().toString().isEmpty()){
                    mEnterCode.setError("Code cannot be empty");
                    mEnterCode.requestFocus();
                }else{
                    verifyVerificationCode(mEnterCode.getText().toString());
                }
            }
        });

        mChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(verifyPhoneActivity.this,AddPhoneNumberActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    private void sendVerificationCode(String no) {
        Log.d(TAG, "sendVerificationCode: sending code");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                no,
                1,
                TimeUnit.MINUTES,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);


    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                1,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            final String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            Log.d(TAG, "onVerificationCompleted: " + code);
            if (code != null) {
                mEnterCode.setText(code);

               verifyVerificationCode(code);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(verifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "onVerificationFailed: faliled" + e.getLocalizedMessage() + e.getCause() + e.getMessage());

        }


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            Log.d(TAG, "onCodeSent: code of user " + s);
            resendToken = forceResendingToken;

            Map<String,Object> code = new HashMap<>();

            code.put("code",mVerificationId);
            Log.d(TAG, "onCodeSent: " + resendToken);

            mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).update(code);



        }
    };

    private void verifyVerificationCode(final String code) {


            mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        User user = task.getResult().toObject(User.class);

                        if(user.getCode() != null){
                            if(!user.getCode().equals(getString(R.string.empty))){
                                try {
                                    Log.d(TAG, "verifyVerificationCode: " + user.getCode());
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(user.getCode(), code);

                                    Log.d(TAG, "verifyVerificationCode: " + credential);
                                    //signing the user
                                    signInWithPhoneAuthCredential(credential);
                                }catch (Exception e){
                                    Log.d(TAG, "verifyVerificationCode: " + e.getMessage());
                                    mEnterCode.setError("Wrong Code");
                                    mEnterCode.requestFocus();
                                }

                            }else{
                                try {
                                    if(mVerificationId != null){
                                        Log.d(TAG, "verifyVerificationCode: " + mVerificationId);
                                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                                        Log.d(TAG, "verifyVerificationCode: " + credential);
                                        //signing the user
                                        signInWithPhoneAuthCredential(credential);
                                    }

                                }catch (Exception e){
                                    Log.d(TAG, "verifyVerificationCode: " + e.getMessage());
                                    mEnterCode.setError("Wrong Code");
                                    mEnterCode.requestFocus();
                                }
                            }
                        }else{
                            try {
                                if(mVerificationId != null){
                                    Log.d(TAG, "verifyVerificationCode: " + mVerificationId);
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                                    Log.d(TAG, "verifyVerificationCode: " + credential);
                                    //signing the user
                                    signInWithPhoneAuthCredential(credential);
                                }

                            }catch (Exception e){
                                Log.d(TAG, "verifyVerificationCode: " + e.getMessage());
                                mEnterCode.setError("Wrong Code");
                                mEnterCode.requestFocus();
                            }
                        }
                    }
                }
            });
        try {
            Log.d(TAG, "verifyVerificationCode: " + mVerificationId);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            Log.d(TAG, "verifyVerificationCode: " + credential);
            //signing the user
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Log.d(TAG, "verifyVerificationCode: " + e.getMessage());
            mEnterCode.setError("Wrong Code");
            mEnterCode.requestFocus();
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: " + credential.getSmsCode());
                if(Objects.equals(credential.getSmsCode(), mEnterCode.getText().toString())){
                    Map<String,Object> phone = new HashMap<>();

                    phone.put("p_no",phonenumber);

                    mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).update(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(verifyPhoneActivity.this, ChooseHouseActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
                else{
                    mEnterCode.setError("Wrong Code");
                    mEnterCode.requestFocus();
                }
    }
}
