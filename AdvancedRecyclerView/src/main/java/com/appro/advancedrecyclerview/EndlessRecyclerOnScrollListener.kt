package com.appro.advancedrecyclerview

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by koa on 9/30/2015.
 */
abstract class EndlessRecyclerOnScrollListener(layoutManager: RecyclerView.LayoutManager?) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    var previousTotal = 0 // The total number of items in the dataset after the last load
    var loading = true // True if we are still waiting for the last set of data to load.
    var moreDataAvailable = false
    private var visibleThreshold = 3 // The minimum items, below current scroll position, before loading more.
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0

    var current_page = 1

    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var mGridLayoutManager: GridLayoutManager? = null
    private var layoutType: Int = 0

    init {
        if (layoutManager is LinearLayoutManager) {
            this.mLinearLayoutManager = layoutManager
            layoutType = LINEAR_TYPE
            visibleThreshold = 2
        }
        if (layoutManager is GridLayoutManager) {
            this.mGridLayoutManager = layoutManager
            layoutType = GRID_TYPE
            visibleThreshold = mGridLayoutManager!!.spanCount
        }

    }

    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
            when (layoutType) {
                GRID_TYPE -> {
                    visibleItemCount = mGridLayoutManager!!.childCount
                    totalItemCount = mGridLayoutManager!!.itemCount - 1
                    firstVisibleItem = mGridLayoutManager!!.findFirstVisibleItemPosition()

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false
                            previousTotal = totalItemCount
                        }
                    }

                    if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold * mGridLayoutManager!!.spanCount) {
                        current_page++
                        moreDataAvailable = true
                        onLoadMore(current_page)
                        loading = true
                    }
                }
                LINEAR_TYPE -> {

                    visibleItemCount = recyclerView.childCount
                    totalItemCount = mLinearLayoutManager!!.itemCount - 1
                    firstVisibleItem = mLinearLayoutManager!!.findFirstVisibleItemPosition()

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false
                            previousTotal = totalItemCount
                        }
                    }

                    if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                        // End has been reached

                        // Do something
                        current_page++
                        moreDataAvailable = true
                        onLoadMore(current_page)
                        loading = true
                    }
                }
            }
        }


    }


    abstract fun onLoadMore(current_page: Int)

    companion object {
        var TAG = EndlessRecyclerOnScrollListener::class.java!!.getSimpleName()
        private val GRID_TYPE = 0
        private val LINEAR_TYPE = 1
    }
}