package com.intouchapp.intouch.Main.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.intouchapp.intouch.Models.Compliment;
import com.intouchapp.intouch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComplimentsActivity extends AppCompatActivity {

    //widget
    private TextView mCompnum1,mCompnum2,mCompnum3,mCompnum4,mCompnum5,mCompnum6;
    private ImageView add1,add2,add3,add4,add5,add6;
    private ImageView mBack;

    //variable
    private static final String TAG = "ComplimentsActivity";
    String n_id;
    Compliment compliment;

    //firebase
    FirebaseFirestore mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compliments);

        mCompnum1 = (TextView) findViewById(R.id.tv_compnumber1);
        mCompnum2 = (TextView) findViewById(R.id.tv_compnumber2);
        mCompnum3 = (TextView) findViewById(R.id.tv_compnumber3);
        mCompnum4 = (TextView) findViewById(R.id.tv_compnumber4);
        mCompnum5 = (TextView) findViewById(R.id.tv_compnumber5);
        mCompnum6 = (TextView) findViewById(R.id.tv_compnumber6);

        add1 = (ImageView) findViewById(R.id.iv_add1);
        add2 = (ImageView) findViewById(R.id.iv_add2);
        add3 = (ImageView) findViewById(R.id.iv_add3);
        add4 = (ImageView) findViewById(R.id.iv_add4);
        add5 = (ImageView) findViewById(R.id.iv_add5);
        add6 = (ImageView) findViewById(R.id.iv_add6);

        mBack = (ImageView) findViewById(R.id.iv_back);

        mDb = FirebaseFirestore.getInstance();

        add1.setVisibility(View.GONE);
        add2.setVisibility(View.GONE);
        add3.setVisibility(View.GONE);
        add4.setVisibility(View.GONE);
        add5.setVisibility(View.GONE);
        add6.setVisibility(View.GONE);

        n_id = getString(R.string.empty);
        n_id = getIntent().getStringExtra(getString(R.string.n_id));
        complimentInfo();
        clicks();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void complimentInfo(){
        try {
            Log.d(TAG, "complimentInfo:  n_id " + n_id);
            mDb.collection(getString(R.string.collection_compliment)).document(n_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Compliment compliment = Objects.requireNonNull(task.getResult()).toObject(Compliment.class);

                    assert compliment != null;
                    List<String> member = compliment.getU_id();
                    if(member != null){
                        if(member.size() != 0){
                            if(member.contains(FirebaseAuth.getInstance().getUid())){
                                add1.setVisibility(View.GONE);
                                add2.setVisibility(View.GONE);
                                add3.setVisibility(View.GONE);
                                add4.setVisibility(View.GONE);
                                add5.setVisibility(View.GONE);
                                add6.setVisibility(View.GONE);
                            }else{
                                add1.setVisibility(View.VISIBLE);
                                add2.setVisibility(View.VISIBLE);
                                add3.setVisibility(View.VISIBLE);
                                add4.setVisibility(View.VISIBLE);
                                add5.setVisibility(View.VISIBLE);
                                add6.setVisibility(View.VISIBLE);
                            }
                        }else{
                            add1.setVisibility(View.VISIBLE);
                            add2.setVisibility(View.VISIBLE);
                            add3.setVisibility(View.VISIBLE);
                            add4.setVisibility(View.VISIBLE);
                            add5.setVisibility(View.VISIBLE);
                            add6.setVisibility(View.VISIBLE);
                        }

                    }else{
                        add1.setVisibility(View.VISIBLE);
                        add2.setVisibility(View.VISIBLE);
                        add3.setVisibility(View.VISIBLE);
                        add4.setVisibility(View.VISIBLE);
                        add5.setVisibility(View.VISIBLE);
                        add6.setVisibility(View.VISIBLE);
                    }


                    mCompnum1.setText("" + compliment.getComp1());
                    mCompnum2.setText("" + compliment.getComp2());
                    mCompnum3.setText("" + compliment.getComp3());
                    mCompnum4.setText("" + compliment.getComp4());
                    mCompnum5.setText("" + compliment.getComp5());
                    mCompnum6.setText("" + compliment.getComp6());
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    private void clicks(){
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add1.setVisibility(View.GONE);
                add2.setVisibility(View.GONE);
                add3.setVisibility(View.GONE);
                add4.setVisibility(View.GONE);
                add5.setVisibility(View.GONE);
                add6.setVisibility(View.GONE);
                Log.d(TAG, "onClick: clicked 1");
                addToDatabase(getString(R.string.comp1),mCompnum1);
            }
        });
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add1.setVisibility(View.GONE);
                add2.setVisibility(View.GONE);
                add3.setVisibility(View.GONE);
                add4.setVisibility(View.GONE);
                add5.setVisibility(View.GONE);
                add6.setVisibility(View.GONE);
                Log.d(TAG, "onClick: clicked 1");
                addToDatabase(getString(R.string.comp2),mCompnum2);
            }
        });
        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add1.setVisibility(View.GONE);
                add2.setVisibility(View.GONE);
                add3.setVisibility(View.GONE);
                add4.setVisibility(View.GONE);
                add5.setVisibility(View.GONE);
                add6.setVisibility(View.GONE);
                Log.d(TAG, "onClick: clicked 1");
                addToDatabase(getString(R.string.comp3),mCompnum3);
            }
        });
        add4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add1.setVisibility(View.GONE);
                add2.setVisibility(View.GONE);
                add3.setVisibility(View.GONE);
                add4.setVisibility(View.GONE);
                add5.setVisibility(View.GONE);
                add6.setVisibility(View.GONE);
                Log.d(TAG, "onClick: clicked 1");
                addToDatabase(getString(R.string.comp4),mCompnum4);
            }
        });
        add5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add1.setVisibility(View.GONE);
                add2.setVisibility(View.GONE);
                add3.setVisibility(View.GONE);
                add4.setVisibility(View.GONE);
                add5.setVisibility(View.GONE);
                add6.setVisibility(View.GONE);
                Log.d(TAG, "onClick: clicked 1");
                addToDatabase(getString(R.string.comp5),mCompnum5);
            }
        });
        add6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add1.setVisibility(View.GONE);
                add2.setVisibility(View.GONE);
                add3.setVisibility(View.GONE);
                add4.setVisibility(View.GONE);
                add5.setVisibility(View.GONE);
                add6.setVisibility(View.GONE);
                Log.d(TAG, "onClick: clicked 1");
                addToDatabase(getString(R.string.comp6),mCompnum6);
            }
        });
    }
    private void addToDatabase(final String comp, final TextView tv){
        Log.d(TAG, "addToDatabase: into");
        final DocumentReference sfDocRef = mDb.collection(getString(R.string.collection_compliment)).document(n_id);
        try {
            mDb.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
                    Log.d(TAG, "apply: applied");
                    double newComp = snapshot.getDouble(comp) + 1;

                    transaction.update(sfDocRef, comp, newComp);
                    compliment =  snapshot.toObject(Compliment.class);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    if(compliment.getU_id() != null){
                        compliment.getU_id().add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        mDb.collection(getString(R.string.collection_compliment)).document(n_id).update(getString(R.string.u_id),compliment.getU_id());
                        complimentInfo();

                        add1.setVisibility(View.GONE);
                        add2.setVisibility(View.GONE);
                        add3.setVisibility(View.GONE);
                        add4.setVisibility(View.GONE);
                        add5.setVisibility(View.GONE);
                        add6.setVisibility(View.GONE);

                        Log.d(TAG, "Transaction success!");
                    }else{
                        List<String> u_id = new ArrayList<>();
                        u_id.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        compliment.setU_id(u_id);

                        mDb.collection(getString(R.string.collection_compliment)).document(n_id).update(getString(R.string.u_id),compliment.getU_id());
                        complimentInfo();

                        add1.setVisibility(View.GONE);
                        add2.setVisibility(View.GONE);
                        add3.setVisibility(View.GONE);
                        add4.setVisibility(View.GONE);
                        add5.setVisibility(View.GONE);
                        add6.setVisibility(View.GONE);

                        Log.d(TAG, "Transaction success!");
                    }


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Transaction failure.", e);
                            add1.setVisibility(View.VISIBLE);
                            add2.setVisibility(View.VISIBLE);
                            add3.setVisibility(View.VISIBLE);
                            add4.setVisibility(View.VISIBLE);
                            add5.setVisibility(View.VISIBLE);
                            add6.setVisibility(View.VISIBLE);
                        }
                    });
        }catch (NullPointerException e){
            e.printStackTrace();
        }




    }

}
