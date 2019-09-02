package com.intouchapp.intouch.Main.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Explore.Searching.SearchingActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.SearchItem;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.ExampleAdapter;
import com.intouchapp.intouch.Utills.ExploreRecyclerAdapter;
import com.intouchapp.intouch.Utills.SnapHelperOneByOne;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreActivity extends AppCompatActivity {

    //widget
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;

    private Context mContext;

    List<String> rel = new ArrayList<>();
    List<String> fri = new ArrayList<>();

    List<List<House>> houses = new ArrayList<>();

    //variable
    private List<Hood> hoods = new ArrayList<>();
    private ImageView mBack;

    private User ownUser;

    private static final String TAG = "ExploreActivity";
    String mHoodName[] = {"Mozang","Avenue United","Begum Road"};

    private ExampleAdapter adapter;
    private List<SearchItem> exampleList;

    private FirebaseFirestore mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mContext = ExploreActivity.this;
        mBack = (ImageView) findViewById(R.id.iv_back);
        mSearchView = (SearchView) findViewById(R.id.et_search);

        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();

        mDb = FirebaseFirestore.getInstance();
        checkFreindsRelatives();


        settingSearchView();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    //************************************************* setting search view **************************************************
    private void settingSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty()){
                    Intent intent = new Intent(mContext, SearchingActivity.class);
                    intent.putExtra(getString(R.string.search),query);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }
    //************************************************* check friends and realtives ************************************************
    private  void checkFreindsRelatives() {

        Log.d(TAG, "check into freinds and relatives called");

        mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> relativesList = task.getResult().getDocuments();
                    Log.d(TAG, "check into relatives task successful " + relativesList);
                    for (int i = 0; i < relativesList.size(); i++) {
                        Relative relative = relativesList.get(i).toObject(Relative.class);
                        rel.add(relative.getH_id());
                        Log.d(TAG, "check into relatives single house " + relative.getH_id());

                        if (relativesList.size() - 1 == i) {
                            mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<DocumentSnapshot> freindsList = task.getResult().getDocuments();
                                        Log.d(TAG, "check into freinds task succesful " + freindsList);
                                        for (int j = 0; j < freindsList.size(); j++) {
                                            Friend friend = freindsList.get(j).toObject(Friend.class);
                                            fri.add(friend.getH_id());
                                            Log.d(TAG, "check into friend single house " + friend.getH_id());

                                            if(freindsList.size() -1 == j){
                                                //****************************************** add Markers ***********************************************************
                                                Log.d(TAG, "check add marker called" + j);

                                                mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        List<DocumentSnapshot> hoodList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                        for(int q = 0; q < hoodList.size() ; q++){

                                                            final Hood hood = hoodList.get(q).toObject(Hood.class);


                                                            Log.d(TAG, "onComplete: hood id " + ownUser.getN_id() + hood.getId());
                                                            if(ownUser.getN_id() != null){
                                                                assert hood != null;
                                                                if(!hood.getId().equals(ownUser.getN_id())){
                                                                    hoods.add(hood);
                                                                    final User user = ((UserClient) (mContext.getApplicationContext())).getUser();
                                                                    if(user != null){
                                                                        Log.d(TAG, "check user is not null" );
                                                                        mDb.collection(mContext.getString(R.string.collection_hoods)).document(hood.getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                                boolean bl;
                                                                                Log.d(TAG, "check list of houses " + list);
                                                                                List<House> houseList = new ArrayList<>();
                                                                                for (int n = 0; n < list.size() ; n++){
                                                                                    bl = true;
                                                                                    Log.d(TAG, "check into for loop main");
                                                                                    House house = list.get(n).toObject(House.class);
                                                                                    houseList.add(house);

                                                                                    if(list.size() -1 == n){
                                                                                        houses.add(houseList);
                                                                                    }

                                                                                    settingRecyclerView(rel,fri,hoods,houses,mContext);

                                                                                }
                                                                                if(task.getResult().size() == 0){
                                                                                    settingRecyclerView(rel,fri,hoods,houses,mContext);
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }


                                                        }
                                                    }
                                                });


                                            }





                                        }
                                    }if(Objects.requireNonNull(task.getResult()).size() == 0){

                                        mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    List<DocumentSnapshot> hoodList = Objects.requireNonNull(task.getResult()).getDocuments();

                                                    for(int q = 0; q < hoodList.size() ; q++){

                                                        final Hood hood = hoodList.get(q).toObject(Hood.class);


                                                        Log.d(TAG, "onComplete: hood id " + ownUser.getN_id() + hood.getId());
                                                        if(ownUser.getN_id() != null){
                                                            assert hood != null;
                                                            if(!hood.getId().equals(ownUser.getN_id())){
                                                                hoods.add(hood);
                                                                final User user = ((UserClient) (mContext.getApplicationContext())).getUser();
                                                                if(user != null){
                                                                    Log.d(TAG, "check user is not null" );
                                                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(hood.getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                            boolean bl;
                                                                            Log.d(TAG, "check list of houses " + list);
                                                                            List<House> houseList = new ArrayList<>();
                                                                            for (int n = 0; n < list.size() ; n++){
                                                                                bl = true;
                                                                                Log.d(TAG, "check into for loop main");
                                                                                House house = list.get(n).toObject(House.class);
                                                                                houseList.add(house);

                                                                                if(list.size() -1 == n){
                                                                                    houses.add(houseList);
                                                                                }

                                                                                settingRecyclerView(rel,fri,hoods,houses,mContext);

                                                                            }
                                                                            if(task.getResult().size() == 0){
                                                                                settingRecyclerView(rel,fri,hoods,houses,mContext);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }


                                                    }
                                                }if(task.getResult().size() == 0){
                                                    settingRecyclerView(rel,fri,hoods,houses,mContext);
                                                }

                                            }
                                        });

                                    }
                                }
                            });

                        }
                    }
                }if(Objects.requireNonNull(task.getResult()).size() == 0){
                    mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> freindsList = task.getResult().getDocuments();
                                Log.d(TAG, "check into freinds task succesful " + freindsList);
                                for (int j = 0; j < freindsList.size(); j++) {
                                    Friend friend = freindsList.get(j).toObject(Friend.class);
                                    fri.add(friend.getH_id());
                                    Log.d(TAG, "check into friend single house " + friend.getH_id());

                                    if(freindsList.size() -1 == j){
                                        //****************************************** add Markers ***********************************************************
                                        Log.d(TAG, "check add marker called" + j);

                                        mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                if(task.isSuccessful()){
                                                    List<DocumentSnapshot> hoodList = Objects.requireNonNull(task.getResult()).getDocuments();
                                                    for(int q = 0; q < hoodList.size() ; q++){

                                                        final Hood hood = hoodList.get(q).toObject(Hood.class);

                                                        Log.d(TAG, "onComplete: hood id " + ownUser.getN_id() + hood.getId());
                                                        if(ownUser.getN_id() != null){
                                                            assert hood != null;
                                                            if(!hood.getId().equals(ownUser.getN_id())){
                                                                hoods.add(hood);
                                                                final User user = ((UserClient) (mContext.getApplicationContext())).getUser();
                                                                if(user != null){
                                                                    Log.d(TAG, "check user is not null" );
                                                                    mDb.collection(mContext.getString(R.string.collection_hoods)).document(hood.getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                            boolean bl;
                                                                            Log.d(TAG, "check list of houses " + list);
                                                                            List<House> houseList = new ArrayList<>();
                                                                            for (int n = 0; n < list.size() ; n++){
                                                                                bl = true;
                                                                                Log.d(TAG, "check into for loop main");
                                                                                House house = list.get(n).toObject(House.class);
                                                                                houseList.add(house);

                                                                                if(list.size() -1 == n){
                                                                                    houses.add(houseList);
                                                                                }


                                                                                settingRecyclerView(rel,fri,hoods,houses,mContext);

                                                                            }
                                                                            if(task.getResult().size() == 0){
                                                                                settingRecyclerView(rel,fri,hoods,houses,mContext);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }



                                                    }
                                                }if(task.getResult().size() == 0){
                                                    settingRecyclerView(rel,fri,hoods,houses,mContext);
                                                }

                                            }
                                        });


                                    }





                                }
                            }if(task.getResult().size() == 0){
                                mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if(task.isSuccessful()){
                                            List<DocumentSnapshot> hoodList = Objects.requireNonNull(task.getResult()).getDocuments();
                                            for(int q = 0; q < hoodList.size() ; q++){

                                                final Hood hood = hoodList.get(q).toObject(Hood.class);

                                                Log.d(TAG, "onComplete: hood id " + ownUser.getN_id() + hood.getId());
                                                if(ownUser.getN_id() != null){
                                                    assert hood != null;
                                                    if(!hood.getId().equals(ownUser.getN_id())){
                                                        hoods.add(hood);
                                                        final User user = ((UserClient) (mContext.getApplicationContext())).getUser();
                                                        if(user != null){
                                                            Log.d(TAG, "check user is not null" );
                                                            mDb.collection(mContext.getString(R.string.collection_hoods)).document(hood.getId()).collection(mContext.getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                    boolean bl;
                                                                    Log.d(TAG, "check list of houses " + list);
                                                                    List<House> houseList = new ArrayList<>();
                                                                    for (int n = 0; n < list.size() ; n++){
                                                                        bl = true;
                                                                        Log.d(TAG, "check into for loop main");
                                                                        House house = list.get(n).toObject(House.class);
                                                                        houseList.add(house);

                                                                        if(list.size() -1 == n){
                                                                            houses.add(houseList);
                                                                        }


                                                                        settingRecyclerView(rel,fri,hoods,houses,mContext);

                                                                    }
                                                                    if(task.getResult().size() == 0){
                                                                        settingRecyclerView(rel,fri,hoods,houses,mContext);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }



                                            }
                                        }if(task.getResult().size() == 0){
                                            settingRecyclerView(rel,fri,hoods,houses,mContext);
                                        }

                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

    }


    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<String> rel,List<String> fri,List<Hood> hoods,List<List<House>> houses,Context mContext){

        if(hoods != null){
            if(hoods.size() != 0){
                mRecyclerView.setOnFlingListener(null);
                new SnapHelperOneByOne().attachToRecyclerView(mRecyclerView);
                mRecyclerView.setAdapter(new ExploreRecyclerAdapter(rel,fri,hoods,houses,mContext));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
            }
        }

    }
}
