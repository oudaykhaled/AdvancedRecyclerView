package com.apps2u.stickyheadercursorrecycleradapter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.apps2u.stickyheadercursorrecycleradapter.ApplicationContext;


/**
 * Created by koa on 6/22/2016.
 */
public class MyContentProvider extends ContentProvider {

    MyDatabase mDatabaseHelper;
    public static final String CONTENT_AUTHORITY = ApplicationContext.getInstance().getPackageName();
    /**
     * Content authority for this provider.
     */
    public static final String AUTHORITY = CONTENT_AUTHORITY;

    /**
     * URI ID for route: /entries
     */
    public static final int ROUTE_ENTRIES = 1;

    /**
     * URI ID for route: /entries/{ID}
     */
    public static final int ROUTE_ENTRIES_ID = 2;

    public static final int ROUTE_MEDIA = 3;
    public static final int ROUTE_MEDIA_ID = 4;

    public static final int ROUTE_LINK = 5;
    public static final int ROUTE_LINK_ID = 6;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(AUTHORITY, ChatContract.PATH_ENTRIES, ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, ChatContract.PATH_ENTRIES + "/*", ROUTE_ENTRIES_ID);


    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MyDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {





            case ROUTE_ENTRIES:
                return ChatContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return ChatContract.Entry.CONTENT_ITEM_TYPE;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    /**
     * Perform a database query by URI.
     * <p>
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        Context ctx = getContext();
        String id;
        Cursor c;

        switch (uriMatch) {





            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                id = uri.getLastPathSegment();
                builder.where(ChatContract.Entry._ID + "=?", id);
            case ROUTE_ENTRIES:
                // Return all known entries.
                builder.table(ChatContract.Entry.TABLE_NAME).where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        long id;

        switch (match) {



            case ROUTE_ENTRIES:
                id = db.insertOrThrow(ChatContract.Entry.TABLE_NAME, null, values);
                result = Uri.parse(ChatContract.Entry.CONTENT_URI + "/" + id);
                break;

            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;

        switch (match) {


            case ROUTE_ENTRIES:
                count = builder.table(ChatContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_ID:
                id = uri.getLastPathSegment();
                count = builder.table(ChatContract.Entry.TABLE_NAME)
                        .where(ChatContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;

        switch (match) {




            case ROUTE_ENTRIES:
                count = builder.table(ChatContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                id = uri.getLastPathSegment();
                count = builder.table(ChatContract.Entry.TABLE_NAME)
                        .where(ChatContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * SQLite backend for @{link MyContentProvider}.
     * <p>
     * Provides access to an disk-backed, SQLite datastore which is utilized by MyContentProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    static public class MyDatabase extends SQLiteOpenHelper {
        /**
         * Schema version.
         */
        public static final int DATABASE_VERSION = 13;
        /**
         * Filename for SQLite file.
         */
        public static final String DATABASE_NAME = "Apps2uCHAT.db";


        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String COMMA_SEP = ",";






// TODO: 2/22/2018 create table  querie
        private static final String SQL_CREATE_CHAT =
                "CREATE TABLE " + ChatContract.Entry.TABLE_NAME + " (" +
                        ChatContract.Entry._ID + " INTEGER PRIMARY KEY," +
                        ChatContract.Entry.COLUMN_NAME_CHAT_ID                        + TYPE_TEXT      + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_CHAT_MESSAGE                   + TYPE_TEXT      + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_CHAT_TYPE                      + TYPE_INTEGER   + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_CHAT_STATUS                    + TYPE_TEXT      + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_CHAT_IS_FROM_ME                + TYPE_INTEGER   + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_CHAT_JID                       + TYPE_TEXT      + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_CHAT_CREATED                   + TYPE_TEXT      + COMMA_SEP +
                        ChatContract.Entry.COLUMN_NAME_MESSAGE_IS_READ                + TYPE_TEXT      + COMMA_SEP +
//
//
                        " UNIQUE(" + ChatContract.Entry.COLUMN_NAME_CHAT_ID + "," + ChatContract.Entry.COLUMN_NAME_CHAT_JID + ") ON CONFLICT REPLACE" + ");";




        // TODO: 2/22/2018      delete tables queries
        private static final String SQL_DELETE_CHAT = "DROP TABLE IF EXISTS " + ChatContract.Entry.TABLE_NAME;

        /**
         * SQL statement to drop "follow" table.
         */


        public MyDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_CHAT);
                }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over

            db.execSQL(SQL_DELETE_CHAT);
             onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_CHAT);
              onCreate(db);
        }
    }

}
