package com.intouchapp.intouch.Main.Home;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.intouchapp.intouch.Utills.Constants.MAPVIEW_BUNDLE_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    //widget
    private ConstraintLayout mMain,mProgress,mInternet;

    //variable
    private static final String TAG = "HomeFragment";
    List<String> rel = new ArrayList<>();
    List<String> fri = new ArrayList<>();
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

    //Firebase
    private FirebaseFirestore mDb;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_home, container, false);

       mMain = v.findViewById(R.id.constraintLayoutmain);
       mProgress = v.findViewById(R.id.constraintLayoutprogress);
       mInternet = v.findViewById(R.id.constraintLayoutinternet);
       mMapView = (MapView) v.findViewById(R.id.map);

       mDb = FirebaseFirestore.getInstance();

       mProgress.setVisibility(View.VISIBLE);
       mMain.setVisibility(View.GONE);

       settingBitmap();
       initGoogleMap(savedInstanceState);
       mMapView.getMapAsync(this);

       try {
           if(isAdded()){
               blue = ContextCompat.getColor(Objects.requireNonNull(getActivity()),R.color.blue);
               green = ContextCompat.getColor(getActivity(),R.color.green);
               red = ContextCompat.getColor(getActivity(),R.color.red);
           }
       }catch (NullPointerException e){
           e.printStackTrace();
       }

       return v;
    }
    //********************************************* setting bitmap ***********************************************
    private void settingBitmap(){
        try {
            if(isAdded()){
                bitmapGreybg = BitmapFactory.decodeResource(Objects.requireNonNull(getActivity()).getResources(),
                        R.drawable.grey_bgpng);
                bitmapGreybg = Bitmap.createScaledBitmap(bitmapGreybg, width, height, false);
                bitmapGreybg = getCroppedBitmap(bitmapGreybg);
                bitmapGreybg = addBorderToCircularBitmap(bitmapGreybg,2, Color.BLACK);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
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

    //***************************************** on map ready ************************************************************
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
      try {
            if(isAdded()){
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                        getActivity(), R.raw.map_style_json));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        findLocation();

    }
    //************************************************* life cycles to handle map **********************************************
    @Override
    public void onResume() {
        super.onResume();
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
    //****************************************** add Markers ***********************************************************
    private void addMarkers(){
        Log.d(TAG, "check add marker called");
        try {
            if(isAdded()){
                final User user = ((UserClient) (Objects.requireNonNull(getActivity()).getApplicationContext())).getUser();
                if(user != null){
                    Log.d(TAG, "check user is not null" );
                    if(isAdded()){
                        mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                boolean bl;
                                Log.d(TAG, "check list of houses " + list);
                                for (int i = 0; i < list.size() ; i++){
                                    bl = true;
                                    Log.d(TAG, "check into for loop main");
                                    House house = list.get(i).toObject(House.class);
                                    if(list.get(i).getId().equals(user.getH_id())){
                                        Log.d(TAG, "check own house condition matched");
                                        if(isAdded()) {
                                            assert house != null;
                                            new AddMarker(house.getImage(),house.getLocation().getLatitude(),house.getLocation().getLongitude(),list.get(i).getId(),house.getN_id(),green,getString(R.string.type_member)).execute();
                                        }
                                        bl = false;
                                    }
                                    if(bl){
                                        if(rel != null){
                                            for(int j = 0; j < rel.size();j++){

                                                Log.d(TAG, "check into for loop rel");
                                                if(list.get(i).getId().equals(rel.get(j))){
                                                    Log.d(TAG, "check rel condition matched");
                                                    if(isAdded()) {
                                                        assert house != null;
                                                        new AddMarker(house.getImage(),house.getLocation().getLatitude(),house.getLocation().getLongitude(),list.get(i).getId(),house.getN_id(),red,getString(R.string.type_relative)).execute();
                                                    }
                                                    bl = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if(bl) {
                                        if (fri != null && fri.size() != 0) {
                                            for (int j = 0; j < fri.size(); j++) {
                                                Log.d(TAG, "check into for loop freind");
                                                if (list.get(i).getId().equals(fri.get(j))) {
                                                    Log.d(TAG, "check freind condtion matched");
                                                    if(isAdded()) {
                                                        assert house != null;
                                                        new AddMarker(house.getImage(), house.getLocation().getLatitude(), house.getLocation().getLongitude(), list.get(i).getId(), house.getN_id(),blue,getString(R.string.type_friend)).execute();
                                                    }
                                                    break;
                                                } else if (j == fri.size() - 1) {
                                                    Log.d(TAG, "check  none condition");
                                                    if(isAdded()) {
                                                        assert house != null;
                                                        new AddMarker(house.getImage(), house.getLocation().getLatitude(), house.getLocation().getLongitude(), list.get(i).getId(), house.getN_id(), Color.BLACK,getString(R.string.type_other)).execute();
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "check none condition main loop");
                                            if(isAdded()) {
                                                assert house != null;
                                                new AddMarker(house.getImage(), house.getLocation().getLatitude(), house.getLocation().getLongitude(), list.get(i).getId(), house.getN_id(), Color.BLACK,getString(R.string.type_other)).execute();
                                            }
                                        }
                                    }

                                }
                            }
                        });
                    }
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    //***************************************** findLcation **************************************************************
    private void findLocation(){
        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            User user = new User();
                            if(isAdded()){
                                user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                ((UserClient) (Objects.requireNonNull(getActivity()).getApplicationContext())).setUser(user);
                            }

                            new FindFriendsRelatives().execute();

                            if(isAdded()){
                                assert user != null;
                                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);

                                            assert hood != null;
                                            final double longitudeLeft = hood.getLongitude().getLatitude();
                                            final double longitudeRight = hood.getLongitude().getLongitude();
                                            final double latitudeLeft = hood.getLatitude().getLatitude();
                                            final double latitudeRight = hood.getLatitude().getLongitude();

                                            double midpoint1 = (latitudeRight + latitudeLeft) / 2;
                                            double midpoint2 = (longitudeRight + longitudeLeft) / 2;

                                            Log.d(TAG, "onComplete: midpoint2 " + midpoint2 + " midpoint1 " + midpoint1);

                                            LatLng latLng = new LatLng(midpoint1, midpoint2);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                                            mMap.setMinZoomPreference(18f);
                                            mMap.getUiSettings().setScrollGesturesEnabled(false);

                                            mMain.setVisibility(View.VISIBLE);
                                            mProgress.setVisibility(View.GONE);
                                        } else {
                                            mProgress.setVisibility(View.GONE);
                                            mMain.setVisibility(View.GONE);
                                            mInternet.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                            if(isAdded()){
                                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).get().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mProgress.setVisibility(View.GONE);
                                        mMain.setVisibility(View.GONE);
                                        mInternet.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } else {
                            mProgress.setVisibility(View.GONE);
                            mMain.setVisibility(View.GONE);
                            mInternet.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }

            if(isAdded()){
                mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.setVisibility(View.GONE);
                        mMain.setVisibility(View.GONE);
                        mInternet.setVisibility(View.VISIBLE);
                    }
                });
            }

        }catch (NullPointerException e){
            Log.d(TAG, "findLocation: NullPointerException " + e.getMessage());
            if(isAdded())
            Toast.makeText(getActivity(), getString(R.string.some_thing_went_wrong), Toast.LENGTH_SHORT).show();
        }
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

    //************************************************* check friends and realtives ************************************************
    private  void checkFreindsRelatives(){
        Log.d(TAG, "check into freinds and relatives called" );
        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> relativesList = Objects.requireNonNull(task.getResult()).getDocuments();
                            Log.d(TAG, "check into relatives task successful " + relativesList);
                            for (int i = 0; i < relativesList.size() ; i++){
                                Relative relative = relativesList.get(i).toObject(Relative.class);
                                assert relative != null;
                                rel.add(relative.getH_id());
                                Log.d(TAG, "check into relatives single house " + relative.getH_id());
                            }
                        }

                    }
                });
            }
            if(isAdded()){
                Tasks.await(mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> freindsList = Objects.requireNonNull(task.getResult()).getDocuments();
                            Log.d(TAG, "check into freinds task succesful " + freindsList);
                            for (int i = 0; i < freindsList.size() ; i++){
                                Friend friend = freindsList.get(i).toObject(Friend.class);
                                assert friend != null;
                                fri.add(friend.getH_id());
                                Log.d(TAG, "check into friend single house " + friend.getH_id());
                            }
                        }
                    }
                }));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    class FindFriendsRelatives extends AsyncTask<Void, String, Void> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        FindFriendsRelatives(){

        }
        @Override
        protected void onPreExecute() {


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Void doInBackground(Void... myBundle) {
            Log.d(TAG, "check into freinds and relatives ");
            checkFreindsRelatives();
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
            addMarkers();

        }
    }



    @SuppressLint("StaticFieldLeak")
    class AddMarker extends AsyncTask<Void, String, Bitmap> implements GoogleMap.OnMarkerClickListener {

        Bitmap greyBg;
        double latitude,longitude;
        Bitmap bmp = null;
        URL url = null;
        private String h_id;
        private String n_id;
        String image;
        MarkerOptions houseMarker;
        int color;
        String tag;




        AddMarker(String image, double latitude, double longitude, String h_id, String n_id, int color, String tag){
            this.greyBg = bitmapGreybg;
            this.image = image;
            this.h_id = h_id;
            this.n_id = n_id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.color = color;
            this.tag = tag;
        }


        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "createHouseMarker: called");
            LatLng houseLocation = new LatLng(latitude,longitude);

            houseMarker = new MarkerOptions()
                    .snippet(h_id + n_id + tag)
                    .title("")
                    .position(houseLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(greyBg))
                    .draggable(false);


            Log.d(TAG, "onPostExecute: " + bmp);
            Log.d(TAG, "onPreExecute: " + houseMarker.getSnippet() );
            Log.d(TAG, "onPreExecute: " + n_id);
            mMap.addMarker(houseMarker);
            mMap.setOnMarkerClickListener(this);
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
                bmp = addBorderToCircularBitmap(bmp,3, color);


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

        @Override
        public boolean onMarkerClick(Marker marker) {

            if(isAdded()){
                Intent intent = new Intent(getActivity(), MainHouseInfoActivity.class);
                intent.putExtra(getString(R.string.house_id),marker.getSnippet());
                Log.d(TAG, "onMarkerClick: " + marker.getSnippet() + marker.getTitle());
                startActivity(intent);
            }


            return true;
        }
    }


}
