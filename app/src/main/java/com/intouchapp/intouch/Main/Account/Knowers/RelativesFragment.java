package com.intouchapp.intouch.Main.Account.Knowers;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.KnowersRecyclerAdapter;
import com.intouchapp.intouch.Utills.SearchingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class RelativesFragment extends Fragment {



    //widget
    private RecyclerView mRecyclerView;

    private FirebaseFirestore mDb;
    private ConstraintLayout mNothing;

    private List<String> relativelist = new ArrayList<>();

    private static final String TAG = "RelativesFragment";


    Context mContext;

    public RelativesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_relatives, container, false);

        mDb = FirebaseFirestore.getInstance();
        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);
        mContext = getActivity();

        settingRelativeList();

        return v;
    }
    private void settingRelativeList(){
        try {
            if (isAdded()){
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                Relative relative = list.get(i).toObject(Relative.class);
                                assert relative != null;
                                if (relative.getR_id() != null) {
                                    if (relative.getR_id().size() != 0)
                                        Log.d(TAG, "onComplete: get relative list");
                                    relativelist.addAll(relative.getR_id());
                                    Log.d(TAG, "onComplete:relativeList " + relative);
                                }
                                if (i == list.size() - 1) {
                                    settingRecyclerView(relativelist);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() == 0) {
                            settingRecyclerView(relativelist);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<String> relativeList){
      if(relativeList != null){
            if(relativeList.size() != 0){
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new KnowersRecyclerAdapter(relativeList, getString(R.string.type_relative), mContext));
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
