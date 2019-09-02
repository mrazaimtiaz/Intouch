package com.intouchapp.intouch.Main.Explore.Searching;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SearchingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class HouseSearchResultFragment extends Fragment {

    private static final String TAG = "HoodSearchResultFragmen";

    //widget
    private RecyclerView mRecyclerView;
    private ConstraintLayout mNothing;
    private FirebaseFirestore mDb;

    List<String> rel = new ArrayList<>();
    List<String> fri = new ArrayList<>();

    private List<House> houses = new ArrayList<>();

    Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_house_search_result, container, false);
        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mContext = getActivity();

        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);

        if(isAdded()){
            mContext = getActivity();
        }
        if(isAdded()){
            SearchingActivity activity = (SearchingActivity) getActivity();
            assert activity != null;
            final String getData = activity.sendData();
            mDb = FirebaseFirestore.getInstance();
            Log.d(TAG, "onCreateView: getData" + getData);

            gettingList(getData);
        }



        return v;
    }

    //************************************************* check friends and realtives ************************************************
    private  void gettingList(final String getData) {

        Log.d(TAG, "check into freinds and relatives called");

        try {
            if(isAdded()){
                mDb.collection(mContext.getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection(mContext.getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> relativesList = Objects.requireNonNull(task.getResult()).getDocuments();
                            Log.d(TAG, "check into relatives task successful " + relativesList);
                            for (int i = 0; i < relativesList.size(); i++) {
                                Relative relative = relativesList.get(i).toObject(Relative.class);
                                assert relative != null;
                                rel.add(relative.getH_id());
                                Log.d(TAG, "check into relatives single house " + relative.getH_id());

                                if (relativesList.size() - 1 == i) {
                                    if(isAdded()){
                                        mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    List<DocumentSnapshot> freindsList = Objects.requireNonNull(task.getResult()).getDocuments();
                                                    Log.d(TAG, "check into freinds task succesful " + freindsList);
                                                    for (int j = 0; j < freindsList.size(); j++) {
                                                        Friend friend = freindsList.get(j).toObject(Friend.class);
                                                        assert friend != null;
                                                        fri.add(friend.getH_id());
                                                        Log.d(TAG, "check into friend single house " + friend.getH_id());

                                                        if(freindsList.size() -1 == j){
                                                            //****************************************** add Markers ***********************************************************
                                                            if(isAdded()){
                                                                mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        final List<DocumentSnapshot> Hoodlist = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                        if(task.isSuccessful()){
                                                                            for(int i = 0; i < Hoodlist.size() ; i++){
                                                                                Hood hood = Hoodlist.get(i).toObject(Hood.class);

                                                                                final int finalI = i;
                                                                                assert hood != null;

                                                                                if(isAdded()){
                                                                                    mDb.collection(getString(R.string.collection_hoods)).document(hood.getId()).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                            for(int j = 0; j < list.size() ; j++) {
                                                                                                House house = list.get(j).toObject(House.class);

                                                                                                assert house != null;
                                                                                                int ratio = FuzzySearch.ratio(house.getName(),getData);
                                                                                                Log.d(TAG, "onComplete: " + ratio);
                                                                                                if(ratio > 35){
                                                                                                    Log.d(TAG, "onComplete: house added");
                                                                                                    houses.add(house);
                                                                                                }
                                                                                                if(j == list.size() - 1 && finalI == Hoodlist.size() - 1){
                                                                                                    Log.d(TAG, "onComplete: called");
                                                                                                    settingRecyclerView(houses);
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }

                                                                        if(task.getResult().size() == 0){
                                                                            settingRecyclerView(houses);
                                                                        }
                                                                    }
                                                                });
                                                            }


                                                        }

                                                    }
                                                }if(task.getResult().size() == 0){
                                                    if(isAdded()){
                                                        mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                final List<DocumentSnapshot> Hoodlist = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                if(task.isSuccessful()){
                                                                    for(int i = 0; i < Hoodlist.size() ; i++){
                                                                        Hood hood = Hoodlist.get(i).toObject(Hood.class);

                                                                        final int finalI = i;
                                                                        assert hood != null;

                                                                        if(isAdded()){
                                                                            mDb.collection(getString(R.string.collection_hoods)).document(hood.getId()).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                    for(int j = 0; j < list.size() ; j++) {
                                                                                        House house = list.get(j).toObject(House.class);

                                                                                        assert house != null;
                                                                                        int ratio = FuzzySearch.ratio(house.getName(),getData);
                                                                                        Log.d(TAG, "onComplete: " + ratio);
                                                                                        if(ratio > 35){
                                                                                            Log.d(TAG, "onComplete: house added");
                                                                                            houses.add(house);
                                                                                        }
                                                                                        if(j == list.size() - 1 && finalI == Hoodlist.size() - 1){
                                                                                            Log.d(TAG, "onComplete: called");
                                                                                            settingRecyclerView(houses);
                                                                                        }

                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }

                                                                if(task.getResult().size() == 0){
                                                                    settingRecyclerView(houses);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }

                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() == 0){
                            if(isAdded()){
                                mDb.collection(mContext.getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(mContext.getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<DocumentSnapshot> freindsList = Objects.requireNonNull(task.getResult()).getDocuments();
                                            Log.d(TAG, "check into freinds task succesful " + freindsList);
                                            for (int j = 0; j < freindsList.size(); j++) {
                                                Friend friend = freindsList.get(j).toObject(Friend.class);
                                                assert friend != null;
                                                fri.add(friend.getH_id());
                                                Log.d(TAG, "check into friend single house " + friend.getH_id());

                                                if(freindsList.size() -1 == j){
                                                    //****************************************** add Markers ***********************************************************
                                                    if(isAdded()){
                                                        mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                final List<DocumentSnapshot> Hoodlist = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                if(task.isSuccessful()){
                                                                    for(int i = 0; i < Hoodlist.size() ; i++){
                                                                        Hood hood = Hoodlist.get(i).toObject(Hood.class);

                                                                        final int finalI = i;
                                                                        assert hood != null;

                                                                        if(isAdded()){
                                                                            mDb.collection(getString(R.string.collection_hoods)).document(hood.getId()).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                    for(int j = 0; j < list.size() ; j++) {
                                                                                        House house = list.get(j).toObject(House.class);

                                                                                        assert house != null;
                                                                                        int ratio = FuzzySearch.ratio(house.getName(),getData);
                                                                                        Log.d(TAG, "onComplete: " + ratio);
                                                                                        if(ratio > 35){
                                                                                            Log.d(TAG, "onComplete: house added");
                                                                                            houses.add(house);
                                                                                        }
                                                                                        if(j == list.size() - 1 && finalI == Hoodlist.size() - 1){
                                                                                            Log.d(TAG, "onComplete: called");
                                                                                            settingRecyclerView(houses);
                                                                                        }

                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }

                                                                if(task.getResult().size() == 0){
                                                                    settingRecyclerView(houses);
                                                                }
                                                            }
                                                        });
                                                    }


                                                }

                                            }
                                        }if(task.getResult().size() == 0){
                                            if(isAdded()){
                                                mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        final List<DocumentSnapshot> Hoodlist = Objects.requireNonNull(task.getResult()).getDocuments();

                                                        if(task.isSuccessful()){
                                                            for(int i = 0; i < Hoodlist.size() ; i++){
                                                                Hood hood = Hoodlist.get(i).toObject(Hood.class);

                                                                final int finalI = i;
                                                                assert hood != null;

                                                                if(isAdded()){
                                                                    mDb.collection(getString(R.string.collection_hoods)).document(hood.getId()).collection(getString(R.string.collection_houses)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                            for(int j = 0; j < list.size() ; j++) {
                                                                                House house = list.get(j).toObject(House.class);

                                                                                assert house != null;
                                                                                int ratio = FuzzySearch.ratio(house.getName(),getData);
                                                                                Log.d(TAG, "onComplete: " + ratio);
                                                                                if(ratio > 35){
                                                                                    Log.d(TAG, "onComplete: house added");
                                                                                    houses.add(house);
                                                                                }
                                                                                if(j == list.size() - 1 && finalI == Hoodlist.size() - 1){
                                                                                    Log.d(TAG, "onComplete: called");
                                                                                    settingRecyclerView(houses);
                                                                                }

                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                        if(task.getResult().size() == 0){
                                                            settingRecyclerView(houses);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }

                    }
                });

            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<House> houses){
        Log.d(TAG, "settingRecyclerView: called " + houses);
        if(houses != null){
            if(houses.size() != 0){
                Log.d(TAG, "settingRecyclerView: 2called " + houses);
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new SearchingRecyclerAdapter(null,houses,null,rel,fri,null,getString(R.string.type_house),mContext));
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                }
            }else{
                Log.d(TAG, "settingRecyclerView:3 called " + houses);
                mNothing.setVisibility(View.VISIBLE);
            }
        }else{
            Log.d(TAG, "settingRecyclerView: called " + houses);
            mNothing.setVisibility(View.VISIBLE);
        }

    }
}
