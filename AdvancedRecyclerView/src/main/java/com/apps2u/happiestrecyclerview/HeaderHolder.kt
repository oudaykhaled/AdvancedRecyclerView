package com.apps2u.happiestrecyclerview

import android.view.View
import android.widget.TextView

class HeaderHolder(itemView: View) : ViewHolder(itemView) {
    var mTextView: TextView

    init {
        mTextView = itemView.findViewById<View>(R.id.text) as TextView
    }

    fun setHeader(header: String) {
        mTextView.text = header
    }

    fun setVisibility(visibility: Boolean) {
        if (visibility)
            mTextView.visibility = View.VISIBLE
        else
            mTextView.visibility = View.INVISIBLE
    }
}
