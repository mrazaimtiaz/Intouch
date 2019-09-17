package com.intouchapp.intouch.Main.Account.Knowers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
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
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.KnowersRecyclerAdapter;
import com.intouchapp.intouch.Utills.SearchingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FriendsFragment extends Fragment {


    //widget
    private RecyclerView mRecyclerView;

    private List<String> freindList = new ArrayList<>();

    private FirebaseFirestore mDb;
    private ConstraintLayout mNothing;

    Context mContext;

    private static final String TAG = "FriendsFragment";


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friends, container, false);

        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);
        mContext = getActivity();

        mDb = FirebaseFirestore.getInstance();

        settingFriendList();
        return v;
    }
    //************************************** setting freind list *******************************************************
    private void settingFriendList(){
        try {
            if (isAdded()){
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                Friend friend = list.get(i).toObject(Friend.class);
                                if(friend != null){
                                    if (friend.getF_id() != null) {
                                        if (friend.getF_id().size() != 0) {

                                            Log.d(TAG, "onComplete: get friend list");
                                            freindList.addAll(friend.getF_id());
                                            Log.d(TAG, "onComplete:freindList " + freindList);
                                        }
                                    }
                                }

                                if (i == list.size() - 1) {
                                    settingRecyclerView(freindList);
                                }
                            }
                        } if(Objects.requireNonNull(task.getResult()).size() == 0) {
                            settingRecyclerView(freindList);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<String> freindList){
        if(freindList != null){
            if(freindList.size() != 0){
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new KnowersRecyclerAdapter(freindList, getString(R.string.type_friend), mContext));
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
