package com.intouchapp.intouch.Utills;

import com.google.firebase.Timestamp;

public class ChatAppMsgDTO {

        public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

        public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

        // Message content.
        private String msgContent;

        private Timestamp timeStamp;

        private String name;

        // Message type.
        private String msgType;

        public ChatAppMsgDTO(String msgType, String msgContent, Timestamp timestamp,String name) {
            this.msgType = msgType;
            this.msgContent = msgContent;
            this.timeStamp = timestamp;
            this.name = name;
        }

        String getMsgContent() {
            return msgContent;
        }

        public void setMsgContent(String msgContent) {
            this.msgContent = msgContent;
        }

        String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

    Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

