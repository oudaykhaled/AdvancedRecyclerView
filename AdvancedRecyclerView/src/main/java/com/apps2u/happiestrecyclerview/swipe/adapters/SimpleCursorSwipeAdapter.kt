package com.apps2u.happiestrecyclerview.swipe.adapters

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup


import com.apps2u.happiestrecyclerview.swipe.SwipeLayout
import com.apps2u.happiestrecyclerview.swipe.implments.SwipeItemAdapterMangerImpl
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.apps2u.happiestrecyclerview.swipe.util.Attributes

import androidx.cursoradapter.widget.SimpleCursorAdapter

/**
 * Created by Ouday Khaled on 5/23/2018.
 */

abstract class SimpleCursorSwipeAdapter : SimpleCursorAdapter, SwipeItemMangerInterface, SwipeAdapterInterface {

    private val mItemManger = SwipeItemAdapterMangerImpl(this)

    override val openItems: List<Int>
        get() = mItemManger.openItems

    override val openLayouts: List<SwipeLayout>
        get() = mItemManger.openLayouts

    override var mode: Attributes.Mode
        get() = mItemManger.mode
        set(mode) {
            mItemManger.mode = mode
        }

    protected constructor(context: Context, layout: Int, c: Cursor, from: Array<String>, to: IntArray, flags: Int) : super(context, layout, c, from, to, flags) {}

    protected constructor(context: Context, layout: Int, c: Cursor, from: Array<String>, to: IntArray) : super(context, layout, c, from, to) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertViewIsNull = convertView == null
        val v = super.getView(position, convertView, parent)
        if (convertViewIsNull) {
            mItemManger.initialize(v, position)
        } else {
            mItemManger.updateConvertView(v, position)
        }
        return v
    }

    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun closeItem(position: Int) {
        mItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        mItemManger.closeAllExcept(layout)
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mItemManger.isOpen(position)
    }
}
