package com.intouchapp.intouch.Main.Explore.Searching;


import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Home.FallowersActivity;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SearchingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * A simple {@link Fragment} subclass.
 */
public class HoodSearchResultFragment extends Fragment {



    private static final String TAG = "HoodSearchResultFragmen";

    //widget
    private RecyclerView mRecyclerView;
    private ConstraintLayout mNothing;
    private FirebaseFirestore mDb;

    private List<Hood> hoods = new ArrayList<>();

    Context mContext;


    public HoodSearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hood_search_result, container, false);

        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);

        if(isAdded()){
            mContext = getActivity();
        }

        SearchingActivity activity = (SearchingActivity) getActivity();
        assert activity != null;
        final String getData = activity.sendData();

        mDb = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreateView: getData" + getData);

        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_hoods)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                        for(int i = 0; i < list.size() ; i++){
                            Hood hood = list.get(i).toObject(Hood.class);

                            assert hood != null;
                            int ratio = FuzzySearch.ratio(hood.getName(),getData);
                            if(ratio > 35){
                                hoods.add(hood);
                            }

                            if(i == list.size() - 1){
                                settingRecyclerView(hoods);
                            }
                        }
                    }
                });
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return v;
    }
    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<Hood> hoods){
        if(hoods != null){
            if(hoods.size() != 0){
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new SearchingRecyclerAdapter(hoods,null,null,null,null,null,getString(R.string.type_hood),mContext));
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                }

            }else{
                mNothing.setVisibility(View.VISIBLE);
            }
        }else{
            mNothing.setVisibility(View.VISIBLE);
        }

    }

}
