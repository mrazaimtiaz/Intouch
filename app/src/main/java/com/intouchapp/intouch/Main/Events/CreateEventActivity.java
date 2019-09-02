package com.intouchapp.intouch.Main.Events;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.Posts.PostPhotoActivity;
import com.intouchapp.intouch.Main.Posts.ShareWithActivity;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Signup.GalleryActivity;
import com.intouchapp.intouch.Utills.NothingSelectedSpinnerAdapter;
import com.intouchapp.intouch.Utills.UniversalImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CreateEventActivity extends AppCompatActivity {

    //widget
    private EditText mWrite,mPlace;
    private Spinner mCategory;
    private TextView mDate,mTime;
    private ImageView mImage;

    private ImageView mBack;
    private TextView next;

    private String image,write,category,date,time,place;

    private static final String TAG = "CreateEventActivity";

    private DatePickerDialog.OnDateSetListener mDateSetListener;


    //variable
    private Context mContext;
    private String activityName;

    private Intent intent;
    private String mAppend = "file://";


    //temp
    private  int count = 1;

    //firebase
    FirebaseFirestore mDb;

    ArrayList<String> userInputs = new ArrayList<String>();
    ArrayList<String> mIntent = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mBack = (ImageView) findViewById(R.id.iv_back);
        next = (TextView) findViewById(R.id.tv_next);
        mImage = (ImageView) findViewById(R.id.iv_post_image);
        mWrite = (EditText) findViewById(R.id.et_saysomething);
        mPlace = (EditText) findViewById(R.id.et_write_place);


        mCategory = (Spinner) findViewById(R.id.btn_category);
        mTime = (TextView) findViewById(R.id.btn_time);
        mDate = (TextView) findViewById(R.id.btn_date);

        mContext = CreateEventActivity.this;

        image = getString(R.string.empty);
        category = getString(R.string.empty);

        mDb = FirebaseFirestore.getInstance();

        activityName = getString(R.string.CreateEventActivity_class);

        setCategory();
        setImage();
        postImage();
        getDate();
        getTime();


        checkError();
        onTextChange();
        clickNext();

    }
    private void clickNext(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkError()){
                    Intent intent = new Intent(mContext, InvitationActivity.class);
                    mIntent.add(mWrite.getText().toString());
                    mIntent.add(mPlace.getText().toString());
                    mIntent.add(mCategory.getSelectedItem().toString());
                    mIntent.add(mDate.getText().toString());
                    mIntent.add(mTime.getText().toString());
                    mIntent.add(image);
                    Log.d(TAG, "onClick: mIntent " + mIntent);
                    intent.putExtra(getString(R.string.intent_photo),mIntent);
                    startActivity(intent);
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

        mPlace.addTextChangedListener(new TextWatcher() {
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
                    String positionitem = String.valueOf(mCategory.getSelectedItemPosition());
                    userInputs.add(mWrite.getText().toString());
                    userInputs.add(mPlace.getText().toString());
                    userInputs.add(positionitem);
                    userInputs.add(mDate.getText().toString());
                    userInputs.add(mTime.getText().toString());


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
            mPlace.setText(fetchList.get(1));
            mCategory.setSelection(Integer.parseInt(fetchList.get(2)));
            mDate.setText(fetchList.get(3));
            mTime.setText(fetchList.get(4));


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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean showRationale = shouldShowRequestPermissionRationale( permissions[0]);

        Log.d(TAG, "onRequestPermissionsResult: " + count);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: granted " + count);
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

            count = 0;

            //resume tasks needing this permission
            String positionitem = String.valueOf(mCategory.getSelectedItemPosition());
            userInputs.add(mWrite.getText().toString());
            userInputs.add(mPlace.getText().toString());
            userInputs.add(positionitem);
            userInputs.add(mDate.getText().toString());
            userInputs.add(mTime.getText().toString());

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

    public void setCategory(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_events, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mCategory.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));

        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkError();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void getDate() {
        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year, month, day);

                Field mDatePickerField;
                try {
                    mDatePickerField = dialog.getClass().getDeclaredField("mDatePicker");
                    mDatePickerField.setAccessible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.getDatePicker().updateDate(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);


                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

            }

        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                mDate.setText(date);
                mDate.setError(null);
                checkError();
            }

        };


    }
    public void getTime(){
        mTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTime.setText( selectedHour + ":" + selectedMinute);
                        checkError();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.selecttime));
                mTimePicker.show();

            }
        });
    }

    //************************************************check error****************************************************
    public boolean checkError() {

        Log.d(TAG, "checkError: checking errors");

        write = mWrite.getText().toString();
        place = mPlace.getText().toString();
        category =(String) mCategory.getSelectedItem();
        date = mDate.getText().toString();
        time = mTime.getText().toString();

        if (!write.isEmpty() && !place.isEmpty() && !image.equals(getString(R.string.empty)) && category != null && !date.equals(getString(R.string.string_date)) && !time.equals(getString(R.string.string_time)))
        {
            next.setAlpha(1f);
            next.setEnabled(true);
            return true;
        }else {

            next.setAlpha(0.5f);
            next.setEnabled(false);
            return false;
        }

    }

}