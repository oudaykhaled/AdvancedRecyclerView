package com.apps2u.stickyheadercursorrecycleradapter.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps2u.happiestrecyclerview.CursorRecyclerViewAdapter;
import com.apps2u.happiestrecyclerview.RecyclerView;
import com.apps2u.happiestrecyclerview.ViewHolder;
import com.apps2u.stickyheadercursorrecycleradapter.R;
import com.apps2u.stickyheadercursorrecycleradapter.provider.ChatContract;

/**
 * Created by Ouday Khaled on 5/18/2018.
 */

public class TestAdapter extends CursorRecyclerViewAdapter<ViewHolder> {


    static final int TEXTTYPE = 1;
//    public StickyLayoutManager stickyLayoutManager;

    public TestAdapter(Activity context, RecyclerView recyclerView) {
        super(context, null, recyclerView);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        switch (getItemType(position)) {
            case TEXTTYPE:
                ViewHolderText viewHolderText = (ViewHolderText) viewHolder;
                viewHolderText.setData(cursor, position);
                break;

        }
    }

    @Override
    public ViewHolder onCreateViewHolders(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = null;
        switch (viewType) {
            case TEXTTYPE:
                v = layoutInflater.inflate(R.layout.bubble_text_left, parent, false);
                return new ViewHolderText(v);
            default:
                return null;

        }
    }

    @Override
    public int getItemType(int position) {
        getCursor().moveToPosition(position);
        return getCursor().getInt(getCursor().getColumnIndex(ChatContract.Entry.COLUMN_NAME_CHAT_TYPE));

    }

    @Override
    public int getHeaderLayout() {
        return R.layout.header_text_date;
    }

    @Override
    public boolean isStickyHeader() {
        return true;
    }

    @Override
    public boolean attachAlwaysLastHeader() {
        return false;
    }

    @Override
    public int getOrientation() {
        return Companion.getVERTICAL();
    }

    @Override
    public String getSectionCondition(Cursor c) {
        return c.getString(c.getColumnIndex(ChatContract.Entry.COLUMN_NAME_CHAT_ID));
    }

    @Override
    public boolean isSection() {
        return true;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }


//    @Override
//    public StickyLayoutManager getStickyLayoutManager() {
//        return stickyLayoutManager;
//    }

}
