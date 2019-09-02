package com.intouchapp.intouch.Utills;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {
    private static final String TAG = "FileSearch";

    /**
     * search directory and return the list of all directories
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        Log.d(TAG, "getDirectoryPaths: " + file.listFiles());
        try {
            for(int i = 0; i <listFiles.length; i++){
                if(listFiles[i].isDirectory()){
                    pathArray.add(listFiles[i].getAbsolutePath());
                }
            }
        }catch (NullPointerException e){
            Log.d(TAG, "getDirectoryPaths: NullPointerException " + e.getMessage());
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePath(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for(int i = 0; i <listFiles.length; i++){
            if(listFiles[i].isFile()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
