package com.appro.stickyheadercursorrecycleradapter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.appro.advancedrecyclerview.RecyclerView
import com.appro.advancedrecyclerview.RecyclerViewAdapter
import com.appro.advancedrecyclerview.ViewHolder
import com.appro.stickyheadercursorrecycleradapter.R

/**
 * Created by Ouday Khaled on 5/18/2018.
 */

class TestAdapter(context: Activity, recyclerView: RecyclerView<*>) : RecyclerViewAdapter<ViewHolder, ChatModel>(context, recyclerView) {

    override val headerLayout: Int
        get() = R.layout.header_text_date

    override val footerLayout: Int
        get() = R.layout.test_footer

    override val isStickyHeader: Boolean
        get() = true

    override val orientation: Int
        get() = RecyclerViewAdapter.Companion.VERTICAL

    override val isSection: Boolean
        get() = true

    override fun attachAlwaysLastHeader(): Boolean {
        return false
    }


    override fun onBindViewHolders(viewHolder: ViewHolder, position: Int) {
        when (getItemType(position)) {
            TEXTTYPE -> {
                val viewHolderText = viewHolder as ViewHolderText
                viewHolderText.text.text = data!![position].message
                viewHolderText.date.text = data!![position].dateOfCreation
            }
        }
    }

    override fun onCreateViewHolders(parent: ViewGroup, viewType: Int): ViewHolder? {
        val layoutInflater = LayoutInflater.from(parent.context)
        var v: View? = null
        when (viewType) {
            TEXTTYPE -> {
                v = layoutInflater.inflate(R.layout.bubble_text_left, parent, false)
                return ViewHolderText(v)
            }

            else -> return null
        }
    }

    override fun getItemType(position: Int): Int {
        return TEXTTYPE

    }

    override fun getSectionCondition(position: Int): String {
        return data!![position].chatID
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return 0
    }

    companion object {


        internal val TEXTTYPE = 1
    }


}
