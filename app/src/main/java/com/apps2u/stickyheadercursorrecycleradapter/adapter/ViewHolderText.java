package com.apps2u.stickyheadercursorrecycleradapter.adapter;

import android.database.Cursor;
import android.view.View;

import com.apps2u.happiestrecyclerview.RecyclerView;
import com.apps2u.happiestrecyclerview.ViewHolder;
import com.apps2u.stickyheadercursorrecycleradapter.MainActivity;
import com.apps2u.stickyheadercursorrecycleradapter.R;
import com.apps2u.stickyheadercursorrecycleradapter.provider.ChatContract;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * Created by ama on 2/23/2016.
 */

public  class ViewHolderText extends ViewHolder {


    public AppCompatTextView text;

    public AppCompatTextView date;


    public ViewHolderText(final View parent) {
        super(parent);
        text = parent.findViewById(R.id.text);
        date = parent.findViewById(R.id.date);
    }



    public static ViewHolderText newInstance(View parent) {
        return new ViewHolderText(parent);//onRecyclerItemClickListerner
    }

        int cursorPosition;
    public void setData(Cursor cursor, int cursorPosition){
        this.cursorPosition = getItemPosition();
        cursor.moveToPosition(cursorPosition);
        text.setText(cursor.getString(cursor.getColumnIndex(ChatContract.Entry.COLUMN_NAME_CHAT_MESSAGE)));
        date.setText(MainActivity.getTimeString(Long.parseLong(cursor.getString(cursor.getColumnIndex(ChatContract.Entry.COLUMN_NAME_CHAT_CREATED)))));
    }



}