package com.intouchapp.intouch.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.intouchapp.intouch.R;

public class AddPhoneNumberActivity extends AppCompatActivity {

    EditText phoneNumber;

    Button mSendCode;

    CountryCodePicker mCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_number);

        phoneNumber = findViewById(R.id.et_phonenumber);

        mSendCode = findViewById(R.id.btn_sent);

        mCode = findViewById(R.id.countrycode);

        mCode.setAutoDetectedCountry(true);

        mSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneNumber.getText().toString();

                if(phone.isEmpty()){
                    phoneNumber.setError("Phonenumber cannot be empty");
                    phoneNumber.requestFocus();
                }
                else if(phone.length() < 6){
                    phoneNumber.setError("Minimum length 6");
                    phoneNumber.requestFocus();
                }
                else{
                    Toast.makeText(AddPhoneNumberActivity.this, getString(R.string.Verification_code_send_to )+ "+" + mCode.getSelectedCountryCode() + phoneNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPhoneNumberActivity.this, verifyPhoneActivity.class);
                    intent.putExtra("phoneNumber", "+" + mCode.getSelectedCountryCode() + phoneNumber.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
