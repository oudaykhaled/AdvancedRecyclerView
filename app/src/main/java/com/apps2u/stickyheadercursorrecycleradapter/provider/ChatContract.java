/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apps2u.stickyheadercursorrecycleradapter.provider;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.BaseColumns;

import com.apps2u.stickyheadercursorrecycleradapter.ApplicationContext;
import com.apps2u.stickyheadercursorrecycleradapter.adapter.ChatModel;

import java.util.ArrayList;


public class ChatContract {

    private ChatContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = ApplicationContext.getInstance().getPackageName();

    /**
     * Base URI. (content://com.koa.sakrrealestate)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    public static final String PATH_ENTRIES = "chats";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.chat.entries";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.chat.entrie";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "Chat";
        /**
         * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
         */


/////// colons name
        public static final String COLUMN_NAME_CHAT_ID = "idChat";
        public static final String COLUMN_NAME_CHAT_MESSAGE = "message";
        public static final String COLUMN_NAME_CHAT_TYPE = "type";       //if message or media
        public static final String COLUMN_NAME_CHAT_STATUS = "status";     // read, pending, delivered
        public static final String COLUMN_NAME_CHAT_IS_FROM_ME = "isMe";
        public static final String COLUMN_NAME_CHAT_JID = "JID";
        public static final String COLUMN_NAME_CHAT_CREATED = "creationDate";
        public static final String COLUMN_NAME_MESSAGE_IS_READ = "msgIsRead";


    }


    public synchronized static void insertChat(ChatModel chat, Context context) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        batch.add(ContentProviderOperation.newInsert(Entry.CONTENT_URI)
                .withValue(Entry.COLUMN_NAME_CHAT_ID, chat.getChatID())
                .withValue(Entry.COLUMN_NAME_CHAT_MESSAGE, chat.getMessage())
                .withValue(Entry.COLUMN_NAME_CHAT_TYPE, chat.getChatType())
                .withValue(Entry.COLUMN_NAME_CHAT_STATUS, chat.getStatus())
                .withValue(Entry.COLUMN_NAME_CHAT_IS_FROM_ME, chat.isFromMe())
                .withValue(Entry.COLUMN_NAME_CHAT_JID, chat.getJid())
                .withValue(Entry.COLUMN_NAME_CHAT_CREATED, chat.getDateOfCreation())
                .withValue(Entry.COLUMN_NAME_MESSAGE_IS_READ, "0")
                .build());


        try {
            context.getContentResolver().applyBatch(ChatContract.CONTENT_AUTHORITY, batch);
        } catch (RemoteException remoteEx) {
            remoteEx.printStackTrace();
        } catch (OperationApplicationException operationAppEx) {
            operationAppEx.printStackTrace();
        }


        context.getContentResolver().notifyChange(
                Entry.CONTENT_URI,
                null,
                false);
    }


    static public void setAllMessageRead(final Context context, final String jid) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Entry.COLUMN_NAME_MESSAGE_IS_READ, 1);
                context.getContentResolver().update(Entry.CONTENT_URI, contentValues, Entry.COLUMN_NAME_CHAT_JID + " = '" + jid + "'", null);
            }
        });


    }

    // TODO: 2/22/2018 create functions or queries specified for this table
    static public void clearData(Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Entry.CONTENT_URI;
        contentResolver.delete(uri, null, null);
    }






}