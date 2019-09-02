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
import com.intouchapp.intouch.Models.Request;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.SendRequestsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SendRequestsFragment extends Fragment {

    //variable


    //widget
    private RecyclerView mRecyclerView;
    private ConstraintLayout mNothing;

    private User ownUser;

    List<Request> requests = new ArrayList<>();

    private static final String TAG = "SendRequestsFragment";

    //firebase
    private FirebaseFirestore mDb;



    Context mContext;
    public SendRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_request, container, false);

        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);

        if(isAdded()){
            mContext = getActivity();
        }


        mDb = FirebaseFirestore.getInstance();

        if (isAdded()){
            ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();
        }

        settingSendRequest();

        return v;
    }

    private void settingSendRequest(){

        try {
            if (isAdded()){
                mDb.collection(mContext.getString(R.string.collection_requests)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: task");
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();

                            for (int i = 0; i < list.size() ; i++){
                                Log.d(TAG, "onComplete: task loop");
                                Request request = list.get(i).toObject(Request.class);

                                assert request != null;
                                if(request.getS_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())&& (request.getStatus().equals(getString(R.string.request_accept)))
                                        && !request.getType().equals(mContext.getString(R.string.type_member))){
                                    Log.d(TAG, "onComplete: matched");
                                    requests.add(request);
                                }

                                if(list.size() - 1 == i){
                                    settingRecyclerView(requests);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() == 0){
                            Log.d(TAG, "onComplete: task 0");
                            settingRecyclerView(requests);
                        }

                    }
                });
            }

        }catch (NullPointerException e){

        }


    }
    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<Request> requests){

        if(requests != null){
            if(requests.size() != 0){
                mNothing.setVisibility(View.GONE);
                if(isAdded()){
                    mRecyclerView.setAdapter(new SendRequestsRecyclerAdapter(requests, mContext));
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
