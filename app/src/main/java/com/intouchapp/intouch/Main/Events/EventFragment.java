package com.intouchapp.intouch.Main.Events;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.MainActivity;
import com.intouchapp.intouch.Main.Posts.PostPhotoActivity;
import com.intouchapp.intouch.Models.Event;
import com.intouchapp.intouch.Models.Friend;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Post;
import com.intouchapp.intouch.Models.Relative;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.DropDownAdapter;
import com.intouchapp.intouch.Utills.EventRecyclerAdapter;
import com.intouchapp.intouch.Utills.PostRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    //widget
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloat;
    private ConstraintLayout mNothing,mMain,mProgress;
    private Spinner dropDown;

    private FirebaseFirestore mDb;

    private Context mContext;

    private List<String> freindList;
    private List<String> memberList;
    private List<String> relativeList;
    private User user;



    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mNothing = (ConstraintLayout) v.findViewById(R.id.constraintLayoutNothing);
        mProgress = (ConstraintLayout) v.findViewById(R.id.constraintLayoutprogress);
        mMain = (ConstraintLayout) v.findViewById(R.id.constraintLayoutmain);
        mFloat = (FloatingActionButton) v.findViewById(R.id.floating_add_event);


        dropDown = (Spinner) v.findViewById(R.id.iv_dropdown);

        if(isAdded())
        mContext = getActivity();

        mDb = FirebaseFirestore.getInstance();

        freindList = new ArrayList<>();
        memberList = new ArrayList<>();
        relativeList = new ArrayList<>();

        mMain.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        gettingUserList();

        intentMethod();

        return v;
    }

    //******************************************************** intent method ***************************************************************
    private void intentMethod(){
        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdded()){
                    Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
    //******************************************************** getting userdata ****************************************************
    private void gettingUserList() {
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
                                            if (isAdded()){
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
                                                        if (isAdded()){
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
                                                                                settingList();
                                                                                if(isAdded()){
                                                                                    String[] privacy = {getString(R.string.public_), getString(R.string.hood), getString(R.string.knowers), getString(R.string.friend), getString(R.string.relative), getString(R.string.family)};
                                                                                    int[] images = {R.drawable.ic_public_black, R.drawable.ic_castle_with_flag_yellow, R.drawable.ic_knower_magenta, R.drawable.ic_friends_blue, R.drawable.ic_relative_red, R.drawable.ic_shelter_green};
                                                                                    dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                        @Override
                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                            if (position == 0) {
                                                                                                mMain.setVisibility(View.GONE);
                                                                                                mProgress.setVisibility(View.VISIBLE);
                                                                                                settingList();
                                                                                            }
                                                                                            if(position == 1){
                                                                                                mMain.setVisibility(View.GONE);
                                                                                                mProgress.setVisibility(View.VISIBLE);
                                                                                                hoodList();
                                                                                            }
                                                                                            if(position == 2){
                                                                                                mMain.setVisibility(View.GONE);
                                                                                                mProgress.setVisibility(View.VISIBLE);
                                                                                                knowerList();
                                                                                            }
                                                                                            if(position == 3){
                                                                                                mMain.setVisibility(View.GONE);
                                                                                                mProgress.setVisibility(View.VISIBLE);
                                                                                                freindList();
                                                                                            }
                                                                                            if(position == 4){
                                                                                                mMain.setVisibility(View.GONE);
                                                                                                mProgress.setVisibility(View.VISIBLE);
                                                                                                relativeList();
                                                                                            }
                                                                                            if(position == 5){
                                                                                                mMain.setVisibility(View.GONE);
                                                                                                mProgress.setVisibility(View.VISIBLE);
                                                                                                memberList();
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                                                        }
                                                                                    });
                                                                                    if(isAdded()){
                                                                                        DropDownAdapter customAdapter = new DropDownAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), images, privacy);
                                                                                        dropDown.setAdapter(customAdapter);
                                                                                    }
                                                                                }


                                                                            }
                                                                        }
                                                                    }if(Objects.requireNonNull(task.getResult()).size() == 0){
                                                                        settingList();

                                                                        if(isAdded()){
                                                                            String[] privacy = {getString(R.string.public_), getString(R.string.hood), getString(R.string.knowers), getString(R.string.friend), getString(R.string.relative), getString(R.string.family)};
                                                                            int[] images = {R.drawable.ic_public_black, R.drawable.ic_castle_with_flag_yellow, R.drawable.ic_knower_magenta, R.drawable.ic_friends_blue, R.drawable.ic_relative_red, R.drawable.ic_shelter_green};
                                                                            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                @Override
                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                    if (position == 0) {
                                                                                        mMain.setVisibility(View.GONE);
                                                                                        mProgress.setVisibility(View.VISIBLE);
                                                                                        settingList();
                                                                                    }
                                                                                    if(position == 1){
                                                                                        mMain.setVisibility(View.GONE);
                                                                                        mProgress.setVisibility(View.VISIBLE);
                                                                                        hoodList();
                                                                                    }
                                                                                    if(position == 2){
                                                                                        mMain.setVisibility(View.GONE);
                                                                                        mProgress.setVisibility(View.VISIBLE);
                                                                                        knowerList();
                                                                                    }
                                                                                    if(position == 3){
                                                                                        mMain.setVisibility(View.GONE);
                                                                                        mProgress.setVisibility(View.VISIBLE);
                                                                                        freindList();
                                                                                    }
                                                                                    if(position == 4){
                                                                                        mMain.setVisibility(View.GONE);
                                                                                        mProgress.setVisibility(View.VISIBLE);
                                                                                        relativeList();
                                                                                    }
                                                                                    if(position == 5){
                                                                                        mMain.setVisibility(View.GONE);
                                                                                        mProgress.setVisibility(View.VISIBLE);
                                                                                        memberList();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                                }
                                                                            });
                                                                            if(isAdded()){
                                                                                DropDownAdapter customAdapter = new DropDownAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), images, privacy);
                                                                                dropDown.setAdapter(customAdapter);
                                                                            }
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
                                Intent intent = new Intent(getActivity(), MainActivity.class);
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
    //************************************************** relative list *****************************************************
    private void relativeList() {
        final List<Event> members = new ArrayList<>();

        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_events)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0;i < list.size() ; i++){
                                final Event event = list.get(i).toObject(Event.class);

                                assert event != null;
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_relative))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            Log.d(TAG, "onComplete: into the list " + relativeList);
                                            if(relativeList != null){
                                                Log.d(TAG, "setting list relative list not null " + relativeList);
                                                Log.d(TAG, "onComplete: " + relativeList);
                                                if(relativeList.size() != 0){
                                                    Log.d(TAG, "onComplete: size is 0" + relativeList);
                                                    if(relativeList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add relative post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(i == list.size() -1){
                                    Log.d(TAG, "onComplete: 1settingrecycler " + memberList + relativeList + freindList);
                                    settingRecyclerView(members);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() -1 == 0) {
                            Log.d(TAG, "onComplete: 2settingrecycler " + memberList + relativeList + freindList);
                            settingRecyclerView(members);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //****************************************************** friend list *************************************************************
    private void freindList() {
        final List<Event> members = new ArrayList<>();

        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_events)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0;i < list.size() ; i++){
                                final Event event = list.get(i).toObject(Event.class);

                                if(isAdded()){
                                    assert event != null;
                                    if(event.getType().equals(getString(R.string.type_friend))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            if(freindList != null){
                                                Log.d(TAG, "setting list freind list not null " + freindList);
                                                if(freindList.size() != 0){
                                                    if(freindList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add freind list post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(i == list.size() -1){
                                    Log.d(TAG, "onComplete: 1settingrecycler " + memberList + relativeList + freindList);
                                    settingRecyclerView(members);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() -1 == 0){
                            Log.d(TAG, "onComplete: 2settingrecycler " + memberList + relativeList + freindList);
                            settingRecyclerView(members);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }
    //************************************************************ memberlist ************************************************************
    private void memberList() {
        final List<Event> members = new ArrayList<>();

        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_events)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0;i < list.size() ; i++){
                                final Event event = list.get(i).toObject(Event.class);

                                assert event != null;
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_member))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            if(memberList != null){
                                                Log.d(TAG, "onComplete: setting list " + memberList);
                                                if(memberList.size() != 0){
                                                    if(memberList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add member post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(i == list.size() -1){
                                    Log.d(TAG, "onComplete: 1settingrecycler " + memberList + relativeList + freindList);
                                    settingRecyclerView(members);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() -1 == 0){
                            Log.d(TAG, "onComplete: 2settingrecycler " + memberList + relativeList + freindList);
                            settingRecyclerView(members);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //*************************************************************************** knowerlist *********************************************
    private void knowerList(){
        final List<Event> members = new ArrayList<>();

        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_events)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0;i < list.size() ; i++){
                                final Event event = list.get(i).toObject(Event.class);

                                if(isAdded()){
                                    assert event != null;
                                    if(event.getType().equals(getString(R.string.type_relative))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            Log.d(TAG, "onComplete: into the list " + relativeList);
                                            if(relativeList != null){
                                                Log.d(TAG, "setting list relative list not null " + relativeList);
                                                Log.d(TAG, "onComplete: " + relativeList);
                                                if(relativeList.size() != 0){
                                                    Log.d(TAG, "onComplete: size is 0" + relativeList);
                                                    if(relativeList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add relative post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_friend))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            if(freindList != null){
                                                Log.d(TAG, "setting list freind list not null " + freindList);
                                                if(freindList.size() != 0){
                                                    if(freindList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add freind list post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_member))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            if(memberList != null){
                                                Log.d(TAG, "onComplete: setting list " + memberList);
                                                if(memberList.size() != 0){
                                                    if(memberList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add member post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }

                                }
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_knower))){
                                        if(memberList != null){
                                            if(memberList.size() != 0){
                                                if(memberList.contains(event.getU_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                        if(freindList != null){
                                            if(freindList.size() != 0){
                                                if(freindList.contains(event.getU_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                        if(relativeList != null){
                                            if(relativeList.size() != 0){
                                                if(relativeList.contains(event.getU_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(i == list.size() -1){
                                    Log.d(TAG, "onComplete: 1settingrecycler " + memberList + relativeList + freindList);
                                    settingRecyclerView(members);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() == 0) {
                            Log.d(TAG, "onComplete: 2settingrecycler " + memberList + relativeList + freindList);
                            settingRecyclerView(members);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    private void hoodList(){
        final List<Event> members = new ArrayList<>();

        try {
            if(isAdded()){
                mDb.collection(getString(R.string.collection_events)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0;i < list.size() ; i++){
                                final Event event = list.get(i).toObject(Event.class);

                                if(isAdded()){
                                    assert event != null;
                                    if(event.getType().equals(getString(R.string.type_relative))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            Log.d(TAG, "onComplete: into the list " + relativeList);
                                            if(relativeList != null){
                                                Log.d(TAG, "setting list relative list not null " + relativeList);
                                                Log.d(TAG, "onComplete: " + relativeList);
                                                if(relativeList.size() != 0){
                                                    Log.d(TAG, "onComplete: size is 0" + relativeList);
                                                    if(relativeList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add relative post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_friend))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            if(freindList != null){
                                                Log.d(TAG, "setting list freind list not null " + freindList);
                                                if(freindList.size() != 0){
                                                    if(freindList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add freind list post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_member))){
                                        if(event.getShareWith().equals(getString(R.string.empty))){
                                            if(memberList != null){
                                                Log.d(TAG, "onComplete: setting list " + memberList);
                                                if(memberList.size() != 0){
                                                    if(memberList.contains(event.getU_id())){
                                                        Log.d(TAG, "onComplete: setting list add member post");
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user != null){
                                                if(event.getShareWith().equals(user.getH_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(isAdded()){
                                    if(event.getType().equals(getString(R.string.type_knower))){
                                        if(memberList != null){
                                            if(memberList.size() != 0){
                                                if(memberList.contains(event.getU_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                        if(freindList != null){
                                            if(freindList.size() != 0){
                                                if(freindList.contains(event.getU_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                        if(relativeList != null){
                                            if(relativeList.size() != 0){
                                                if(relativeList.contains(event.getU_id())){
                                                    members.add(event);
                                                }
                                            }
                                        }
                                    }
                                    if(event.getType().equals(getString(R.string.type_hood))){
                                        if(user.getN_id() != null){
                                            if(isAdded()){
                                                mDb.collection(getString(R.string.collection_users)).document(event.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            User postUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                            assert postUser != null;
                                                            if(postUser.getN_id() != null){
                                                                if(user.getN_id().equals(postUser.getN_id())){
                                                                    members.add(event);
                                                                }else {
                                                                    if(memberList != null){
                                                                        if(memberList.size() != 0){
                                                                            if(memberList.contains(event.getU_id())){
                                                                                members.add(event);
                                                                            }
                                                                        }
                                                                    }
                                                                    if(freindList != null){
                                                                        if(freindList.size() != 0){
                                                                            if(freindList.contains(event.getU_id())){
                                                                                members.add(event);
                                                                            }
                                                                        }
                                                                    }
                                                                    if(relativeList != null){
                                                                        if(relativeList.size() != 0){
                                                                            if(relativeList.contains(event.getU_id())){
                                                                                members.add(event);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }else {
                                                                if(memberList != null){
                                                                    if(memberList.size() != 0){
                                                                        if(memberList.contains(event.getU_id())){
                                                                            members.add(event);
                                                                        }
                                                                    }
                                                                }
                                                                if(freindList != null){
                                                                    if(freindList.size() != 0){
                                                                        if(freindList.contains(event.getU_id())){
                                                                            members.add(event);
                                                                        }
                                                                    }
                                                                }
                                                                if(relativeList != null){
                                                                    if(relativeList.size() != 0){
                                                                        if(relativeList.contains(event.getU_id())){
                                                                            members.add(event);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }


                                        }else {
                                            if(memberList != null){
                                                if(memberList.size() != 0){
                                                    if(memberList.contains(event.getU_id())){
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                            if(freindList != null){
                                                if(freindList.size() != 0){
                                                    if(freindList.contains(event.getU_id())){
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                            if(relativeList != null){
                                                if(relativeList.size() != 0){
                                                    if(relativeList.contains(event.getU_id())){
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if(i == list.size() -1){
                                    Log.d(TAG, "onComplete: 1settingrecycler " + memberList + relativeList + freindList);
                                    settingRecyclerView(members);
                                }
                            }
                        }if(Objects.requireNonNull(task.getResult()).size() == 0) {
                            Log.d(TAG, "onComplete: 2settingrecycler " + memberList + relativeList + freindList);
                            settingRecyclerView(members);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //********************************************************* setting list *********************************************************
    private void settingList() {
        final List<Event> members = new ArrayList<>();

        try {
            if (isAdded()) {

                mDb.collection(getString(R.string.collection_events)).orderBy(getString(R.string.timestamp)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                final Event event = list.get(i).toObject(Event.class);

                                assert event != null;
                                if(isAdded()){
                                    if (event.getU_id().equals(FirebaseAuth.getInstance().getUid())) {
                                        members.add(event);
                                    } else {
                                        if (event.getType().equals(getString(R.string.type_relative))) {
                                            if (event.getShareWith().equals(getString(R.string.empty))) {
                                                Log.d(TAG, "onComplete: into the list " + relativeList);
                                                if (relativeList != null) {
                                                    Log.d(TAG, "setting list relative list not null " + relativeList);
                                                    Log.d(TAG, "onComplete: " + relativeList);
                                                    if (relativeList.size() != 0) {
                                                        Log.d(TAG, "onComplete: size is 0" + relativeList);
                                                        if (relativeList.contains(event.getU_id())) {
                                                            Log.d(TAG, "onComplete: setting list add relative post");
                                                            members.add(event);
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (user != null) {
                                                    if (event.getShareWith().equals(user.getH_id())) {
                                                        members.add(event);
                                                    }
                                                }
                                            }
                                        }

                               if(isAdded()){
                                   if (event.getType().equals(getString(R.string.type_friend))) {
                                       if (event.getShareWith().equals(getString(R.string.empty))) {
                                           if (freindList != null) {
                                               Log.d(TAG, "setting list freind list not null " + freindList);
                                               if (freindList.size() != 0) {
                                                   if (freindList.contains(event.getU_id())) {
                                                       Log.d(TAG, "onComplete: setting list add freind list post");
                                                       members.add(event);
                                                   }
                                               }
                                           }
                                       } else {
                                           if (user != null) {
                                               if (event.getShareWith().equals(user.getH_id())) {
                                                   members.add(event);
                                               }
                                           }
                                       }
                                   }
                               }
                                  if(isAdded()){
                                      if (event.getType().equals(getString(R.string.type_member))) {
                                          if (event.getShareWith().equals(getString(R.string.empty))) {
                                              if (memberList != null) {
                                                  Log.d(TAG, "onComplete: setting list " + memberList);
                                                  if (memberList.size() != 0) {
                                                      if (memberList.contains(event.getU_id())) {
                                                          Log.d(TAG, "onComplete: setting list add member post");
                                                          members.add(event);
                                                      }
                                                  }
                                              }
                                          } else {
                                              if (user != null) {
                                                  if (event.getShareWith().equals(user.getH_id())) {
                                                      members.add(event);
                                                  }
                                              }
                                          }
                                      }
                                  }
                                  if(isAdded()){
                                      if (event.getType().equals(getString(R.string.type_knower))) {
                                          if (memberList != null) {
                                              if (memberList.size() != 0) {
                                                  if (memberList.contains(event.getU_id())) {
                                                      members.add(event);
                                                  }
                                              }
                                          }
                                          if (freindList != null) {
                                              if (freindList.size() != 0) {
                                                  if (freindList.contains(event.getU_id())) {
                                                      members.add(event);
                                                  }
                                              }
                                          }
                                          if (relativeList != null) {
                                              if (relativeList.size() != 0) {
                                                  if (relativeList.contains(event.getU_id())) {
                                                      members.add(event);
                                                  }
                                              }
                                          }
                                      }
                                  }
                                   if(isAdded()){
                                       if (event.getType().equals(getString(R.string.type_hood))) {
                                           if (user.getN_id() != null) {

                                               if(isAdded()){
                                                   mDb.collection(getString(R.string.collection_users)).document(event.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                           if (task.isSuccessful()) {
                                                               User postUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                               assert postUser != null;
                                                               if (postUser.getN_id() != null) {
                                                                   if (user.getN_id().equals(postUser.getN_id())) {
                                                                       members.add(event);
                                                                   } else {
                                                                       if (memberList != null) {
                                                                           if (memberList.size() != 0) {
                                                                               if (memberList.contains(event.getU_id())) {
                                                                                   members.add(event);
                                                                               }
                                                                           }
                                                                       }
                                                                       if (freindList != null) {
                                                                           if (freindList.size() != 0) {
                                                                               if (freindList.contains(event.getU_id())) {
                                                                                   members.add(event);
                                                                               }
                                                                           }
                                                                       }
                                                                       if (relativeList != null) {
                                                                           if (relativeList.size() != 0) {
                                                                               if (relativeList.contains(event.getU_id())) {
                                                                                   members.add(event);
                                                                               }
                                                                           }
                                                                       }
                                                                   }
                                                               } else {
                                                                   if (memberList != null) {
                                                                       if (memberList.size() != 0) {
                                                                           if (memberList.contains(event.getU_id())) {
                                                                               members.add(event);
                                                                           }
                                                                       }
                                                                   }
                                                                   if (freindList != null) {
                                                                       if (freindList.size() != 0) {
                                                                           if (freindList.contains(event.getU_id())) {
                                                                               members.add(event);
                                                                           }
                                                                       }
                                                                   }
                                                                   if (relativeList != null) {
                                                                       if (relativeList.size() != 0) {
                                                                           if (relativeList.contains(event.getU_id())) {
                                                                               members.add(event);
                                                                           }
                                                                       }
                                                                   }
                                                               }
                                                           }
                                                       }
                                                   });

                                               }

                                           } else {
                                               if (memberList != null) {
                                                   if (memberList.size() != 0) {
                                                       if (memberList.contains(event.getU_id())) {
                                                           members.add(event);
                                                       }
                                                   }
                                               }
                                               if (freindList != null) {
                                                   if (freindList.size() != 0) {
                                                       if (freindList.contains(event.getU_id())) {
                                                           members.add(event);
                                                       }
                                                   }
                                               }
                                               if (relativeList != null) {
                                                   if (relativeList.size() != 0) {
                                                       if (relativeList.contains(event.getU_id())) {
                                                           members.add(event);
                                                       }
                                                   }
                                               }
                                           }
                                       }
                                   }

                                    if (event.getType().equals(getString(R.string.type_other))) {
                                        if (user.getN_id() != null) {
                                            if (isAdded()){
                                                mDb.collection(getString(R.string.collection_hoods)).document(user.getN_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                                                        assert hood != null;
                                                        if (hood.getFallower() != null) {
                                                            if (hood.getFallower().size() != 0) {
                                                                if(hood.getFallower().contains(FirebaseAuth.getInstance().getUid()));
                                                                members.add(event);
                                                            }
                                                        }
                                                    }
                                                });
                                            }

                                        } else {
                                            if(isAdded()){
                                                mDb.collection(getString(R.string.collection_users)).document(event.getU_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            User postUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                                            assert postUser != null;
                                                            if (postUser.getN_id() != null) {
                                                                if (user.getN_id().equals(postUser.getN_id())) {
                                                                    members.add(event);
                                                                }
                                                            }
                                                            if (memberList != null) {
                                                                if (memberList.size() != 0) {
                                                                    if (memberList.contains(event.getU_id())) {
                                                                        members.add(event);
                                                                    }
                                                                }
                                                            }
                                                            if (freindList != null) {
                                                                if (freindList.size() != 0) {
                                                                    if (freindList.contains(event.getU_id())) {
                                                                        members.add(event);
                                                                    }
                                                                }
                                                            }
                                                            if (relativeList != null) {
                                                                if (relativeList.size() != 0) {
                                                                    if (relativeList.contains(event.getU_id())) {
                                                                        members.add(event);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    }
                                }


                                if (i == list.size() - 1) {
                                    Log.d(TAG, "onComplete: 1settingrecycler " + memberList + relativeList + freindList);
                                    settingRecyclerView(members);
                                }
                                }
                            }
                        } if(Objects.requireNonNull(task.getResult()).size() == 0) {
                            Log.d(TAG, "onComplete: 2settingrecycler " + memberList + relativeList + freindList);
                            settingRecyclerView(members);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    //------------------------------------------------setting recyclerview---------------------------------------------------------------------

    public void settingRecyclerView(List<Event> events){
        if(events != null){
            if(events.size() != 0){
                mNothing.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG, "settingRecyclerView: " + relativeList + freindList + memberList);
                if(isAdded()){
                    Collections.reverse(events);
                    mRecyclerView.setAdapter(new EventRecyclerAdapter(events,relativeList,freindList,memberList,getActivity()));
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                }
                mMain.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);

            }else{
                mMain.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                mNothing.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }else{
            mMain.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mNothing.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        }

    }

}
