package com.intouchapp.intouch.Main.Chats.SelectChat;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.SelectChatRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectChatFamilyFragment extends Fragment {


    //widget
    private RecyclerView mRecyclerView;

    Context mContext;
    private FirebaseFirestore mDb;
    private ConstraintLayout mNothing;

    private User user;

    private static final String TAG = "FamilyFragment";
    private List<String> memberList = new ArrayList<>();

    public SelectChatFamilyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_family, container, false);

        mDb = FirebaseFirestore.getInstance();
        //intialization
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);
        mContext = getActivity();

        settingMemberList();

        return v;
    }
    private void settingMemberList() {
        mDb.collection(getString(R.string.collection_users)).document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().toObject(User.class);
                    mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).collection(getString(R.string.collection_houses)).document(user.getH_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                House house = task.getResult().toObject(House.class);
                                if (house.getMembers() != null) {
                                    if (house.getMembers().size() != 0) {
                                        Log.d(TAG, "onComplete: get member list");
                                        memberList.addAll(house.getMembers());
                                        Log.d(TAG, "onComplete:memberList " + memberList);
                                        memberList.remove(FirebaseAuth.getInstance().getUid());
                                        settingRecyclerView(memberList);
                                    }
                                    if(house.getMembers().size() == 1){
                                        settingRecyclerView(memberList);
                                    }
                                }else{
                                    settingRecyclerView(memberList);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView (List<String> memberList) {
        mRecyclerView.setAdapter(new SelectChatRecyclerAdapter(memberList, mContext.getString(R.string.type_member), mContext));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
    }
}

