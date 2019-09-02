package com.intouchapp.intouch.Utills;

import android.os.Environment;

public class FilePaths {

    //*storage/emulated/0
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();


    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
    public String FIREBASE_HOUSE_IMAGE_STORAGE = "photos/houses/";
    public String FIREBASE_HOOD_IMAGE_STORAGE = "photos/hood/";
    public String FIREBASE_POST_IMAGE_STORAGE = "photos/post/";
    public String FIREBASE_EVENT_IMAGE_STORAGE = "photos/event/";
}
