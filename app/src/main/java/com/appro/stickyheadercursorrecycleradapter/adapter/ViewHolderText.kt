package com.appro.stickyheadercursorrecycleradapter.adapter

import android.view.View

import androidx.appcompat.widget.AppCompatTextView

import com.appro.advancedrecyclerview.ViewHolder
import com.appro.stickyheadercursorrecycleradapter.R


/**
 * Created by ama on 2/23/2016.
 */

class ViewHolderText(parent: View) : ViewHolder(parent) {


    var text: AppCompatTextView

    var date: AppCompatTextView


    init {
        text = parent.findViewById(R.id.text)
        date = parent.findViewById(R.id.date)
    }


}