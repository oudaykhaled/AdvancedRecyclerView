package com.apps2u.stickyheadercursorrecycleradapter.adapter;


import android.webkit.URLUtil;



/**
 * Created by Ouday Khaled on 2/20/2018.
 */

public class ChatModel {

    public final static int MEDIATYPE = 0;
    public final static int TEXTTYPE = 1;
    public final static int TYPE_HEADER = 0;

    public final static int TEXTTYPE_RIGHT = 3;
    public final static int TEXTTYPE_LEFT = 4;

    public final static int IMAGETYPE_LEFT = 5;
    public final static int IMAGETYPE_RIGHT = 6;
    public final static int VIDEOTYPE_LEFT = 7;
    public final static int VIDEOTYPE_RIGHT = 8;
    public final static int AUDIOTYPE_LEFT = 9;
    public final static int AUDIOTYPE_RIGHT = 10;
    public final static int FILETYPE_LEFT = 11;
    public final static int FILETYPE_RIGHT = 12;
    public final static int LOCATION_RIGHT = 13;
    public final static int LOCATION_LEFT = 14;
    public final static int LINK_RIGHT = 15;
    public final static int LINK_LEFT = 16;


    public final static int IMAGE = 1;
    public final static int VIDEO = 2;
    public final static int AUDIO = 3;
    public final static int FILE = 4;
    public final static int LOCATION = 5;
    public final static int CONTACTS = 6;
    public final static int URL_TYPE = 7;

    private boolean isFromMe = false;
    private String message = "";
    private int chatType = 0;
    private String chatID = "";
    private int status = 0;
    private String jid;
    private String dateOfCreation;

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public boolean isFromMe() {
        return isFromMe;
    }

    public void setFromMe(boolean fromMe) {
        isFromMe = fromMe;
    }


    public void setStatus(int status) {
        if (this.status < status)
            this.status = status;
    }

    public int getStatus() {
        return status;
    }




    public static ChatModel createChatModel(String body, String id, String jid) {

        ChatModel chatModel = new ChatModel();
        chatModel.setMessage(body);
        chatModel.setChatID(id);
        chatModel.setJid(jid);
        chatModel.setDateOfCreation(System.currentTimeMillis() + "");
        chatModel.setChatType(TEXTTYPE);

        return chatModel;
    }




}
