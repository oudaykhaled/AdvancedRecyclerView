package com.apps2u.happiestrecyclerview

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created by Ouday Khaled on 5/22/2018.
 */

class RecyclerView<T : androidx.recyclerview.widget.RecyclerView.Adapter<*>>(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
        internal set
    internal var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    internal var swipeListener: SwipeListener? = null
    internal var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener? = null
    protected var isRefreshEnabled = false
    var isLoadMore = false

    val layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager?
        get() = recyclerView.getLayoutManager()

    var adapter: T
        get() = recyclerView.adapter as T
        set(adapter) = recyclerView.setAdapter(adapter)


    fun scrollToPosition(position: Int) {
        recyclerView.scrollToPosition(position)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        recyclerView.setNestedScrollingEnabled(enabled)
    }

    fun addOnScrollListener(listener: androidx.recyclerview.widget.RecyclerView.OnScrollListener) {
        recyclerView.addOnScrollListener(listener)
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MyRecyclerView, 0, 0)

        try {
            isRefreshEnabled = ta.getBoolean(R.styleable.MyRecyclerView_isRefresh, false)
            isLoadMore = ta.getBoolean(R.styleable.MyRecyclerView_isLoadMore, false)
        } finally {
            ta.recycle()
        }

        if (isRefreshEnabled) {
            initSwipeRefreshLayout(context)
        } else {
            init(context)
        }


    }

    fun addEndlessRecyclerOnScrollListener(context: Context) {

        if (endlessRecyclerOnScrollListener != null) {
            if (recyclerView != null)
                recyclerView!!.removeOnScrollListener(endlessRecyclerOnScrollListener!!)
            endlessRecyclerOnScrollListener = null
        }


        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener(recyclerView.layoutManager) {
            override fun onLoadMore(current_page: Int) {
                if (swipeListener != null) {
                    val cd = ConnectionDetector(context)
                    if (cd.isConnectingToInternet) {
                        //                        mSwipeRefreshLayout.setRefreshing(true);
                        swipeListener!!.loadMore(current_page)
                    } else {
                        //                        mSwipeRefreshLayout.setRefreshing(false);
                        swipeListener!!.onSwipeConnectionError()

                    }
                }
            }
        }

        if (endlessRecyclerOnScrollListener != null && recyclerView != null)
            recyclerView!!.addOnScrollListener(endlessRecyclerOnScrollListener!!)

    }

    fun setSwipeListener(swipeListener: SwipeListener) {
        this.swipeListener = swipeListener
    }

    internal fun initSwipeRefreshLayout(context: Context) {
        recyclerView = androidx.recyclerview.widget.RecyclerView(context)

        mSwipeRefreshLayout = SwipeRefreshLayout(recyclerView.getContext())
        addView(mSwipeRefreshLayout)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            recyclerView.setLayoutParams(LayoutParams(LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)))
        }


        val frameLayout = FrameLayout(context)
        frameLayout.addView(recyclerView)
        mSwipeRefreshLayout!!.addView(frameLayout)

        mSwipeRefreshLayout!!.setOnRefreshListener {
            val cd = ConnectionDetector(context)
            if (cd.isConnectingToInternet) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipe()
                }
            } else {
                mSwipeRefreshLayout!!.isRefreshing = false
                if (swipeListener != null)
                    swipeListener!!.onSwipeConnectionError()
            }
        }

    }

    fun setIsRefreshing(isRefreshing: Boolean) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.isRefreshing = isRefreshing
        }

    }


    fun onConnectionError() {
        if (endlessRecyclerOnScrollListener != null) {
            endlessRecyclerOnScrollListener!!.onLoadMore(endlessRecyclerOnScrollListener!!.current_page)

        }
    }


    private inner class ConnectionDetector(private val _context: Context) {

        /**
         * Checking for all possible internet providers
         */
        val isConnectingToInternet: Boolean
            get() {
                val connectivity = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connectivity != null) {
                    val info = connectivity.allNetworkInfo
                    if (info != null)
                        for (i in info.indices)
                            if (info[i].state == NetworkInfo.State.CONNECTED) {
                                return true
                            }

                }
                return false
            }
    }


    private fun init(context: Context) {
        recyclerView = androidx.recyclerview.widget.RecyclerView(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            recyclerView.setLayoutParams(LayoutParams(LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)))
        }
        addView(recyclerView)
    }


    fun addDivider(divider: Drawable, includeAds: Boolean, includeSections: Boolean) {
        recyclerView.addItemDecoration((adapter as RecyclerViewAdapter<*, *>).getCustomDividerItemDecoration(divider, includeAds, includeSections))
    }
}
