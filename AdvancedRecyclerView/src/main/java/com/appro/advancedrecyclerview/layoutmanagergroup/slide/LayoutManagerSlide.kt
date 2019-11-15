package com.appro.advancedrecyclerview.layoutmanagergroup.slide


import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

class LayoutManagerSlide(recyclerView: RecyclerView, itemTouchHelper: ItemTouchHelper) : RecyclerView.LayoutManager() {

    private var mRecyclerView: RecyclerView? = null
    private var mItemTouchHelper: ItemTouchHelper? = null

    private val mOnTouchListener = View.OnTouchListener { v, event ->
        val childViewHolder = mRecyclerView?.getChildViewHolder(v)
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            childViewHolder?.let { mItemTouchHelper?.startSwipe(it) }
        }
        false
    }

    init {
        this.mRecyclerView = checkIsNull(recyclerView)
        this.mItemTouchHelper = checkIsNull(itemTouchHelper)
    }

    private fun <T> checkIsNull(t: T?): T {
        if (t == null) {
            throw NullPointerException()
        }
        return t
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        detachAndScrapAttachedViews(recycler!!)
        val itemCount = itemCount
        if (itemCount > ItemConfig.DEFAULT_SHOW_ITEM) {
            for (position in ItemConfig.DEFAULT_SHOW_ITEM downTo 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val widthSpace = width - getDecoratedMeasuredWidth(view)
                val heightSpace = height - getDecoratedMeasuredHeight(view)
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 5,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 5 + getDecoratedMeasuredHeight(view))

                if (position == ItemConfig.DEFAULT_SHOW_ITEM) {
                    view.scaleX = 1 - (position - 1) * ItemConfig.DEFAULT_SCALE
                    view.scaleY = 1 - (position - 1) * ItemConfig.DEFAULT_SCALE
                    view.translationY = ((position - 1) * view.measuredHeight / ItemConfig.DEFAULT_TRANSLATE_Y).toFloat()
                } else if (position > 0) {
                    view.scaleX = 1 - position * ItemConfig.DEFAULT_SCALE
                    view.scaleY = 1 - position * ItemConfig.DEFAULT_SCALE
                    view.translationY = (position * view.measuredHeight / ItemConfig.DEFAULT_TRANSLATE_Y).toFloat()
                } else {
                    view.setOnTouchListener(mOnTouchListener)
                }
            }
        } else {
            for (position in itemCount - 1 downTo 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val widthSpace = width - getDecoratedMeasuredWidth(view)
                val heightSpace = height - getDecoratedMeasuredHeight(view)
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 5,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 5 + getDecoratedMeasuredHeight(view))

                if (position > 0) {
                    view.scaleX = 1 - position * ItemConfig.DEFAULT_SCALE
                    view.scaleY = 1 - position * ItemConfig.DEFAULT_SCALE
                    view.translationY = (position * view.measuredHeight / ItemConfig.DEFAULT_TRANSLATE_Y).toFloat()
                } else {
                    view.setOnTouchListener(mOnTouchListener)
                }
            }
        }
    }

}
