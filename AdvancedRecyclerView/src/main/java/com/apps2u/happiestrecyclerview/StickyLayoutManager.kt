package com.apps2u.happiestrecyclerview

import android.content.Context
import androidx.recyclerview.widget.*
import android.view.View

import java.util.ArrayList
import java.util.LinkedHashMap

internal class StickyLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean, headerHandler: StickyHeaderHandler) : LinearLayoutManager(context, orientation, reverseLayout) {

    private var positioner: StickyHeaderPositioner? = null
    private var headerHandler: StickyHeaderHandler? = null
    private val headerPositions = ArrayList<Int>()
    private var viewRetriever: ViewRetriever.RecyclerViewRetriever? = null
    private var headerElevation = StickyHeaderPositioner.NO_ELEVATION
    private var listener: StickyHeaderListener? = null

    private val visibleHeaders: Map<Int, View>
        get() {
            val visibleHeaders = LinkedHashMap<Int, View>()

            for (i in 0 until childCount) {
                val view = getChildAt(i)
                val dataPosition = getPosition(view!!)
                if (headerPositions.contains(dataPosition)) {
                    visibleHeaders[dataPosition] = view
                }
            }
            return visibleHeaders
        }

    init {
        init(headerHandler)
    }

    private fun init(stickyHeaderHandler: StickyHeaderHandler) {
        Preconditions.checkNotNull(stickyHeaderHandler, "StickyHeaderHandler == null")
        this.headerHandler = stickyHeaderHandler
    }

    /**
     * Register a callback to be invoked when a header is attached/re-bound or detached.
     *
     * @param listener The callback that will be invoked, or null to unset.
     */
    fun setStickyHeaderListener(listener: StickyHeaderListener?) {
        this.listener = listener
        if (positioner != null) {
            positioner!!.setListener(listener)
        }
    }

    /**
     * Enable or disable elevation for Sticky Headers.
     *
     *
     * If you want to specify a specific amount of elevation, use
     * [StickyLayoutManager.elevateHeaders]
     *
     * @param elevateHeaders Enable Sticky Header elevation. Default is false.
     */
    fun elevateHeaders(elevateHeaders: Boolean) {
        this.headerElevation = if (elevateHeaders)
            StickyHeaderPositioner.DEFAULT_ELEVATION
        else
            StickyHeaderPositioner.NO_ELEVATION
        elevateHeaders(headerElevation)
    }

    /**
     * Enable Sticky Header elevation with a specific amount.
     *
     * @param dp elevation in dp
     */
    fun elevateHeaders(dp: Int) {
        this.headerElevation = dp
        if (positioner != null) {
            positioner!!.setElevateHeaders(dp)
        }
    }

    override fun onLayoutChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler?, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        cacheHeaderPositions()
        if (positioner != null) {
            runPositionerInit()
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: androidx.recyclerview.widget.RecyclerView.Recycler?, state: androidx.recyclerview.widget.RecyclerView.State?): Int {
        val scroll = super.scrollVerticallyBy(dy, recycler, state)
        if (Math.abs(scroll) > 0) {
            if (positioner != null) {
                viewRetriever?.let {
                    positioner!!.updateHeaderState(
                            findFirstVisibleItemPosition(), visibleHeaders, it, findFirstCompletelyVisibleItemPosition() == 0)
                }
            }
        }
        return scroll
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: androidx.recyclerview.widget.RecyclerView.Recycler?, state: androidx.recyclerview.widget.RecyclerView.State?): Int {
        val scroll = super.scrollHorizontallyBy(dx, recycler, state)
        if (Math.abs(scroll) > 0) {
            if (positioner != null) {
                viewRetriever?.let {
                    positioner!!.updateHeaderState(
                            findFirstVisibleItemPosition(), visibleHeaders, it, findFirstCompletelyVisibleItemPosition() == 0)
                }
            }
        }
        return scroll
    }

    override fun removeAndRecycleAllViews(recycler: androidx.recyclerview.widget.RecyclerView.Recycler) {
        super.removeAndRecycleAllViews(recycler)
        if (positioner != null) {
            positioner!!.clearHeader()
        }
    }

    override fun onAttachedToWindow(view: androidx.recyclerview.widget.RecyclerView?) {
        Preconditions.validateParentView(view!!)
        viewRetriever = ViewRetriever.RecyclerViewRetriever(view)
        positioner = StickyHeaderPositioner(view)
        positioner!!.setElevateHeaders(headerElevation)
        positioner!!.setListener(listener)
        if (headerPositions.size > 0) {
            // Layout has already happened and header positions are cached. Catch positioner up.
            positioner!!.setHeaderPositions(headerPositions)
            runPositionerInit()
        }
        super.onAttachedToWindow(view)
    }

    private fun runPositionerInit() {
        positioner!!.reset(orientation)
        viewRetriever?.let { positioner!!.updateHeaderState(findFirstVisibleItemPosition(), visibleHeaders, it, findFirstCompletelyVisibleItemPosition() == 0) }
    }


    private fun cacheHeaderPositions(): Int {
        headerPositions.clear()
        val count = headerHandler!!.adapterData
        if (count == null) {
            if (positioner != null) {
                positioner!!.setHeaderPositions(headerPositions)
            }
            return 0
        }

        for (i in 0 until count.size()) {
            headerPositions.add(count.keyAt(i))
        }
        if (positioner != null) {
            positioner!!.setHeaderPositions(headerPositions)
        }


        return count.size()
    }
}
