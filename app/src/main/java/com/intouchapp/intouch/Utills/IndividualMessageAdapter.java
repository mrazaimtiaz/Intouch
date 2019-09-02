package com.intouchapp.intouch.Utills;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intouchapp.intouch.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class IndividualMessageAdapter  extends RecyclerView.Adapter<IndividualMessageAdapter.ChatAppMsgViewHolder> {

    private List<ChatAppMsgDTO> msgDtoList = null;

    private static final String TAG = "IndividualMessageAdapte";

    private Context mContext;

    public IndividualMessageAdapter(List<ChatAppMsgDTO> msgDtoList, Context mContext) {
        this.msgDtoList = msgDtoList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        ChatAppMsgDTO msgDto = this.msgDtoList.get(position);
        // If the message is a received message.
        holder.timeSender.setAlpha(0.5f);
        holder.timeReciver.setAlpha(0.5f);
        if(msgDto.MSG_TYPE_RECEIVED.equals(msgDto.getMsgType()))
        {
            long seconds = (System.currentTimeMillis() / 1000) - msgDto.getTimeStamp().getSeconds();
            Log.d(TAG, "onBindViewHolder: " + seconds);
            long diffMinutes = seconds / 60;
            long diffHours = diffMinutes / 60;
            long diffDays = diffHours / 24;

            if(seconds < 60){
                holder.timeSender.setText (seconds + mContext.getString(R.string.s_ago));
            }else if(diffMinutes < 60){
                holder.timeSender.setText(diffMinutes +  mContext.getString(R.string.m_ago));
            }else if(diffHours < 24){
                holder.timeSender.setText(diffHours + mContext.getString(R.string.h_ago));
            }else if(diffHours < 48){
                holder.timeSender.setText(mContext.getString(R.string.yesterday));
            }
            else{
                holder.timeSender.setText(diffDays + mContext.getString(R.string.d_ago));
            }
            if(msgDto.getName() != null){
                Log.d(TAG, "onBindViewHolder: into the name");
                holder.name.setText(msgDto.getName());
            }
            // Show received message in left linearlayout.
            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.leftMsgTextView.setText(msgDto.getMsgContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        // If the message is a sent message.
        else if(msgDto.MSG_TYPE_SENT.equals(msgDto.getMsgType()))
        {
            long seconds = (System.currentTimeMillis() / 1000) - msgDto.getTimeStamp().getSeconds();
            Log.d(TAG, "onBindViewHolder: " + seconds);
            long diffMinutes = seconds / 60;
            long diffHours = diffMinutes / 60;
            long diffDays = diffHours / 24;
            Log.d(TAG, "onBindViewHolder: "+ msgDto.getTimeStamp());
            if(seconds < 60){
                holder.timeReciver.setText (seconds + mContext.getString(R.string.s_ago));
            }else if(diffMinutes < 60){
                holder.timeReciver.setText(diffMinutes +  mContext.getString(R.string.m_ago));
            }else if(diffHours < 24){
                holder.timeReciver.setText(diffHours + mContext.getString(R.string.h_ago));
            }else if(diffHours < 48){
                holder.timeReciver.setText(mContext.getString(R.string.yesterday));
            }
            else{
                holder.timeReciver.setText(diffDays + mContext.getString(R.string.d_ago));
            }

            // Show sent message in right linearlayout.
            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.rightMsgTextView.setText(msgDto.getMsgContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
        }

    }

    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recyclerview_message, parent, false);
        return new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(msgDtoList==null)
        {
            msgDtoList = new ArrayList<ChatAppMsgDTO>();
        }
        return msgDtoList.size();
    }

    public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout leftMsgLayout;

        ConstraintLayout rightMsgLayout;

        TextView leftMsgTextView;

        TextView rightMsgTextView;

        TextView timeSender,timeReciver;
        TextView name;


        public ChatAppMsgViewHolder(View itemView) {
            super(itemView);

            if(itemView!=null) {
                leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout);
                rightMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_right_msg_layout);
                leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
                rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);

                timeSender = (TextView) itemView.findViewById(R.id.timesender);
                timeReciver = (TextView) itemView.findViewById(R.id.time);

                name = (TextView) itemView.findViewById(R.id.name);

            }
        }
    }
}