package com.intouchapp.intouch.Main.Chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.intouchapp.intouch.Main.Home.MainHouseInfoActivity;
import com.intouchapp.intouch.Main.Home.MemberInfoActivity;
import com.intouchapp.intouch.Models.Chat;
import com.intouchapp.intouch.Models.House;
import com.intouchapp.intouch.Models.Message;
import com.intouchapp.intouch.Models.User;
import com.intouchapp.intouch.R;
import com.intouchapp.intouch.Utills.UserClient;
import com.intouchapp.intouch.Utills.ChatAppMsgDTO;
import com.intouchapp.intouch.Utills.IndividualMessageAdapter;
import com.intouchapp.intouch.Utills.UniversalImageLoader;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class IndividualChatActivity extends AppCompatActivity {

    //widget
    private RecyclerView msgRecyclerView;
    private EditText msgInputText;
    private TextView mName;
    private ImageView mAvatar,mBack,mUserAvatar;

     ImageView msgSendButton;
    private String type,id,category,chat_id,n_id;

    private Context mContext;

    private static final String TAG = "IndividualChatActivity";

    private RequestQueue mRequestQue;

    private String URL = "https://fcm.googleapis.com/fcm/send";

    boolean voice = false;

    private User ownUser;

    //firebase
    FirebaseFirestore mDb;

     List<ChatAppMsgDTO> msgDtoList;
    IndividualMessageAdapter chatAppMsgAdapter;
    private List<String> checkMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        msgRecyclerView= (RecyclerView)findViewById(R.id.recycler_view);
        msgInputText = (EditText)findViewById(R.id.et_comment);
        msgSendButton = (ImageView)findViewById(R.id.iv_sendmessage);
        mAvatar = (ImageView)findViewById(R.id.iv_user_profile);
        mUserAvatar = (ImageView)findViewById(R.id.iv_avatar);
        mBack = (ImageView)findViewById(R.id.iv_back);
        mName = (TextView)findViewById(R.id.tv_name);

        mDb = FirebaseFirestore.getInstance();
        mContext = IndividualChatActivity.this;
        mRequestQue = Volley.newRequestQueue(mContext);
        AndroidThreeTen.init(mContext);

        ownUser =  ((UserClient) (mContext.getApplicationContext())).getUser();

        type = getIntent().getStringExtra(getString(R.string.intent_type));
        id = getIntent().getStringExtra(getString(R.string.intent_memberid));
        n_id = getIntent().getStringExtra(getString(R.string.n_id));
        category = getIntent().getStringExtra(getString(R.string.intent_chat));
        chat_id = getString(R.string.empty);



         msgDtoList = new ArrayList<ChatAppMsgDTO>();
        chatAppMsgAdapter  = new IndividualMessageAdapter(msgDtoList,mContext);

        msgSendButton.setAlpha(0.5f);
        msgSendButton.setEnabled(false);

        settingRecyclerView();
        settingProfile();
        if(category != null){
            if(category.equals(getString(R.string.individual_chat))){
                individualMessage();
                clickSendMessage();
            }else if(category.equals(getString(R.string.group_chat))){
                groupMessage();
                clickSendGroupMessage();
            }
        }
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //*********************************************** setting individual message **********************************************************
    private void individualMessage(){

        mDb.collection(getString(R.string.collection_chat)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    final List<DocumentSnapshot> list = task.getResult().getDocuments();
                    Boolean checkChat = true;
                    for (int i = 0; i < list.size(); i++){
                        Log.d(TAG, "onComplete: into the for loop");
                        Chat chat = list.get(i).toObject(Chat.class);
                        if((chat.getId1().equals(id) && chat.getId2().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) || (chat.getId1().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && chat.getId2().equals(id))){
                            Log.d(TAG, "onComplete: condition matched");
                            chat_id = list.get(i).getId();
                            checkChat = false;
                            msgSendButton.setAlpha(1f);
                            msgSendButton.setEnabled(true);
                            checkMessages();

                        }
                        if(list.size() -1 == i && checkChat){
                            ZoneId tz =  ZoneId.systemDefault();
                            LocalDateTime localDateTime = LocalDateTime.now();
                            long seconds = localDateTime.atZone(tz).toEpochSecond();
                            int nanos = localDateTime.getNano();
                            Timestamp timestamp = new Timestamp(seconds, nanos);
                            chat_id = mDb.collection(getString(R.string.collection_chat)).document().getId();
                            checkMessages();
                            Chat chat1 = new Chat();
                            chat1.setId(chat_id);
                            chat1.setId1(FirebaseAuth.getInstance().getUid());
                            chat1.setId2(id);
                            chat1.setCategory(getString(R.string.individual_chat));
                            chat1.setTimestamp(timestamp);
                            mDb.collection(getString(R.string.collection_chat)).document(chat_id).set(chat1);
                            msgSendButton.setAlpha(1f);
                            msgSendButton.setEnabled(true);
                        }
                    }
                }if(task.getResult().size() == 0){
                    ZoneId tz =  ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                    int nanos = localDateTime.getNano();
                    Timestamp timestamp = new Timestamp(seconds, nanos);
                    chat_id = mDb.collection(getString(R.string.collection_chat)).document().getId();
                    checkMessages();
                    Chat chat1 = new Chat();
                    chat1.setId(chat_id);
                    chat1.setId1(FirebaseAuth.getInstance().getUid());
                    chat1.setId2(id);
                    chat1.setCategory(getString(R.string.individual_chat));
                    chat1.setTimestamp(timestamp);
                    mDb.collection(getString(R.string.collection_chat)).document(chat_id).set(chat1);
                    msgSendButton.setAlpha(1f);
                    msgSendButton.setEnabled(true);
                }
            }
        });

    }
    //*********************************************** clickSendMessage *************************************************
    private void clickSendMessage(){
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String msgContent = msgInputText.getText().toString();
                if(!TextUtils.isEmpty(msgContent))
                {
                    if(!chat_id.equals(getString(R.string.empty))){
                        ZoneId tz =  ZoneId.systemDefault();
                        LocalDateTime localDateTime = LocalDateTime.now();
                        long seconds = localDateTime.atZone(tz).toEpochSecond();
                        int nanos = localDateTime.getNano();
                        Timestamp timestamp = new Timestamp(seconds, nanos);
                        String m_id = mDb.collection(getString(R.string.collection_chat)).document(chat_id).collection(getString(R.string.collection_message)).document().getId();
                        final Message message = new Message();
                        message.setId(m_id);
                        message.setName(ownUser.getName());
                        message.setS_id(FirebaseAuth.getInstance().getUid());
                        message.setMessage(msgContent);
                        message.setTimestamp(timestamp);

                        Map<String,Object> map = new HashMap<>();
                        map.put(getString(R.string.timestamp),timestamp);

                        mDb.collection(getString(R.string.collection_chat)).document(chat_id).update(map);

                        mDb.collection(getString(R.string.collection_chat)).document(chat_id).collection(getString(R.string.collection_message)).document(m_id).set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        User user = task.getResult().toObject(User.class);
                                        sendNotification(user.getDevice_token(),msgContent);
                                    }
                                });
                            }
                        });

                    }


                    msgInputText.setText("");
                }
            }
        });

    }


    private void checkMessages(){
        if(!chat_id.equals(mContext.getString(R.string.empty))){


            mDb.collection(getString(R.string.collection_chat)).document(chat_id).collection(getString(R.string.collection_message)).orderBy(getString(R.string.timestamp)).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int i = 0;
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if(doc.getType() == DocumentChange.Type.ADDED){
                            Log.d(TAG, "onEvent: changed ocured");


                                    Message message = doc.getDocument().toObject(Message.class);
                                    Log.d(TAG, "onEvent: updated message" + doc.getDocument().get("message"));
                                    Log.d(TAG, "onEvent:  message " + message + message.getMessage());
                                    if(message.getS_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        // Add a new sent message to the list.
                                        Log.d(TAG, "onComplete: into the list");
                                        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, message.getMessage(),message.getTimestamp(),null);
                                        msgDtoList.add(msgDto);
                                        // Notify recycler view insert one new data.
                                        int newMsgPosition = msgDtoList.size() - 1;
                                        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                                        msgRecyclerView.scrollToPosition(newMsgPosition);
                                    }else{

                                            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, message.getMessage(),message.getTimestamp(),message.getName());


                                                    Log.d(TAG, "onEvent: into the else list");

                                                    msgDtoList.add(msgDto);
                                                    // Notify recycler view insert one new data.
                                                    int newMsgPosition = msgDtoList.size() - 1;
                                                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                                                    msgRecyclerView.scrollToPosition(newMsgPosition);

                                                    if(voice){
                                                        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.message);
                                                        mediaPlayer.start();
                                                    }




                                }

                        }

                        if(queryDocumentSnapshots.size()  == i + 1){
                            Log.d(TAG, "onEvent: called " + i);
                            checkMessage.clear();
                        }
                        i++;

                    }
                    voice = true;
                }
            });
        }
    }

    //*********************************************** send notification *****************************************************
    private void sendNotification(String token,String message) {

        JSONObject json = new JSONObject();

        try {

            json.put("to",token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            notificationObj.put("body", message) ;
            notificationObj.put("sound","default");
            json.put("notification",notificationObj);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("MUR", "onResponse: ");
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AIzaSyAMzSvgFOknCphW8nUgks6fmTi7zAhLdBg");

                    return header;
                }
            };
            mRequestQue.add(request);

        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }

    }
    //************************************************** setting recylerview ************************************************************
    private void settingRecyclerView(){
        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
    }

    //----------------------------------------------- Group Section ----------------------------------------------------------------------
    //*********************************************** setting Group message **********************************************************
    private void groupMessage(){

        mDb.collection(getString(R.string.collection_chat)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    final List<DocumentSnapshot> list = task.getResult().getDocuments();
                    Boolean checkChat = true;
                    for (int i = 0; i < list.size(); i++){
                        Log.d(TAG, "onComplete: into the for loop");
                        Chat chat = list.get(i).toObject(Chat.class);
                        if(ownUser.getH_id() != null){
                            if((chat.getId1().equals(id) && chat.getId2().equals(ownUser.getH_id()) || (chat.getId1().equals(ownUser.getH_id()) && chat.getId2().equals(id)))){
                                Log.d(TAG, "onComplete: condition matched");
                                chat_id = list.get(i).getId();
                                checkChat = false;
                                msgSendButton.setAlpha(1f);
                                msgSendButton.setEnabled(true);
                                checkMessages();

                            }
                            if(list.size() -1 == i && checkChat){
                                ZoneId tz =  ZoneId.systemDefault();
                                LocalDateTime localDateTime = LocalDateTime.now();
                                long seconds = localDateTime.atZone(tz).toEpochSecond();
                                int nanos = localDateTime.getNano();
                                Timestamp timestamp = new Timestamp(seconds, nanos);
                                chat_id = mDb.collection(getString(R.string.collection_chat)).document().getId();
                                checkMessages();
                                Chat chat1 = new Chat();
                                chat1.setId(chat_id);
                                chat1.setId1(ownUser.getH_id());
                                chat1.setId2(id);
                                chat1.setCategory(getString(R.string.group_chat));
                                chat1.setTimestamp(timestamp);
                                mDb.collection(getString(R.string.collection_chat)).document(chat_id).set(chat1);
                                msgSendButton.setAlpha(1f);
                                msgSendButton.setEnabled(true);
                            }
                        }

                    }
                }if(task.getResult().size() == 0){
                    ZoneId tz =  ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    long seconds = localDateTime.atZone(tz).toEpochSecond();
                    int nanos = localDateTime.getNano();
                    Timestamp timestamp = new Timestamp(seconds, nanos);
                    chat_id = mDb.collection(getString(R.string.collection_chat)).document().getId();
                    checkMessages();
                    Chat chat1 = new Chat();
                    chat1.setId(chat_id);
                    chat1.setId1(ownUser.getH_id());
                    chat1.setId2(id);
                    chat1.setCategory(getString(R.string.group_chat));
                    chat1.setTimestamp(timestamp);
                    mDb.collection(getString(R.string.collection_chat)).document(chat_id).set(chat1);
                    msgSendButton.setAlpha(1f);
                    msgSendButton.setEnabled(true);
                }
            }
        });

    }
    //*********************************************** clickSendMessage *************************************************
    private void clickSendGroupMessage(){
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String msgContent = msgInputText.getText().toString();
                if(!TextUtils.isEmpty(msgContent))
                {
                    if(!chat_id.equals(getString(R.string.empty))){
                        ZoneId tz =  ZoneId.systemDefault();
                        LocalDateTime localDateTime = LocalDateTime.now();
                        long seconds = localDateTime.atZone(tz).toEpochSecond();
                        int nanos = localDateTime.getNano();
                        Timestamp timestamp = new Timestamp(seconds, nanos);
                        String m_id = mDb.collection(getString(R.string.collection_chat)).document(chat_id).collection(getString(R.string.collection_message)).document().getId();
                        final Message message = new Message();
                        message.setId(m_id);
                        message.setName(ownUser.getName());
                        message.setS_id(FirebaseAuth.getInstance().getUid());
                        message.setMessage(msgContent);
                        message.setTimestamp(timestamp);

                        Map<String,Object> map = new HashMap<>();
                        map.put(getString(R.string.timestamp),timestamp);

                        mDb.collection(getString(R.string.collection_chat)).document(chat_id).update(map);

                        mDb.collection(getString(R.string.collection_chat)).document(chat_id).collection(getString(R.string.collection_message)).document(m_id).set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                               if(n_id != null){
                                   mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                           House house = task.getResult().toObject(House.class);
                                           if(house.getMembers() != null){
                                               if(house.getMembers().size() != 0){
                                                   for (int i = 0; i < house.getMembers().size(); i++){
                                                       mDb.collection(getString(R.string.collection_users)).document(house.getMembers().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                               User user = task.getResult().toObject(User.class);
                                                               Log.d(TAG, "onComplete: notification sent");
                                                               sendNotification(user.getDevice_token(),msgContent);
                                                           }
                                                       });
                                                   }
                                               }
                                           }
                                       }
                                   });
                               }
                            }
                        });

                    }


                    msgInputText.setText("");
                }
            }
        });

    }

    private void settingProfile(){
        if(ownUser != null){
            UniversalImageLoader.setImage(ownUser.getPublic_avatar(), mUserAvatar, null, "",mContext);
        }
        if(category != null){
            if(category.equals(getString(R.string.group_chat))){

                if(n_id != null && id != null){
                    mDb.collection(getString(R.string.collection_hoods)).document(n_id).collection(getString(R.string.collection_houses)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            House house = task.getResult().toObject(House.class);
                            if(house.getMembers() != null){
                                UniversalImageLoader.setImage(house.getImage(), mAvatar, null, "",mContext);
                                mName.setText(house.getName());
                            }
                        }
                    });

                    mAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(type != null){
                                Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                intent.putExtra(getString(R.string.house_id),id+n_id+type);
                                startActivity(intent);
                            }
                        }
                    });

                    mName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(type != null){
                                Intent intent = new Intent(mContext, MainHouseInfoActivity.class);
                                intent.putExtra(getString(R.string.house_id),id+n_id+type);
                                startActivity(intent);
                            }
                        }
                    });


                }

            }else if(category.equals(getString(R.string.individual_chat))){
                if(id != null){

                    mDb.collection(getString(R.string.collection_users)).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            User user = task.getResult().toObject(User.class);
                            if(type != null){

                                if(type.equals(getString(R.string.type_member))){
                                    UniversalImageLoader.setImage(user.getFamily_avatar(), mAvatar, null, "",mContext);
                                }
                                else if(type.equals(getString(R.string.type_relative))){
                                    UniversalImageLoader.setImage(user.getRel_avatar(), mAvatar, null, "",mContext);
                                }
                                else if(type.equals(getString(R.string.type_friend))){
                                    UniversalImageLoader.setImage(user.getFriend_avatar(), mAvatar, null, "",mContext);
                                }
                                else{
                                    UniversalImageLoader.setImage(user.getPublic_avatar(), mAvatar, null, "",mContext);
                                }
                            }
                            mName.setText(user.getName());
                        }
                    });

                    mAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(type != null){
                                Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                intent.putExtra(mContext.getString(R.string.intent_type),type);
                                intent.putExtra(mContext.getString(R.string.intent_memberid),id);
                                mContext.startActivity(intent);
                            }
                        }
                    });

                    mName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(type != null){
                                Intent intent = new Intent(mContext, MemberInfoActivity.class);
                                intent.putExtra(mContext.getString(R.string.intent_type),type);
                                intent.putExtra(mContext.getString(R.string.intent_memberid),id);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }
            }
        }
    }



}
