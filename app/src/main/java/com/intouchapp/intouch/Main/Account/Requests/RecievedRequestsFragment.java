package com.intouchapp.intouch.Main.Account.Requests;

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
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.RecievedRequestsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RecievedRequestsFragment extends Fragment {


    List<Request> requests = new ArrayList<>();

    //widget
    private RecyclerView mRecyclerView;
    private ConstraintLayout mNothing;


    private List<String> freindList = new ArrayList<>();

    private User user;

    private FirebaseFirestore mDb;
    Context mContext;

    private static final String TAG = "RecievedRequestsFragmen";
    public RecievedRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recieved_request, container, false);

        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);

        if(isAdded()){
            mContext = getActivity();
        }


        if (isAdded()) {
            assert mContext != null;
            user =  ((UserClient) (mContext.getApplicationContext())).getUser();
        }

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
                                assert friend != null;
                                if (friend.getF_id() != null) {
                                    if (friend.getF_id().size() != 0) {
                                        Log.d(TAG, "onComplete: get friend list");
                                        freindList.addAll(friend.getF_id());
                                        Log.d(TAG, "onComplete:freindList " + freindList);
                                    }
                                }
                                if (i == list.size() - 1) {
                                    Log.d(TAG, "onComplete: for request");
                                    if (isAdded()){
                                        mDb.collection(getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                Log.d(TAG, "onComplete: get task");

                                                List<DocumentSnapshot> requestlist = Objects.requireNonNull(task.getResult()).getDocuments();

                                                for(int j = 0; j < requestlist.size() ; j++){
                                                    Request request = requestlist.get(j).toObject(Request.class);
                                                    if(user != null){
                                                        assert request != null;
                                                        if((request.getR_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) || request.getR_id().equals(user.getH_id())) && request.getStatus().equals(getString(R.string.request_send))){
                                                            requests.add(request);
                                                        }
                                                    }
                                                    if(requestlist.size()-1 == j){
                                                        settingRecyclerView(requests,freindList);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }  if(Objects.requireNonNull(task.getResult()).size() == 0){

                            if (isAdded()){
                                mDb.collection(getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                                        for(int i = 0; i < list.size() ; i++){
                                            Request request = list.get(i).toObject(Request.class);
                                            if(user != null){
                                                assert request != null;
                                                if((request.getR_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) || request.getR_id().equals(user.getH_id())) && request.getStatus().equals(getString(R.string.request_send))){
                                                    requests.add(request);
                                                }
                                            }
                                            if(list.size()-1 == i){
                                                settingRecyclerView(requests,freindList);
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
    public void settingRecyclerView(List<Request> requests,List<String> freindList){
        Log.d(TAG, "settingRecyclerView: called");
        if(requests != null){
            if(requests.size() != 0){
                Log.d(TAG, "settingRecyclerView: size not zero");
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new RecievedRequestsRecyclerAdapter(requests,freindList, mContext));
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
