package com.apps2u.stickyheadercursorrecycleradapter.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps2u.happiestrecyclerview.RecyclerView;
import com.apps2u.happiestrecyclerview.RecyclerViewAdapter;
import com.apps2u.happiestrecyclerview.ViewHolder;
import com.apps2u.stickyheadercursorrecycleradapter.R;

/**
 * Created by Ouday Khaled on 5/18/2018.
 */

public class  TestAdapter1 extends RecyclerViewAdapter<ViewHolder,ChatModel> {


    static final int TEXTTYPE = 1;
//    public StickyLayoutManager stickyLayoutManager;

    public TestAdapter1(Activity context, RecyclerView recyclerView) {
        super(context, recyclerView);

    }

    @Override
    public boolean attachAlwaysLastHeader() {
        return false;
    }


    @Override
    public void onBindViewHolders(ViewHolder viewHolder, int position) {
        switch (getItemType(position)) {
            case TEXTTYPE:
                ViewHolderText viewHolderText = (ViewHolderText) viewHolder;
                viewHolderText.text.setText((getData().get(position)).getMessage());
                viewHolderText.date.setText((getData().get(position)).getDateOfCreation());
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
        return TEXTTYPE;

    }

    @Override
    public int getHeaderLayout() {
        return R.layout.header_text_date;
    }

    @Override
    public int getFooterLayout() {
        return R.layout.test_footer;
    }

    @Override
    public boolean isStickyHeader() {
        return true;
    }

    @Override
    public int getOrientation() {
        return Companion.getVERTICAL();
    }

    @Override
    public String getSectionCondition(int position) {
        return (getData().get(position)).getChatID();
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
