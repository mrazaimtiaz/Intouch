package com.intouchapp.intouch.Main.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intouchapp.intouch.Models.Hood;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.FallowersRecyclerAdapter;

import java.util.List;
import java.util.Objects;

public class FallowersActivity extends AppCompatActivity {

    //widget
    private RecyclerView mRecyclerView;
    private TextView onName;

    private ImageView mBack;
    Context mContext;
    private String n_id;

    private ConstraintLayout mNothing;

    //Firebase
    private FirebaseFirestore mDb;

    private static final String TAG = "FallowersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fallowers);

        //intialization
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mContext = FallowersActivity.this;
        mBack = (ImageView) findViewById(R.id.iv_back);
        onName = (TextView) findViewById(R.id.tv_onName);
        mNothing = (ConstraintLayout) findViewById(R.id.constraintLayoutNothing);

        mDb = FirebaseFirestore.getInstance();

        intentMethod();

        n_id = getString(R.string.empty);
        n_id = getIntent().getStringExtra(getString(R.string.n_id));
        checkonName();
        checkFallowers();

    }
    public void checkonName(){
        try {
            mDb.collection(getString(R.string.collection_hoods)).document(n_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                    assert hood != null;
                    onName.setText(getString(R.string.on) + " " + hood.getName());
                    onName.setAllCaps(true);
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void checkFallowers(){
        try {
            mDb.collection(getString(R.string.collection_hoods)).document(n_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Hood hood = Objects.requireNonNull(task.getResult()).toObject(Hood.class);
                    assert hood != null;
                    settingRecyclerView(hood.getFallower());
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    public void intentMethod(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //------------------------------------------------setting recyclerview---------------------------------------------------------------------
    public void settingRecyclerView(List<String> fallower){
        if(fallower != null && fallower.size() != 0){
            mNothing.setVisibility(View.GONE);
            mRecyclerView.setAdapter(new FallowersRecyclerAdapter(fallower, mContext));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        }else{
            mNothing.setVisibility(View.VISIBLE);
        }

    }
}
