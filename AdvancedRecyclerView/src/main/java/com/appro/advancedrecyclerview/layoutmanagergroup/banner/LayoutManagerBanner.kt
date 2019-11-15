package com.appro.advancedrecyclerview.layoutmanagergroup.banner

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import java.lang.ref.WeakReference

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

class LayoutManagerBanner : LinearLayoutManager {
    private var mLinearSnapHelper: LinearSnapHelper? = null
    var recyclerView: RecyclerView? = null
        private set
    private var mOnSelectedViewListener: OnSelectedViewListener? = null
    private var mRealCount: Int = 0
    private var mCurrentPosition = 0
    private var mHandler: TaskHandler? = null
    private var mTimeDelayed: Long = 1000
    private var mOrientation: Int = 0
    private var mTimeSmooth = 150f

    constructor(context: Context, recyclerView: RecyclerView, realCount: Int) : super(context) {
        this.mLinearSnapHelper = LinearSnapHelper()
        this.mRealCount = realCount
        this.mHandler = TaskHandler(this)
        this.recyclerView = recyclerView
        orientation = LinearLayoutManager.HORIZONTAL
        this.mOrientation = LinearLayoutManager.HORIZONTAL
    }

    constructor(context: Context, recyclerView: RecyclerView, realCount: Int, orientation: Int) : super(context) {
        this.mLinearSnapHelper = LinearSnapHelper()
        this.mRealCount = realCount
        this.mHandler = TaskHandler(this)
        this.recyclerView = recyclerView
        setOrientation(orientation)
        this.mOrientation = orientation
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        mLinearSnapHelper!!.attachToRecyclerView(view)
    }


    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return mTimeSmooth / displayMetrics.densityDpi
            }
        }

        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mLinearSnapHelper != null) {

                val view = mLinearSnapHelper!!.findSnapView(this)
                mCurrentPosition = getPosition(view!!)

                if (mOnSelectedViewListener != null) mOnSelectedViewListener!!.onSelectedView(view, mCurrentPosition % mRealCount)


                mHandler!!.setSendMsg(true)
                val msg = Message.obtain()
                mCurrentPosition++
                msg.what = mCurrentPosition
                mHandler!!.sendMessageDelayed(msg, mTimeDelayed)

            }
        } else if (state == SCROLL_STATE_DRAGGING) {
            mHandler!!.setSendMsg(false)
        }
    }

    fun setTimeDelayed(timeDelayed: Long) {
        this.mTimeDelayed = timeDelayed
    }

    fun setTimeSmooth(timeSmooth: Float) {
        this.mTimeSmooth = timeSmooth
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        mHandler!!.setSendMsg(true)
        val msg = Message.obtain()
        msg.what = mCurrentPosition + 1
        mHandler!!.sendMessageDelayed(msg, mTimeDelayed)
    }

    fun setOnSelectedViewListener(listener: OnSelectedViewListener) {
        this.mOnSelectedViewListener = listener
    }


    /**
     * 停止时，显示在中间的View的监听
     */
    interface OnSelectedViewListener {
        fun onSelectedView(view: View, position: Int)
    }

    private class TaskHandler(bannerLayoutManager: LayoutManagerBanner) : Handler() {
        private val mWeakBanner: WeakReference<LayoutManagerBanner>
        private var mSendMsg: Boolean = false

        init {
            this.mWeakBanner = WeakReference(bannerLayoutManager)
        }

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            if (msg != null && mSendMsg) {
                val position = msg.what
                val bannerLayoutManager = mWeakBanner.get()
                if (bannerLayoutManager != null) {
                    bannerLayoutManager.recyclerView!!.smoothScrollToPosition(position)
                }
            }
        }

        fun setSendMsg(sendMsg: Boolean) {
            this.mSendMsg = sendMsg
        }

    }

    companion object {
        private val TAG = "BannerLayoutManager"
    }
}
