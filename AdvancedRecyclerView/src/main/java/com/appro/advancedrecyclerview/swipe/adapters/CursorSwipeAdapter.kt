package com.appro.advancedrecyclerview.swipe.adapters

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.appro.advancedrecyclerview.swipe.SwipeLayout
import com.appro.advancedrecyclerview.swipe.implments.SwipeItemAdapterMangerImpl
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.appro.advancedrecyclerview.swipe.util.Attributes

/**
 * Created by Ouday Khaled on 5/23/2018.
 */

abstract class CursorSwipeAdapter : CursorAdapter, SwipeItemMangerInterface, SwipeAdapterInterface {

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

    protected constructor(context: Context, c: Cursor, autoRequery: Boolean) : super(context, c, autoRequery) {}

    protected constructor(context: Context, c: Cursor, flags: Int) : super(context, c, flags) {}

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
