package com.apps2u.happiestrecyclerview.layoutmanagergroup.skidright

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.apps2u.happiestrecyclerview.layoutmanagergroup.echelon.ItemViewInfo
import java.util.*

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */
class LayoutManagerSkidRight(private val mItemHeightWidthRatio: Float, private val mScale: Float) : RecyclerView.LayoutManager() {
    private var mHasChild = false
    private var mItemViewWidth: Int = 0
    private var mItemViewHeight: Int = 0
    private var mScrollOffset = Integer.MAX_VALUE
    private var mItemCount: Int = 0
    private val mSkidRightSnapHelper: SkidRightSnapHelper

    val verticalSpace: Int
        get() = height - paddingTop - paddingBottom

    val horizontalSpace: Int
        get() = width - paddingLeft - paddingRight

    init {
        mSkidRightSnapHelper = SkidRightSnapHelper()
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        mSkidRightSnapHelper.attachToRecyclerView(view)
    }

    fun getFixedScrollPosition(direction: Int, fixValue: Float): Int {
        if (mHasChild) {
            if (mScrollOffset % mItemViewWidth == 0) {
                return RecyclerView.NO_POSITION
            }
            val position = mScrollOffset * 1.0f / mItemViewWidth
            return convert2AdapterPosition((if (direction > 0) position + fixValue else position + (1 - fixValue)).toInt() - 1)
        }
        return RecyclerView.NO_POSITION
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        if (state.itemCount == 0 || state.isPreLayout) return
        removeAndRecycleAllViews(recycler!!)
        if (!mHasChild) {
            mItemViewHeight = verticalSpace
            mItemViewWidth = (mItemViewHeight / mItemHeightWidthRatio).toInt()
            mHasChild = true
        }
        mItemCount = itemCount
        mScrollOffset = makeScrollOffsetWithinRange(mScrollOffset)
        fill(recycler)
    }

    fun fill(recycler: RecyclerView.Recycler?) {
        var bottomItemPosition = Math.floor((mScrollOffset / mItemViewWidth).toDouble()).toInt()
        val bottomItemVisibleSize = mScrollOffset % mItemViewWidth
        val offsetPercent = bottomItemVisibleSize * 1.0f / mItemViewWidth
        val space = horizontalSpace

        val layoutInfos = ArrayList<ItemViewInfo>()
        run {
            var i = bottomItemPosition - 1
            var j = 1
            var remainSpace = space - mItemViewWidth
            while (i >= 0) {
                val maxOffset = (horizontalSpace - mItemViewWidth) / 2 * Math.pow(mScale.toDouble(), j.toDouble())
                val start = (remainSpace - offsetPercent * maxOffset).toInt()
                val info = ItemViewInfo(start,
                        (Math.pow(mScale.toDouble(), (j - 1).toDouble()) * (1 - offsetPercent * (1 - mScale))).toFloat(),
                        offsetPercent,
                        start * 1.0f / space
                )
                layoutInfos.add(0, info)

                remainSpace -= maxOffset.toInt()
                if (remainSpace <= 0) {
                    info.top = (remainSpace + maxOffset).toInt()
                    info.positionOffset = 0F
                    info.layoutPercent = (info.top / space).toFloat()
                    info.scaleXY = Math.pow(mScale.toDouble(), (j - 1).toDouble()).toFloat()
                    break
                }
                i--
                j++
            }
        }

        if (bottomItemPosition < mItemCount) {
            val start = space - bottomItemVisibleSize
            layoutInfos.add(ItemViewInfo(start, 1.0f,
                    bottomItemVisibleSize * 1.0f / mItemViewWidth, start * 1.0f / space).setIsBottom())
        } else {
            bottomItemPosition -= 1
        }

        val layoutCount = layoutInfos.size

        val startPos = bottomItemPosition - (layoutCount - 1)
        val endPos = bottomItemPosition
        val childCount = childCount
        for (i in childCount - 1 downTo 0) {
            val childView = getChildAt(i)
            val pos = convert2LayoutPosition(getPosition(childView!!))
            if (pos > endPos || pos < startPos) {
                removeAndRecycleView(childView, recycler!!)
            }
        }
        detachAndScrapAttachedViews(recycler!!)

        for (i in 0 until layoutCount) {
            fillChild(recycler.getViewForPosition(convert2AdapterPosition(startPos + i)), layoutInfos[i])
        }
    }

    private fun fillChild(view: View, layoutInfo: ItemViewInfo) {
        addView(view)
        measureChildWithExactlySize(view)
        val scaleFix = (mItemViewWidth * (1 - layoutInfo.scaleXY) / 2).toInt()

        val top = paddingTop
        layoutDecoratedWithMargins(view, layoutInfo.top - scaleFix, top, layoutInfo.top + mItemViewWidth - scaleFix, top + mItemViewHeight)
        ViewCompat.setScaleX(view, layoutInfo.scaleXY)
        ViewCompat.setScaleY(view, layoutInfo.scaleXY)
    }

    private fun measureChildWithExactlySize(child: View) {
        val lp = child.layoutParams as RecyclerView.LayoutParams
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
                mItemViewWidth - lp.leftMargin - lp.rightMargin, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
                mItemViewHeight - lp.topMargin - lp.bottomMargin, View.MeasureSpec.EXACTLY)
        child.measure(widthSpec, heightSpec)
    }

    private fun makeScrollOffsetWithinRange(scrollOffset: Int): Int {
        return Math.min(Math.max(mItemViewWidth, scrollOffset), mItemCount * mItemViewWidth)
    }


    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val pendingScrollOffset = mScrollOffset + dx
        mScrollOffset = makeScrollOffsetWithinRange(pendingScrollOffset)
        fill(recycler)
        return mScrollOffset - pendingScrollOffset + dx
    }


    fun calculateDistanceToPosition(targetPos: Int): Int {
        val pendingScrollOffset = mItemViewWidth * (convert2LayoutPosition(targetPos) + 1)
        return pendingScrollOffset - mScrollOffset
    }


    override fun scrollToPosition(position: Int) {
        if (position > 0 && position < mItemCount) {
            mScrollOffset = mItemViewWidth * (convert2LayoutPosition(position) + 1)
            requestLayout()
        }
    }


    override fun canScrollHorizontally(): Boolean {
        return true
    }

    fun convert2AdapterPosition(layoutPosition: Int): Int {
        return mItemCount - 1 - layoutPosition
    }

    fun convert2LayoutPosition(adapterPostion: Int): Int {
        return mItemCount - 1 - adapterPostion
    }


}
