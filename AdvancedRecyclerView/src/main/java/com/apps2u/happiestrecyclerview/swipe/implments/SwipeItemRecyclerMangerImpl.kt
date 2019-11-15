package com.apps2u.happiestrecyclerview.swipe.implments

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.apps2u.happiestrecyclerview.swipe.SwipeLayout


/**
 * Created by Ouday Khaled on 5/23/2018.
 */

class SwipeItemRecyclerMangerImpl(protected var mAdapter: RecyclerView.Adapter<*>) : SwipeItemMangerImpl(mAdapter) {

    override fun bindView(target: View, position: Int) {
        val resId = getSwipeLayoutId(position)

        val onLayoutListener = OnLayoutListener(position, this)
        val swipeLayout = target.findViewById<View>(resId) as SwipeLayout

        if (swipeLayout.getTag(resId) == null) {
            val swipeMemory = SwipeMemory(position)
            swipeLayout.addSwipeListener(swipeMemory)
            swipeLayout.addOnLayoutListener(onLayoutListener)
            swipeLayout.setTag(resId, ValueBox(position, swipeMemory, onLayoutListener))
            mShownLayouts.add(swipeLayout)
        } else {
            val valueBox = swipeLayout.getTag(resId) as SwipeItemMangerImpl.ValueBox
            valueBox.swipeMemory.setPosition(position)
            valueBox.onLayoutListener.setPosition(position)
            valueBox.position = position
        }
    }

    override fun initialize(target: View, position: Int) {

    }

    override fun updateConvertView(target: View, position: Int) {

    }

}
