package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Main.Home.ComplimentsActivity;
import com.intouchapp.intouch.Main.Home.HoodInfoActivity;
import com.intouchapp.intouch.Main.Home.MainHouseInfoActivity;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreRecyclerAdapter extends RecyclerView.Adapter<ExploreRecyclerAdapter.MyOwnHolder> implements GoogleMap.OnMarkerClickListener {




    //variable
    private static final String TAG = "HomeFragment";

    private Bitmap bitmapGreybg ;

    int blue;
    int green;
    int red;

    int height = 50;
    int width = 50;

    //map
    private MapView mMapView;
    GoogleMap mMap;
    Location mLocation;

    List<String> rel;
    List<String> fri;
    List<Hood> hoods = new ArrayList<>();;
    List<List<House>> houses = new ArrayList<>();;
    Context mContext;

    //Firebase
    private FirebaseFirestore mDb;


    public ExploreRecyclerAdapter(List<String> rel,List<String> fri,List<Hood> hoods,List<List<House>> houses,Context mContext){
        Log.d(TAG, "MemberRecyclerViewAdapter: constructor called");

        mDb = FirebaseFirestore.getInstance();
        this.rel = rel;
        this.fri = fri;
        this.hoods = hoods;
        this.houses = houses;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(mContext);
        View myOwnInflator = myInflator.inflate(R.layout.recyclerview_explore,parent,false);
        return new MyOwnHolder(myOwnInflator);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyOwnHolder holder, final int position) {

        try {
            mDb.collection(mContext.getString(R.string.collection_hoods)).document(houses.get(position).get(0).getN_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                    assert hood != null;
                    if(hood.getFallower() != null){
                        if (hood.getFallower().contains(FirebaseAuth.getInstance().getUid())){
                            holder.mFallow.setText(mContext.getString(R.string.fallowing));
                        }
                    }
                    holder.mHoodName.setText(hood.getName());
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.mHoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HoodInfoActivity.class);
                intent.putExtra(mContext.getString(R.string.n_id),houses.get(position).get(0).getN_id());
                mContext.startActivity(intent);
            }
        });
        holder.mCompliment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComplimentsActivity.class);
                intent.putExtra(mContext.getString(R.string.n_id),houses.get(position).get(0).getN_id());
                mContext.startActivity(intent);
            }
        });

        holder.mFallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(holder.mFallow.getText().equals(mContext.getString(R.string.fallow))){
                        final List<String> id = new ArrayList<>();
                        holder.mFallow.setText(mContext.getString(R.string.fallowing));
                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(houses.get(position).get(0).getN_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                                assert hood != null;
                                if(hood.getFallower() != null){
                                    if(hood.getFallower().size() != 0){
                                        hood.getFallower().add(FirebaseAuth.getInstance().getUid());
                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(houses.get(position).get(0).getN_id()).set(hood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }else {
                                        id.add(FirebaseAuth.getInstance().getUid());
                                        hood.setFallower(id);
                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(houses.get(position).get(0).getN_id()).set(hood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                }else {
                                    id.add(FirebaseAuth.getInstance().getUid());
                                    hood.setFallower(id);
                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(houses.get(position).get(0).getN_id()).set(hood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });


    }
    private void init(int position){
        if(position !=-1) {
            LatLng latLng;
            for (int i = 0; i <hoods.size(); i++){

                if(hoods.get(i).getId().equals(houses.get(position).get(0).getN_id())){

                    Log.d(TAG, "onMapReady: "+ hoods.get(i).getId() + houses.get(position).get(0).getN_id());
                    final double longitudeLeft = hoods.get(i).getLongitude().getLatitude();
                    final double longitudeRight = hoods.get(i).getLongitude().getLongitude();
                    final double latitudeLeft = hoods.get(i).getLatitude().getLatitude();
                    final double latitudeRight = hoods.get(i).getLatitude().getLongitude();

                    double midpoint1 = (latitudeRight + latitudeLeft) / 2;
                    double midpoint2 = (longitudeRight + longitudeLeft) / 2;

                    Log.d(TAG, "onComplete: midpoint2 " + midpoint2 + " midpoint1 " + midpoint1);

                    latLng = new LatLng(midpoint1, midpoint2);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));

                }
            }

            bitmapGreybg = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.grey_bgpng);
            bitmapGreybg = Bitmap.createScaledBitmap(bitmapGreybg, width, height, false);
            bitmapGreybg = getCroppedBitmap(bitmapGreybg);
            bitmapGreybg = addBorderToCircularBitmap(bitmapGreybg,2, Color.BLACK);

            mMap.setMinZoomPreference(18f);
            mMap.getUiSettings().setScrollGesturesEnabled(false);

            for(int i = 0; i < houses.get(position).size(); i++){


                Log.d(TAG, "onMapReady: " + position + i);


                if(rel.contains(houses.get(position).get(i).getH_id()) ){


                    MarkerOptions houseMarker = new MarkerOptions()
                            .snippet(houses.get(position).get(i).getH_id()  + houses.get(position).get(i).getN_id()  + mContext.getString(R.string.type_relative))
                            .position(new LatLng(houses.get(position).get(i).getLocation().getLatitude(),houses.get(position).get(i).getLocation().getLongitude()))
                            .icon(redHouse(mContext))
                            .draggable(false);

                    mMap.addMarker(houseMarker);
                    Log.d(TAG, "onMapReady: n)id " + houses.get(position).get(i).getN_id() + houses.get(position).get(i).getLocation().getLongitude());

                }else if(fri.contains(houses.get(position).get(i).getH_id())){
                    MarkerOptions houseMarker = new MarkerOptions()
                            .snippet(houses.get(position).get(i).getH_id()  + houses.get(position).get(i).getN_id()  + mContext.getString(R.string.type_friend))
                            .position(new LatLng(houses.get(position).get(i).getLocation().getLatitude(),houses.get(position).get(i).getLocation().getLongitude()))
                            .icon(blueHouse(mContext))
                            .draggable(false);

                    mMap.addMarker(houseMarker);
                    Log.d(TAG, "onMapReady: n)id " + houses.get(position).get(i).getN_id() + houses.get(position).get(i).getLocation().getLongitude());

                }else{

                    MarkerOptions houseMarker = new MarkerOptions()
                            .snippet(houses.get(position).get(i).getH_id()  + houses.get(position).get(i).getN_id()  + mContext.getString(R.string.type_other))
                            .position(new LatLng(houses.get(position).get(i).getLocation().getLatitude(),houses.get(position).get(i).getLocation().getLongitude()))
                            .icon(blackHouse(mContext))
                            .draggable(false);

                    mMap.addMarker(houseMarker);
                    Log.d(TAG, "onMapReady: n)id " + houses.get(position).get(i).getN_id() + houses.get(position).get(i).getLocation().getLongitude());

                }
                mMap.setOnMarkerClickListener(this);


            }
        }


    }
    @Override
    public int getItemCount() {
        return hoods.size();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
        intent.putExtra(mContext.getString(R.string.house_id),marker.getSnippet());
        Log.d(TAG, "onMarkerClick: " + marker.getSnippet() + marker.getTitle());
        mContext.startActivity(intent);

        return true;
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

        //widget
        TextView mCompliment;
        TextView mHoodName;
        TextView mFallow;
        MapView mMapView;

        LatLng latLng;


        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);

            mCompliment = (TextView) itemView.findViewById(R.id.tv_compliments);
            mHoodName = (TextView) itemView.findViewById(R.id.tv_user_name);
            mFallow = (TextView) itemView.findViewById(R.id.tv_fallow);
            mMapView = (MapView) itemView.findViewById(R.id.map);
            if (mMapView != null)
            {
                mMapView.onCreate(null);
                mMapView.onResume();
                mMapView.getMapAsync(this);
            }



        }


        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    mContext, R.raw.map_style_json));

            init(getAdapterPosition());

            // new FindFriendsRelatives(getAdapterPosition(),latLng).execute();


        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
            intent.putExtra(mContext.getString(R.string.house_id),marker.getSnippet());
            Log.d(TAG, "onMarkerClick: " + marker.getSnippet() + marker.getTitle());
            mContext.startActivity(intent);

            return true;
        }
    }


    //************************************************* bitmap methods******************************************************************
    private Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor){
        // Calculate the circular bitmap width with border
        int dstBitmapWidth = srcBitmap.getWidth()+borderWidth*2;

        // Initialize a new Bitmap to make it bordered circular bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth,dstBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);
        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        // Initialize a new Paint instance to draw border
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        /*
            public void drawCircle (float cx, float cy, float radius, Paint paint)
                Draw the specified circle using the specified paint. If radius is <= 0, then nothing
                will be drawn. The circle will be filled or framed based on the Style in the paint.

            Parameters
                cx : The x-coordinate of the center of the cirle to be drawn
                cy : The y-coordinate of the center of the cirle to be drawn
                radius : The radius of the cirle to be drawn
                paint : The paint used to draw the circle
        */
        // Draw the circular border around circular bitmap
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getWidth() / 2, // cy
                canvas.getWidth()/2 - borderWidth / 2, // Radius
                paint // Paint
        );

        // Free the native object associated with this bitmap.
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    //************************************************ marker for home *****************************************************
    private BitmapDescriptor blackHouse(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_house);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight() + 0);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //************************************************ marker for home *****************************************************
    private BitmapDescriptor blueHouse(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_blue_house);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight() + 0);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    //************************************************ marker for home *****************************************************
    private BitmapDescriptor redHouse(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_red_house);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight() + 0);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




}

