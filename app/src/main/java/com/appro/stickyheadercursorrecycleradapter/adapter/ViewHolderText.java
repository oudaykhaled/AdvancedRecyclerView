package com.appro.stickyheadercursorrecycleradapter.adapter;

import android.database.Cursor;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.appro.advancedrecyclerview.ViewHolder;
import com.appro.stickyheadercursorrecycleradapter.MainActivity;
import com.appro.stickyheadercursorrecycleradapter.R;
import com.appro.stickyheadercursorrecycleradapter.provider.ChatContract;


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