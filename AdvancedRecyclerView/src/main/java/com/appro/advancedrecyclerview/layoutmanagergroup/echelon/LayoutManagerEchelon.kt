package com.appro.advancedrecyclerview.layoutmanagergroup.echelon

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

class LayoutManagerEchelon(private val mContext: Context) : RecyclerView.LayoutManager() {
    private var mItemViewWidth: Int = 0
    private var mItemViewHeight: Int = 0
    private var mItemCount: Int = 0
    private var mScrollOffset = Integer.MAX_VALUE
    private val mScale = 0.9f

    /**
     * 获取RecyclerView的显示高度
     */
    val verticalSpace: Int
        get() = height - paddingTop - paddingBottom

    /**
     * 获取RecyclerView的显示宽度
     */
    val horizontalSpace: Int
        get() = width - paddingLeft - paddingRight

    init {
        mItemViewWidth = (horizontalSpace * 0.87f).toInt()//item的宽
        mItemViewHeight = (mItemViewWidth * 1.46f).toInt()//item的高
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        if (state.itemCount == 0 || state.isPreLayout) return
        removeAndRecycleAllViews(recycler!!)

        mItemViewWidth = (horizontalSpace * 0.87f).toInt()
        mItemViewHeight = (mItemViewWidth * 1.46f).toInt()
        mItemCount = itemCount
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset), mItemCount * mItemViewHeight)

        layoutChild(recycler)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val pendingScrollOffset = mScrollOffset + dy
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset + dy), mItemCount * mItemViewHeight)
        layoutChild(recycler)
        return mScrollOffset - pendingScrollOffset + dy
    }


    override fun canScrollVertically(): Boolean {
        return true
    }


    private fun layoutChild(recycler: RecyclerView.Recycler?) {
        if (itemCount == 0) return
        var bottomItemPosition = Math.floor((mScrollOffset / mItemViewHeight).toDouble()).toInt()
        var remainSpace = verticalSpace - mItemViewHeight

        val bottomItemVisibleHeight = mScrollOffset % mItemViewHeight
        val offsetPercentRelativeToItemView = bottomItemVisibleHeight * 1.0f / mItemViewHeight

        val layoutInfos = ArrayList<ItemViewInfo>()
        run {
            var i = bottomItemPosition - 1
            var j = 1
            while (i >= 0) {
                val maxOffset = (verticalSpace - mItemViewHeight) / 2 * Math.pow(0.8, j.toDouble())
                val start = (remainSpace - offsetPercentRelativeToItemView * maxOffset).toInt()
                val scaleXY = (Math.pow(mScale.toDouble(), (j - 1).toDouble()) * (1 - offsetPercentRelativeToItemView * (1 - mScale))).toFloat()
                val layoutPercent = start * 1.0f / verticalSpace
                val info = ItemViewInfo(start, scaleXY, offsetPercentRelativeToItemView, layoutPercent)
                layoutInfos.add(0, info)
                remainSpace = (remainSpace - maxOffset).toInt()
                if (remainSpace <= 0) {
                    info.top = (remainSpace + maxOffset).toInt()
                    info.positionOffset = 0F
                    info.layoutPercent = (info.top / verticalSpace).toFloat()
                    info.scaleXY = Math.pow(mScale.toDouble(), (j - 1).toDouble()).toFloat()
                    break
                }
                i--
                j++
            }
        }

        if (bottomItemPosition < mItemCount) {
            val start = verticalSpace - bottomItemVisibleHeight
            layoutInfos.add(ItemViewInfo(start, 1.0f, bottomItemVisibleHeight * 1.0f / mItemViewHeight, start * 1.0f / verticalSpace)
                    .setIsBottom())
        } else {
            bottomItemPosition = bottomItemPosition - 1//99
        }

        val layoutCount = layoutInfos.size
        val startPos = bottomItemPosition - (layoutCount - 1)
        val endPos = bottomItemPosition
        val childCount = childCount
        for (i in childCount - 1 downTo 0) {
            val childView = getChildAt(i)
            val pos = getPosition(childView!!)
            if (pos > endPos || pos < startPos) {
                removeAndRecycleView(childView, recycler!!)
            }
        }

        detachAndScrapAttachedViews(recycler!!)

        for (i in 0 until layoutCount) {
            val view = recycler.getViewForPosition(startPos + i)
            val layoutInfo = layoutInfos[i]
            addView(view)
            measureChildWithExactlySize(view)
            val left = (horizontalSpace - mItemViewWidth) / 2
            layoutDecoratedWithMargins(view, left, layoutInfo.top, left + mItemViewWidth, layoutInfo.top + mItemViewHeight)
            view.pivotX = (view.width / 2).toFloat()
            view.pivotY = 0f
            view.scaleX = layoutInfo.scaleXY
            view.scaleY = layoutInfo.scaleXY
        }
    }

    /**
     * 测量itemview的确切大小
     */
    private fun measureChildWithExactlySize(child: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(mItemViewWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(mItemViewHeight, View.MeasureSpec.EXACTLY)
        child.measure(widthSpec, heightSpec)
    }

    companion object {

        private val TAG = "EchelonLayoutManager"
    }

}

