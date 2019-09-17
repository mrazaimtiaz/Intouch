package com.intouchapp.intouch.Main.Explore.Searching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SearchingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.xdrop.fuzzywuzzy.FuzzySearch;


public class PeopleSearchResultFragment extends Fragment {

    //variable
    private static final String TAG = "PeopleSearchResultFragm";

    //widget
    private RecyclerView mRecyclerView;
    private ConstraintLayout mNothing;
    private List<String> freindList;
    private List<String> memberList;
    private List<String> relativeList;

    //Firebase
    private FirebaseFirestore mDb;

    private List<User> users = new ArrayList<>();

    private User user;

    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_people_search_result, container, false);
        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);
        mContext = getActivity();

        freindList = new ArrayList<>();
        memberList = new ArrayList<>();
        relativeList = new ArrayList<>();


        mDb = FirebaseFirestore.getInstance();

        if(isAdded()){
            SearchingActivity activity = (SearchingActivity) getActivity();
            final String getData = activity.sendData();

            gettingUserList(getData);
        }


        return v;
    }
    //******************************************************** getting userdata ****************************************************
    private void gettingUserList(final String getData) {
        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_users)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            try {
                                if(isAdded()){
                                    mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                House house = Objects.requireNonNull(task.getResult()).toObject(House.class);
                                                assert house != null;
                                                if (house.getMembers() != null) {
                                                    if (house.getMembers().size() != 0) {
                                                        Log.d(TAG, "onComplete: get member list");
                                                        memberList.addAll(house.getMembers());
                                                        Log.d(TAG, "onComplete:memberList " + memberList);
                                                    }
                                                }
                                            }
                                            if(isAdded()){
                                                mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_relatives)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                                                    relativeList.addAll(relative.getR_id());
                                                                    Log.d(TAG, "onComplete:relativeList " + relativeList);
                                                                }
                                                            }
                                                        }
                                                        if(isAdded()){
                                                            mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_friends)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                                                                        for (int i = 0; i < list.size(); i++) {
                                                                            Friend friend = list.get(i).toObject(Friend.class);
                                                                            assert friend != null;
                                                                            if (friend.getF_id() != null) {
                                                                                if (friend.getF_id().size() != 0) {
                                                                                    Log.d(TAG, "onComplete: get friend list");
                                                                                    freindList.addAll(friend.getF_id());
                                                                                    Log.d(TAG, "onComplete:freindList " + freindList);
                                                                                }
                                                                            }
                                                                            if (i == list.size() - 1) {
                                                                                if(isAdded()){
                                                                                    mDb.collection(getString(R.string.collection_users)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                            for(int i = 0; i < list.size() ; i++){
                                                                                                User user1 = list.get(i).toObject(User.class);

                                                                                                assert user1 != null;
                                                                                                int ratio = FuzzySearch.ratio(user1.getName(),getData);
                                                                                                if(ratio > 35){
                                                                                                    users.add(user1);
                                                                                                }

                                                                                                if(i == list.size() - 1){
                                                                                                    settingRecyclerView(users);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }


                                                                            }
                                                                        }
                                                                    }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                                        if(isAdded()){
                                                                            mDb.collection(getString(R.string.collection_users)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                                                                    for(int i = 0; i < list.size() ; i++){
                                                                                        User user1 = list.get(i).toObject(User.class);

                                                                                        assert user1 != null;
                                                                                        int ratio = FuzzySearch.ratio(user1.getName(),getData);
                                                                                        if(ratio > 35){
                                                                                            users.add(user1);
                                                                                        }

                                                                                        if(i == list.size() - 1){
                                                                                            settingRecyclerView(users);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                        }

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

                            } catch (NullPointerException e) {
                                Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
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
    public void settingRecyclerView(List<User> user){
        if(user != null){
            if(user.size() != 0){
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new SearchingRecyclerAdapter(null,null,user,relativeList,freindList,memberList,getString(R.string.type_people),mContext));
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
