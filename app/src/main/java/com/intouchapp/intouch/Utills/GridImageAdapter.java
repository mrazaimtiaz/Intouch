package com.intouchapp.intouch.Utills;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.intouchapp.intouch.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflator;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;


    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;

    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    @SuppressLint("WrongViewCast")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        /*
        viewholder build pattern (Stailer to recyclerview)
         */
        if (convertView == null) {
            convertView = mInflator.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String imgURL = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(mAppend + imgURL, holder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View v) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(v.VISIBLE);

                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View v, FailReason failReason) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(v.GONE);

                }

            }

            @Override
            public void onLoadingComplete(String imageUri, View v, Bitmap loadedImage) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(v.GONE);

                }

            }

            @Override
            public void onLoadingCancelled(String imageUri, View v) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(v.GONE);

                }

            }
        });
        return convertView;
    }
}