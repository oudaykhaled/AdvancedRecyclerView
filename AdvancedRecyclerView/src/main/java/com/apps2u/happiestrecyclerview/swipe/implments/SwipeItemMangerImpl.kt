package com.apps2u.happiestrecyclerview.swipe.implments

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.BaseAdapter


import com.apps2u.happiestrecyclerview.swipe.SimpleSwipeListener
import com.apps2u.happiestrecyclerview.swipe.SwipeLayout
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.apps2u.happiestrecyclerview.swipe.util.Attributes

import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet

/**
 * Created by Ouday Khaled on 5/23/2018.
 */

abstract class SwipeItemMangerImpl : SwipeItemMangerInterface {

    override var mode: Attributes.Mode = Attributes.Mode.Single
        set(mode) {
            field = mode
            mOpenPositions.clear()
            mShownLayouts.clear()
            mOpenPosition = INVALID_POSITION
        }
    val INVALID_POSITION = -1

    protected var mOpenPosition = INVALID_POSITION

    protected var mOpenPositions: MutableSet<Int> = HashSet()
    protected var mShownLayouts: MutableSet<SwipeLayout> = HashSet()

    protected var mBaseAdapter: BaseAdapter? = null
    protected var mRecyclerAdapter: RecyclerView.Adapter<*>? = null

    override val openItems: List<Int>
        get() = if (this.mode == Attributes.Mode.Multiple) {
            ArrayList(mOpenPositions)
        } else {
            Arrays.asList(mOpenPosition)
        }

    override val openLayouts: List<SwipeLayout>
        get() = ArrayList(mShownLayouts)

    constructor(adapter: BaseAdapter?) {
        kotlin.requireNotNull(adapter) { "Adapter can not be null" }

        kotlin.require(adapter is SwipeItemMangerInterface) { "adapter should implement the SwipeAdapterInterface" }

        this.mBaseAdapter = adapter
    }

    constructor(adapter: RecyclerView.Adapter<*>?) {
        kotlin.requireNotNull(adapter) { "Adapter can not be null" }

        kotlin.require(adapter is SwipeItemMangerInterface) { "adapter should implement the SwipeAdapterInterface" }

        this.mRecyclerAdapter = adapter
    }

    /* initialize and updateConvertView used for AdapterManagerImpl */
    abstract fun initialize(target: View, position: Int)

    abstract fun updateConvertView(target: View, position: Int)

    /* bindView used for RecyclerViewManagerImpl */
    abstract fun bindView(target: View, position: Int)

    fun getSwipeLayoutId(position: Int): Int {
        return if (mBaseAdapter != null) {
            (mBaseAdapter as SwipeAdapterInterface).getSwipeLayoutResourceId(position)
        } else if (mRecyclerAdapter != null) {
            (mRecyclerAdapter as SwipeAdapterInterface).getSwipeLayoutResourceId(position)
        } else {
            -1
        }
    }

    override fun openItem(position: Int) {
        if (this.mode == Attributes.Mode.Multiple) {
            if (!mOpenPositions.contains(position))
                mOpenPositions.add(position)
        } else {
            mOpenPosition = position
        }
        if (mBaseAdapter != null) {
            mBaseAdapter!!.notifyDataSetChanged()
        } else if (mRecyclerAdapter != null) {
            mRecyclerAdapter!!.notifyDataSetChanged()
        }
    }

    override fun closeItem(position: Int) {
        if (this.mode == Attributes.Mode.Multiple) {
            mOpenPositions.remove(position)
        } else {
            if (mOpenPosition == position)
                mOpenPosition = INVALID_POSITION
        }
        if (mBaseAdapter != null) {
            mBaseAdapter!!.notifyDataSetChanged()
        } else if (mRecyclerAdapter != null) {
            mRecyclerAdapter!!.notifyDataSetChanged()
        }
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        for (s in mShownLayouts) {
            if (s !== layout)
                s.close()
        }
    }

    override fun closeAllItems() {
        if (this.mode == Attributes.Mode.Multiple) {
            mOpenPositions.clear()
        } else {
            mOpenPosition = INVALID_POSITION
        }
        for (s in mShownLayouts) {
            s.close()
        }
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mShownLayouts.remove(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return if (this.mode == Attributes.Mode.Multiple) {
            mOpenPositions.contains(position)
        } else {
            mOpenPosition == position
        }
    }

    internal inner class ValueBox(var position: Int, var swipeMemory: SwipeMemory, var onLayoutListener: SwipeItemMangerImpl.OnLayoutListener)

    class OnLayoutListener(private var position: Int, private var swipeManager: SwipeItemMangerInterface) : SwipeLayout.OnLayout {

        fun setPosition(position: Int) {
            this.position = position
        }

        override fun onLayout(v: SwipeLayout) {
            if (swipeManager.isOpen(position)) {
                v.open(false, false)
            } else {
                v.close(false, false)
            }
        }

    }

    internal inner class SwipeMemory(private var position: Int) : SimpleSwipeListener() {

        override fun onClose(layout: SwipeLayout) {
            if (mode == Attributes.Mode.Multiple) {
                mOpenPositions.remove(position)
            } else {
                mOpenPosition = INVALID_POSITION
            }
        }

        override fun onStartOpen(layout: SwipeLayout) {
            if (mode == Attributes.Mode.Single) {
                closeAllExcept(layout)
            }
        }

        override fun onOpen(layout: SwipeLayout) {
            if (mode == Attributes.Mode.Multiple)
                mOpenPositions.add(position)
            else {
                closeAllExcept(layout)
                mOpenPosition = position
            }
        }

        fun setPosition(position: Int) {
            this.position = position
        }
    }

}

