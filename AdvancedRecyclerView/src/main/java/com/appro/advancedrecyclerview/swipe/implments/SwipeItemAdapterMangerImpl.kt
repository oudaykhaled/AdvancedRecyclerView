package com.appro.advancedrecyclerview.swipe.implments

import android.view.View
import android.widget.BaseAdapter

import com.appro.advancedrecyclerview.swipe.SwipeLayout


/**
 * Created by Ouday Khaled on 5/23/2018.
 */

class SwipeItemAdapterMangerImpl(protected var mAdapter: BaseAdapter) : SwipeItemMangerImpl(mAdapter) {

    override fun initialize(target: View, position: Int) {
        val resId = getSwipeLayoutId(position)

        val onLayoutListener = OnLayoutListener(position, this)
        val swipeLayout = target.findViewById<View>(resId) as SwipeLayout

        val swipeMemory = SwipeMemory(position)
        swipeLayout.addSwipeListener(swipeMemory)
        swipeLayout.addOnLayoutListener(onLayoutListener)
        swipeLayout.setTag(resId, ValueBox(position, swipeMemory, onLayoutListener))

        mShownLayouts.add(swipeLayout)
    }

    override fun updateConvertView(target: View, position: Int) {
        val resId = getSwipeLayoutId(position)

        val swipeLayout = target.findViewById<View>(resId) as SwipeLayout
                ?: throw IllegalStateException("can not find SwipeLayout in target view")

        val valueBox = swipeLayout.getTag(resId) as SwipeItemMangerImpl.ValueBox
        valueBox.swipeMemory.setPosition(position)
        valueBox.onLayoutListener.setPosition(position)
        valueBox.position = position
    }

    override fun bindView(target: View, position: Int) {

    }

}

