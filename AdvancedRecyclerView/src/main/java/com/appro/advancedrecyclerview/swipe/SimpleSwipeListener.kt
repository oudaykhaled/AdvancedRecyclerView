package com.appro.advancedrecyclerview.swipe


/**
 * Created by Ouday Khaled on 5/23/2018.
 */

open class SimpleSwipeListener : SwipeLayout.SwipeListener {

    override fun onStartOpen(layout: SwipeLayout) {}

    override fun onOpen(layout: SwipeLayout) {}

    override fun onStartClose(layout: SwipeLayout) {}

    override fun onClose(layout: SwipeLayout) {}

    override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}

    override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
}
