package com.intouchapp.intouch.Signup;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.intouchapp.intouch.Main.Events.CreateEventActivity;
import com.intouchapp.intouch.Main.Events.SingleShareEventActivity;
import com.intouchapp.intouch.Main.Posts.PostPhotoActivity;
import com.intouchapp.intouch.Main.Posts.SingleSharePostActivity;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Main.Settings.AvatarActivity;
import com.intouchapp.intouch.Main.Settings.ChangeHoodInfoActivity;
import com.intouchapp.intouch.Main.Settings.ChangeHouseInfoActivity;
import com.intouchapp.intouch.Utills.FilePaths;
import com.intouchapp.intouch.Utills.FileSearch;
import com.intouchapp.intouch.Utills.GridImageAdapter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    //vars
    private ArrayList<String> directories;
    private Intent intent;

    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    private ArrayList<String> images;



    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ImageView shareClose;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    private String mAppend = "file:/";
    private String mSelectedImage;
    private String activityName;

    ArrayList<String> userInputs = new ArrayList<String>();


    private static final String TAG = "GalleryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        galleryImage = (ImageView) findViewById(R.id.galleryImageView);
        gridView = (GridView) findViewById(R.id.gridView);
        directorySpinner = (Spinner) findViewById(R.id.spinnerDirectory);
        shareClose = (ImageView) findViewById(R.id.ivCloseShare);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        images = getAllShownImagesPath(GalleryActivity.this);



        TextView nextScreen = (TextView) findViewById(R.id.tvNext);

        activityName =  getString(R.string.empty);
        intent = getIntent();
        if(intent.hasExtra(getString(R.string.user_inputs))){

            userInputs=  getIntent().getStringArrayListExtra(getString(R.string.user_inputs));
            Log.d(TAG, "onClick: data get from register"+userInputs.get(0));

        }

        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the galllery fragment ");
                finish();
            }
        });



        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityName = getIntent().getStringExtra(getString(R.string.activity_name));
                try {
                    if (activityName.equals(getString(R.string.SignupActivity_class))) {
                        Log.d(TAG, "onClick: activityName"  + activityName );
                        Intent intent = new Intent(GalleryActivity.this, SignupActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.NewHouseActivity_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName );
                        Intent intent = new Intent(GalleryActivity.this, NewHouseActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.PostPhotoActivity_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, PostPhotoActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.SingleSharePostActivity_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, SingleSharePostActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.CreateEventActivity_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, CreateEventActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.SingleShareEventActivity_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, SingleShareEventActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.AvatarActivity_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, AvatarActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        intent.putExtra(getString(R.string.intent_no_request),getIntent().getBooleanExtra(getString(R.string.intent_no_request),true));
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.ChangeHouseInfo_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, ChangeHouseInfoActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.intent_no_request),getIntent().getBooleanExtra(getString(R.string.intent_no_request),true));
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }else if(activityName.equals(getString(R.string.ChangeHoodInfo_class))){
                        Log.d(TAG, "onClick: activityName"  + activityName);
                        Log.d(TAG, "onClick: userinpit" + userInputs);
                        Intent intent = new Intent(GalleryActivity.this, ChangeHoodInfoActivity.class);
                        intent.putExtra(getString(R.string.user_inputs), userInputs);
                        intent.putExtra(getString(R.string.intent_no_request),getIntent().getBooleanExtra(getString(R.string.intent_no_request),true));
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }



                }catch (NullPointerException e){
                    Toast.makeText(GalleryActivity.this, "some thing went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        init();
    }

    private void init(){
        FilePaths filePaths = new FilePaths();


            Log.d(TAG, "init: 2" + filePaths.PICTURES);
            //check for either folder inside "/storage/emulated/0/pictures"
            if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
                directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
                Log.d(TAG, "init: direct" + directories);
            }



        ArrayList<String> directoriesName = new ArrayList<>();
        directoriesName.add("All Images");

        for(int i = 0; i < directories.size();i++){
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index).replace("/","");
            Log.d(TAG, "init: string "+ string);

            directoriesName.add(string);
        }




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GalleryActivity.this,android.R.layout.simple_spinner_item,
                directoriesName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    setupGridView("All Images");
                }
                else {
                    Log.d(TAG, "onItemSelected: selected: " + directories.get(position - 1));

                    //steup our image grid for the directory choosen
                    setupGridView(directories.get(position -1));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //**************************************************************************************************************************************************
    private void setupGridView(String selectedDirectory){
        if(selectedDirectory.equals("All Images")){

            int gridWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
            gridView.setColumnWidth(imageWidth);

            //use the grid adapter to adapter the image to gridview */
            GridImageAdapter mAdapter = new GridImageAdapter(GalleryActivity.this,R.layout.layout_grid_imageview,mAppend,images);

            gridView.setAdapter(mAdapter);

            //gridView.setAdapter(new GalleryActivity.ImageAdapter(this));


            //set the first image to displayed when the activity fragment view is inflated


            try{
                setImage(images.get(0),galleryImage,mAppend);
                mSelectedImage = images.get(0);
            }catch (IndexOutOfBoundsException e){
                Log.d(TAG, "setupGridView: IndexOutOfBoundsException " + e.getMessage());
            }


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: selected image " + images.get(position));

                    setImage(images.get(position),galleryImage,mAppend);
                    mSelectedImage = images.get(position);

                }
            });


        }
        else {


            Log.d(TAG, "setupGridView: directory choosen " + selectedDirectory);
            final ArrayList<String> imgURLs = FileSearch.getFilePath(selectedDirectory);


            //set the grid column width
            int gridWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
            gridView.setColumnWidth(imageWidth);


            //use the grid adapter to adapter the image to gridview */
            GridImageAdapter mAdapter = new GridImageAdapter(GalleryActivity.this, R.layout.layout_grid_imageview, mAppend, imgURLs);

            gridView.setAdapter(mAdapter);

            //gridView.setAdapter(new GalleryActivity.ImageAdapter(this));


            //set the first image to displayed when the activity fragment view is inflated


            try {
                setImage(imgURLs.get(0), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(0);
            } catch (IndexOutOfBoundsException e) {
                Log.d(TAG, "setupGridView: IndexOutOfBoundsException " + e.getMessage());
            }


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: selected image " + images.get(position));

                    setImage(imgURLs.get(position), galleryImage, mAppend);
                    mSelectedImage = imgURLs.get(position);

                }
            });
        }


    }

    private void setImage(String imgURL,ImageView image, String append){
        Log.d(TAG, "setImage: setting image ");

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(GalleryActivity.this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app


        ImageLoader.getInstance().init(config.build());

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);


            }
        });
    }
    /**
     * Getting All Images Path.
     *
     * @param activity
     *            the activity
     * @return ArrayList with images Path
     */
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        if(isStoragePermissionGranted()) {
            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
        }else {
            finish();
        }
        return listOfAllImages;
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

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

}
