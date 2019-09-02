package com.intouchapp.intouch.Signup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Register.SplashActivity;
import com.intouchapp.intouch.Utills.SharedPreManager;
import com.intouchapp.intouch.Utills.MarkersClusterizer;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.intouchapp.intouch.Utills.Constants.ERROR_DIALOG_REQUEST;
import static com.intouchapp.intouch.Utills.Constants.MAPVIEW_BUNDLE_KEY;
import static com.intouchapp.intouch.Utills.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.intouchapp.intouch.Utills.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class ChooseHouseActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    //widgets
    private Button mOpenSettings, mEnableGPS, mTryAgain;
    private ConstraintLayout mMain, mInternet, mPermission, mGPS,mProgress;

    boolean openDialog;

    //variable
    private Context mContext;
    private Bitmap bitmapGreybg ;
    private String device_token;
    int height = 50;
    int width = 50;
    private static final int INTERVAL = 25;
    private float oldZoom = 0;

    //temp
    boolean temp;
    int count = 0;
    boolean create;

    //map
    private FusedLocationProviderClient mFusedLocationClient;
    private MapView mMapView;
    GoogleMap mMap;
    Location mLocation;
    private ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();

    Projection projection;
     double longitudeLeft;
     double longitudeRight;
     double latitudeLeft;
     double latitudeRight;
    private LinkedHashMap<Point, ArrayList<MarkerOptions>> clusters;

    //firebase
    private FirebaseFirestore mDb;

    private static final String TAG = "ChooseHouseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_house);
        mMapView = findViewById(R.id.map);

        Log.d(TAG, "onCreate: mapview " + mMapView);

        Log.d(TAG, "onCreate: locat" + mLocation);

        mMain = findViewById(R.id.constraintLayoutmain);
        mInternet = findViewById(R.id.constraintLayoutinternet);
        mPermission = findViewById(R.id.constraintLayoutpermission);
        mGPS = findViewById(R.id.constraintLayoutgps);
        mProgress = findViewById(R.id.constraintLayoutprogress);

        mOpenSettings = findViewById(R.id.btn_opensettings);
        mEnableGPS = findViewById(R.id.btn_gps);
        mTryAgain = findViewById(R.id.btn_tryagain);

        mDb = FirebaseFirestore.getInstance();
        mContext = ChooseHouseActivity.this;
        openDialog = true;
        temp = false;
        create = true;

        initGoogleMap(savedInstanceState);
        mMapView.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                User user = task.getResult().toObject(User.class);

                if(!user.getH_id().equals(getString(R.string.empty))){
                    Intent intent = new Intent(ChooseHouseActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        if(SharedPreManager.getInstance(ChooseHouseActivity.this).getToken() != null) {
            device_token = SharedPreManager.getInstance(ChooseHouseActivity.this).getToken();
            Map<String,Object> map = new HashMap<>();
            map.put(getString(R.string.device_token),device_token);
            try {
                mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }



        //permission methods
        settingBitmap();
        permissionCheck();
        openSettings();
        enableGPS();
        tryAgain();

    }

    //********************************************* setting bitmap ***********************************************
    private void settingBitmap(){
        bitmapGreybg = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.grey_bgpng);
        bitmapGreybg = Bitmap.createScaledBitmap(bitmapGreybg, width, height, false);
        bitmapGreybg = getCroppedBitmap(bitmapGreybg);
        bitmapGreybg = addBorderToCircularBitmap(bitmapGreybg,2, Color.BLACK);
    }

    //*********************************************** map implementation *****************************************
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style_json));

    }
    //*****************************************************  map content ******************************************
    private void mapContent(){

        mProgress.setVisibility(View.GONE);
        mMain.setVisibility(View.VISIBLE);

        //getting boundary
        projection = mMap.getProjection();
        longitudeLeft = projection.getVisibleRegion().farLeft.longitude;
        longitudeRight = projection.getVisibleRegion().farRight.longitude;
        latitudeLeft = projection.getVisibleRegion().nearLeft.latitude;
        latitudeRight = projection.getVisibleRegion().farRight.latitude;

        mMap.setMinZoomPreference(5f);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setOnCameraIdleListener(this);

        Log.d(TAG, "mapContent: longitudeLeft " + longitudeLeft + " longitudeRight "+ longitudeRight + " latitudeLeft " + latitudeLeft + " latitudeRight " + latitudeRight);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        Toast.makeText(mContext, getString(R.string.toast_zoom), Toast.LENGTH_SHORT).show();

        new BackgroundDatabase().execute();


    }
    private boolean checkDatabase(){
        Log.d(TAG, "checkDatabase: called");
        try {
            mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "onComplete: called 2");
                    if(task.isSuccessful()) {
                        Log.d(TAG, "onComplete: called 3");
                        final List<DocumentSnapshot> hood_list;

                        hood_list = task.getResult().getDocuments();
                        Log.d(TAG, "onComplete: " + hood_list);
                        for (int j = 0; j < hood_list.size(); j++) {

                            Log.d(TAG, "onComplete: hood_list " + hood_list);



                            new BackgroundHood(hood_list.get(j).getId(),hood_list.size(),j).execute();


                        }
                    }
                    if(task.getResult().size() == 0) {
                        createHouseMarker(mLocation);
                    }
                }
            });

            Log.d(TAG, "checkDatabase: value of create " + create);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return create;
    }

    private boolean checkHood(String id){
        Log.d(TAG, "checkHood: called");
        try {
            Tasks.await(mDb.collection(getString(R.string.collection_hoods)).document(id).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "checkHood: called 1");
                    if(task.isSuccessful()){
                        Log.d(TAG, "checkHood: called 2");

                        final List<DocumentSnapshot> house_list;
                        house_list = task.getResult().getDocuments();
                        for (int i = 0; i < house_list.size(); i++) {

                            House house = house_list.get(i).toObject(House.class);

                            Log.d(TAG, "onComplete: getName " + house.getName());

                            new DownloadFileFromURL(bitmapGreybg,house.getImage(),house.getLocation().getLatitude(),house.getLocation().getLongitude(),house_list.get(i).getId(),house.getN_id()).execute();
                            Log.d(TAG, "onComplete: current location " + mLocation.getLatitude() + " " + mLocation.getLongitude());

                            Log.d(TAG, "onComplete: string" + String.valueOf(mLocation.getLatitude()).substring(0, 6) + " " + String.valueOf(house.getLocation().getLatitude()).substring(0, 6) + " "  + String.valueOf(mLocation.getLongitude()).substring(0, 6)  + " " + String.valueOf(house.getLocation().getLongitude()).substring(0, 6));

                            if((String.valueOf(mLocation.getLatitude()).substring(0, 6).equals(String.valueOf(house.getLocation().getLatitude()).substring(0, 6)) && String.valueOf(mLocation.getLongitude()).substring(0, 6).equals(String.valueOf(house.getLocation().getLongitude()).substring(0, 6)))){

                                Log.d(TAG, "onComplete: location matched");

                                Log.d(TAG, "onComplete: int values" + Integer.parseInt(String.valueOf(mLocation.getLongitude()).substring(5,8)) + " " +  Integer.parseInt(String.valueOf(house.getLocation().getLongitude()).substring(5,8)) + " " +  Integer.parseInt(String.valueOf(mLocation.getLatitude()).substring(5,8)) + " " + Integer.parseInt(String.valueOf(house.getLocation().getLatitude()).substring(5,8)));

                                if(Math.abs(Integer.parseInt(String.valueOf(mLocation.getLatitude()).substring(5,8)) - Integer.parseInt(String.valueOf(house.getLocation().getLatitude()).substring(5,8))) < 14 && Math.abs(Integer.parseInt(String.valueOf(mLocation.getLongitude()).substring(5,8)) - Integer.parseInt(String.valueOf(house.getLocation().getLongitude()).substring(5,8))) < 14){
                                    openDialog = false;
                                    create = false;
                                    Log.d(TAG, "onComplete: condition matched value of create " + create);
                                    Log.d(TAG, "onComplete: house cant be created" + (Integer.parseInt(String.valueOf(mLocation.getLatitude()).substring(5,8)) - Integer.parseInt(String.valueOf(house.getLocation().getLatitude()).substring(5,8))) + " " + (Integer.parseInt(String.valueOf(mLocation.getLongitude()).substring(5,8)) - Integer.parseInt(String.valueOf(house.getLocation().getLongitude()).substring(5,8))));
                                }
                            }

                        }
                    }else {
                        Log.d(TAG, "exception " +  task.getException().getMessage() );
                    }
                }
            }));
        } catch (ExecutionException e) {
            Log.d(TAG, "checkHood: ExecutionException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d(TAG, "checkHood: InterruptedException");
            e.printStackTrace();
        }catch (NullPointerException e){
            finish();
            e.printStackTrace();
        }
        Log.d(TAG, "checkHood: value of create " + create);
        return  create;

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

    //********************************************** create house marker *********************************************
    private void createHouseMarker(Location location) {
        Log.d(TAG, "createHouseMarker: called");

        try {
            LatLng houseLocation = new LatLng(location.getLatitude(), location.getLongitude());
            final MarkerOptions houseMarker = new MarkerOptions()
                    .snippet(getString(R.string.new_house))
                    .position(houseLocation)
                    .icon(bitmapDescriptorFromVector(mContext))
                    .draggable(false);
            markers.add(houseMarker);
            mMap.addMarker(houseMarker).showInfoWindow();

            mMap.setOnMarkerClickListener(ChooseHouseActivity.this);
        }catch (NullPointerException e){
            e.printStackTrace();
        }





    }
    //
    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getSnippet().equals(getString(R.string.new_house))){
            Intent intent = new Intent(ChooseHouseActivity.this,NewHouseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble(getString(R.string.latitudeLeft),latitudeLeft);
            bundle.putDouble(getString(R.string.latitudeRight),latitudeRight);
            bundle.putDouble(getString(R.string.longitudeLeft),longitudeLeft);
            bundle.putDouble(getString(R.string.longitudeRight),longitudeRight);
            bundle.putDouble(getString(R.string.currentLatitude),mLocation.getLatitude());
            bundle.putDouble(getString(R.string.currentLongititude),mLocation.getLongitude());

            intent.putExtras(bundle);

            startActivity(intent);
        }else {
            Intent intent = new Intent(ChooseHouseActivity.this,HouseInfoActivity.class);
            intent.putExtra(getString(R.string.house_id),marker.getSnippet());
            Log.d(TAG, "onMarkerClick: " + marker.getSnippet() + marker.getTitle());
            startActivity(intent);
        }
        return true;
    }

    //************************************************ marker for home *****************************************************
    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
            Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_placeholder);
        Bitmap bitmap = null;
        try {
            background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight() + 0);
            bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            background.draw(canvas);
            background.draw(canvas);
        }catch (NullPointerException e){
            e.printStackTrace();
        }



        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //***************************************** initalizing googlemap *******************************************************
    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

    }
    private void moveToMain(){
        try {
            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    if(!user.getH_id().equals(getString(R.string.empty))){
                        Intent intent = new Intent(mContext,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //************************************************* life cycles to handle map **********************************************
    @Override
    protected void onResume() {
        moveToMain();
        Log.d(TAG, "onResume: value of temp " + temp);
        super.onResume();
        if(mLocation == null && temp)  {
            Log.d(TAG, "onResume: null");
            permissionCheck();
        }
        temp = true;
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();

    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();

    }


    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();

    }

    //************************************************ get last location of user after checking permission **********************
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: started");
        mProgress.setVisibility(View.VISIBLE);

                if (ActivityCompat.checkSelfPermission(ChooseHouseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {@Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {

                        mLocation = task.getResult();
                        if(mLocation != null){
                            mMap.getUiSettings().setAllGesturesEnabled(false);
                            Log.d(TAG, "onComplete: " + mLocation);
                            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                            Log.d(TAG, "onComplete: latitude: " + mLocation.getLatitude());
                            Log.d(TAG, "onComplete: longitude: " + mLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                            mapContent();
                        }else {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "getLastKnownLocation: called.");
                                    if (ActivityCompat.checkSelfPermission(ChooseHouseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {@Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isSuccessful()) {

                                            mLocation = task.getResult();
                                            if(mLocation != null){

                                                Log.d(TAG, "onComplete: " + mLocation);
                                                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                                                Log.d(TAG, "onComplete: latitude: " + mLocation.getLatitude());
                                                Log.d(TAG, "onComplete: longitude: " + mLocation.getLongitude());
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                                                mapContent();
                                            }
                                        }else {
                                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                            Toast.makeText(mContext, getString(R.string.toast_restart_gps), Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }
                                    });
                                }
                            }, 2500);

                        }
                    }else{
                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        Toast.makeText(mContext, getString(R.string.toast_restart_gps), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                });



    }
    //************************************************* permissioncheck **********************************************************
    private void permissionCheck(){

        if(getLocationPermission()){
            if(getGpsStatus()){
                if(isServicesOK()){
                    mInternet.setVisibility(View.GONE);
                    mGPS.setVisibility(View.GONE);
                    mProgress.setVisibility(View.VISIBLE);
                    mPermission.setVisibility(View.GONE);
                    getLastKnownLocation();
                }

            }else{
                mInternet.setVisibility(View.GONE);
                mGPS.setVisibility(View.VISIBLE);
                mMain.setVisibility(View.INVISIBLE);
                mPermission.setVisibility(View.GONE);
                mProgress.setVisibility(View.GONE);
            }

        }else{
            mGPS.setVisibility(View.GONE);
            mInternet.setVisibility(View.GONE);
            mMain.setVisibility(View.INVISIBLE);
            mPermission.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }

    }

    //************************************************** get services status *******************************************************
    public boolean isServicesOK(){

        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable( mContext);
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ChooseHouseActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();

        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            finish();
        }
        return false;

    }

    //************************************************** get permission status **********************************************
    private boolean getLocationPermission() {
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else{
            if(count == 0) {
                count = 1;
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return false;
            }else {
                mInternet.setVisibility(View.GONE);
                mMain.setVisibility(View.INVISIBLE);
                mGPS.setVisibility(View.GONE);
                mPermission.setVisibility(View.VISIBLE);
                return false;
            }
        }
    }


    //************************************************** getLocationPermission result ************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionCheck();
            }
        }
    }


    //*********************************************** get gps status ************************************************************
    private boolean getGpsStatus(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Log.d(TAG, "getGpsStatus: gps disabled");
            return false;
        }
        else {
            Log.d(TAG, "getGpsStatus: gps enabled");
            return true;
        }
    }


    //******************************************** open settings ******************************************************
    private void openSettings(){
        Log.d(TAG, "openSettings: called");
        mOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button called");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }
    //******************************************** enable gps ******************************************************
    private void enableGPS(){
        Log.d(TAG, "enableGPS: called");
        mEnableGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button called");
                Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
            }
        });
    }
    //******************************************** try again ******************************************************
    private void tryAgain(){
        mTryAgain.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChooseHouseActivity.this,
                        ChooseHouseActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onCameraIdle() {
        if (mMap.getCameraPosition().zoom != oldZoom) {

            try {

                clusters = MarkersClusterizer.clusterMarkers(mMap, markers, INTERVAL);

            } catch (ExecutionException e) {

                e.printStackTrace();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

        oldZoom = mMap.getCameraPosition().zoom;
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<Void, String, Bitmap> {

        Bitmap greyBg;
        double latitude,longitude;
        Bitmap bmp = null;
        URL url = null;
        private String h_id;
        private String n_id;
        String image;
        MarkerOptions houseMarker;




        DownloadFileFromURL(Bitmap greyBg, String image, double latitude, double longitude, String h_id, String n_id){
            this.greyBg = greyBg;
            this.image = image;
            this.h_id = h_id;
            this.n_id = n_id;
            this.latitude = latitude;
            this.longitude = longitude;


        }


        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "createHouseMarker: called");
            LatLng houseLocation = new LatLng(latitude,longitude);

            houseMarker = new MarkerOptions()
                    .snippet(h_id + n_id)
                    .title("")
                    .position(houseLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(greyBg))
                    .draggable(false);

            markers.add(houseMarker);

            Log.d(TAG, "onPostExecute: " + bmp);

            mMap.addMarker(houseMarker);
            mMap.setOnMarkerClickListener(ChooseHouseActivity.this);
            super.onPreExecute();

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Bitmap doInBackground(Void... myBundle) {

            try {

                url = new URL(image);
                URLConnection conection = url.openConnection();
                conection.connect();

                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                bmp = Bitmap.createScaledBitmap(bmp, width, height, false);
                bmp = getCroppedBitmap(bmp);
                bmp = addBorderToCircularBitmap(bmp,2, Color.BLACK);


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return bmp;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            Log.d(TAG, "onPostExecute: "+ bmp);
            if(bmp != null){
                houseMarker.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                mMap.addMarker(houseMarker);

            }



        }

    }






     @SuppressLint("StaticFieldLeak")
     class BackgroundDatabase extends AsyncTask<Void, String, Boolean> {


        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        BackgroundDatabase(){

        }

         @Override
        protected void onPreExecute() {


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Boolean doInBackground(Void... myBundle) {
            Boolean createHouse = checkDatabase();

            Log.d(TAG, "doInBackground: value of create house" + createHouse);
            return createHouse;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Boolean createHouse) {


        }

    }


    class BackgroundHood extends AsyncTask<Void, String, Void> {

        String id;
        int size;
        int i;

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        public BackgroundHood(String id,int size,int i){
            this.id = id;
            this.size = size;
            this.i = i;
        }
        @Override
        protected void onPreExecute() {


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Void doInBackground(Void... myBundle) {
            boolean check = checkHood(id);
            Log.d(TAG, "doInBackground: check value of create house" + check);
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void createHouse) {
            Log.d(TAG, "onPostExecute: " + i + size);
            if(i == size -1){

                if(create){
                    Log.d(TAG, "post execute check value of create house" + createHouse + create);
                    createHouseMarker(mLocation);
                }
            }
        }

    }






}
