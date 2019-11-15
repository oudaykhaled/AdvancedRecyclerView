package com.apps2u.happiestrecyclerview

/*
 * Copyright (C) 2014 skyfish.jy@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.apps2u.happiestrecyclerview.swipe.SwipeLayout
import com.apps2u.happiestrecyclerview.swipe.implments.SwipeItemRecyclerMangerImpl
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.apps2u.happiestrecyclerview.swipe.util.Attributes
import java.util.*


/**
 * Created by Ouday Khaled on 5/18/2018.
 */

abstract class RecyclerViewAdapter<VH : ViewHolder, T : Any> : androidx.recyclerview.widget.RecyclerView.Adapter<VH>, StickyHeaderHandler, SwipeAdapterInterface, SwipeItemMangerInterface {

    private var mSectionsIndexer: SparseArray<String>? = null
    private var mAdsIndexer: SparseArray<Int>? = null

    var mContext: Activity

    var data: ArrayList<T>? = ArrayList()
        set(objects) {

            if (objects != null) {
                if (objects.size < data!!.size)
                    if (recyclerView.endlessRecyclerOnScrollListener != null) {
                        recyclerView.endlessRecyclerOnScrollListener!!.current_page = 1
                        recyclerView.endlessRecyclerOnScrollListener!!.previousTotal = 0
                        recyclerView.endlessRecyclerOnScrollListener!!.moreDataAvailable = false
                    }
            }


            field = objects

            if (isSection)
                calculateSectionHeaders()
            else if (injection != null)
                calculateAdsSections()

            notifyDataSetChanged()

            recyclerView.setIsRefreshing(false)
        }
    private var multiselect_list = ArrayList<T>()
    internal var SelectedBackroundColorID: Int = 0
    internal var menuRecyclerListener: MenuRecyclerListener? = null
    internal var menuID: Int = 0


    internal var mItemManger: SwipeItemRecyclerMangerImpl


    internal var injection: Injection? = null

    val multiSelectedList: ArrayList<T>
        get() = multiselect_list

    var lastHeader: HeaderHolder? = null
    internal var recyclerView: RecyclerView<*>

    abstract val orientation: Int

    abstract val isSection: Boolean

    abstract val isStickyHeader: Boolean

    abstract val headerLayout: Int

    abstract val footerLayout: Int


    internal var onItemTouchListeners: ArrayList<RecyclerItemClickListener>

    internal var isMultiSelect = false
    internal var mActionMode: ActionMode? = null


    private val mActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            if (menuID != 0)
                inflater.inflate(menuID, menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            if (menuRecyclerListener != null)
                menuRecyclerListener!!.onMenuItemSelected(item.itemId)
            mode?.finish()
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mActionMode = null
            multiselect_list = ArrayList()
            notifyDataSetChanged()
            android.os.Handler().postDelayed({ isMultiSelect = false }, 200)
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    override val adapterData: SparseArray<String>?
        get() = if (mSectionsIndexer!!.size() == 0) null else mSectionsIndexer

    //    public AdsHolder createADSViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    //        if (position < 0)
    //            return null;
    //
    //        AdsHolder adsHolder = (AdsHolder) viewHolder;
    //        if(adsHolder.relativeLayout.getChildCount() == 0){
    //            PublisherAdView adView = new PublisherAdView(mContext);
    //            adView.setAdSizes(nativeSize);
    //            adView.setAdUnitId(adsID);
    //
    //            adView.setAppEventListener(new AppEventListener() {
    //                @Override
    //                public void onAppEvent(String s, String s1) {
    ////                    handleNativeAdClick(context, s, s1);
    //                }
    //            });
    //
    //            PublisherAdRequest.Builder adRequest = new PublisherAdRequest.Builder();
    //
    //            adView.loadAd(adRequest.build());
    //            adsHolder.relativeLayout.addView(adView);
    //        }
    //
    //        return adsHolder;
    //    }


    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */


    val lastHeaderData: String
        get() = mSectionsIndexer!!.get(mSectionsIndexer!!.keyAt(mSectionsIndexer!!.size() - 1))

    override val openItems: List<Int>
        get() = mItemManger.openItems

    override val openLayouts: List<SwipeLayout>
        get() = mItemManger.openLayouts

    override var mode: Attributes.Mode
        get() = mItemManger.mode
        set(mode) {
            mItemManger.mode = mode
        }


    fun addData(objects: ArrayList<T>?) {
        var objects = objects
        if (objects == null) {
            objects = ArrayList()
        }
        if (recyclerView.endlessRecyclerOnScrollListener != null)
            recyclerView.endlessRecyclerOnScrollListener!!.moreDataAvailable = false
        data!!.addAll(objects)
        data = data
    }

    fun inject(injection: Injection) {
        this.injection = injection
    }


    constructor(context: Activity, recyclerView: RecyclerView<*>) {
        mContext = context
        mSectionsIndexer = SparseArray()
        mAdsIndexer = SparseArray()
        onItemTouchListeners = ArrayList()
        this.recyclerView = recyclerView

        if (isStickyHeader) {
            val stickyLayoutManager = StickyLayoutManager(context, orientation, false, this)
            stickyLayoutManager.elevateHeaders(true)
            recyclerView.recyclerView!!.layoutManager = stickyLayoutManager
        } else {
            val linearLayoutmanager = LinearLayoutManager(context, orientation, false)
            recyclerView.recyclerView!!.layoutManager = linearLayoutmanager
        }
        if (recyclerView.isLoadMore) {
            recyclerView.addEndlessRecyclerOnScrollListener(context)
        }
        mItemManger = SwipeItemRecyclerMangerImpl(this)
    }

    constructor(context: Activity, recyclerView: RecyclerView<*>, gridLayoutManager: GridLayoutManager) {
        mContext = context
        mSectionsIndexer = SparseArray()
        mAdsIndexer = SparseArray()
        onItemTouchListeners = ArrayList()
        this.recyclerView = recyclerView

        if (isStickyHeader) {
            val stickyLayoutManager = StickyLayoutManager(context, orientation, false, this)
            stickyLayoutManager.elevateHeaders(true)
            recyclerView.recyclerView!!.layoutManager = stickyLayoutManager
        } else {
            recyclerView.recyclerView!!.layoutManager = gridLayoutManager
        }
        if (recyclerView.isLoadMore) {
            recyclerView.addEndlessRecyclerOnScrollListener(context)
        }
        mItemManger = SwipeItemRecyclerMangerImpl(this)
    }

    fun getHeaderHolder(v: View?): HeaderHolder? {
        return v?.let { HeaderHolder(it) }
    }

    private fun getFooterHolder(v: View?): FooterHolder? {
        return v?.let { FooterHolder(it) }
    }


    fun getAdsHolder(v: View): AdsHolder {
        return AdsHolder(v)
    }


    override fun getItemCount(): Int {
        return if (data != null) {
            if (data == null) 0 else data!!.size + mSectionsIndexer!!.size() + mAdsIndexer!!.size() + if (recyclerView.isLoadMore) 1 else 0 // +1 for footer
        } else 0
    }

    abstract fun attachAlwaysLastHeader(): Boolean

    abstract fun getSectionCondition(position: Int): String
    //    public abstract StickyLayoutManager getStickyLayoutManager();

    abstract fun getItemType(postion: Int): Int

    private fun calculateSectionHeaders() {
        mSectionsIndexer!!.clear()
        mAdsIndexer!!.clear()
        var i = 0
        var previous: String? = ""
        var temp: String?
        var count = 0
        var adCounts = 0
        val c = data ?: return


        if (injection != null)
            for (j in 0 until c.size + if ((c.size - injection!!.starting) % injection!!.concurrency == 0) 1 else injection!!.concurrency) {
                if (j == injection!!.starting + 1 || j > injection!!.starting + 1 && (j - injection!!.starting) % injection!!.concurrency == 0) {

                    mAdsIndexer!!.put(i + count + adCounts, injection!!.viewTypes[mAdsIndexer!!.size() % injection!!.viewTypes.size] * -1)
                    adCounts++
                }


                temp = getSectionCondition(j)
                if (temp == null || previous != temp) {
                    mSectionsIndexer!!.put(i + count + adCounts, temp)
                    previous = temp
                    count++
                }
                i++
            }
        else {
            for (j in c.indices) {
                temp = getSectionCondition(j)
                if (temp == null || previous != temp) {
                    mSectionsIndexer!!.put(i + count + adCounts, temp)
                    previous = temp
                    count++
                }
                i++
            }
        }
    }

    private fun calculateAdsSections() {
        mSectionsIndexer!!.clear()
        mAdsIndexer!!.clear()
        var i = 0
        var count = 0
        val c = data ?: return


        if (injection != null)
            for (j in 0 until c.size + if (c.size % injection!!.concurrency == 0) 1 else 0) {

                if (j > injection!!.starting && j % injection!!.concurrency == 0) {
                    mAdsIndexer!!.put(i + count, injection!!.viewTypes[mAdsIndexer!!.size() % injection!!.viewTypes.size] * -1)
                    count++
                }
                i++
            }
    }


    private fun countNumberAdsBefore(position: Int): Int {
        var count = 0

        for (i in 0 until mAdsIndexer!!.size()) {
            if (position > mAdsIndexer!!.keyAt(i))
                count++
            else
                break
        }
        return count
    }

    private fun countNumberSectionsBefore(position: Int): Int {
        var count = 0
        for (i in 0 until mSectionsIndexer!!.size()) {
            if (position > mSectionsIndexer!!.keyAt(i))
                count++
            else
                break
        }

        return count
    }

    private fun countNumberSectionsAndAdsBefore(position: Int): Int {
        var count = 0
        for (i in 0 until mSectionsIndexer!!.size()) {
            if (position > mSectionsIndexer!!.keyAt(i))
                count++
            else
                break
        }
        for (i in 0 until mAdsIndexer!!.size()) {
            if (position > mAdsIndexer!!.keyAt(i))
                count++
            else
                break
        }
        return count
    }

    fun removeAllTouchListeners() {
        for (i in onItemTouchListeners.indices) {
            recyclerView.recyclerView!!.removeOnItemTouchListener(onItemTouchListeners[i])
        }
        onItemTouchListeners.clear()
    }

    fun removeTouchListeners(recyclerItemClickListener: RecyclerItemClickListener) {
        recyclerView.recyclerView!!.removeOnItemTouchListener(recyclerItemClickListener)
        onItemTouchListeners.remove(recyclerItemClickListener)
    }

    fun setOnItemClickedListener(context: Context, SelectedBackroundColorID: Int, onItemClickListener: OnItemClickListener) {
        this.SelectedBackroundColorID = SelectedBackroundColorID

        val recyclerItemClickListener = RecyclerItemClickListener(context, recyclerView.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (!isMultiSelect) {
                    if (mSectionsIndexer!!.indexOfKey(position) >= 0)
                        return
                    if (mAdsIndexer!!.indexOfKey(position) >= 0)
                        return
                    onItemClickListener.onItemClick(view, position - countNumberSectionsAndAdsBefore(position))
                }

            }

            override fun onItemLongClick(view: View?, position: Int) {
                if (!isMultiSelect) {
                    if (mSectionsIndexer!!.indexOfKey(position) >= 0)
                        return
                    if (mAdsIndexer!!.indexOfKey(position) >= 0)
                        return
                    view?.let { onItemClickListener.onItemLongClick(it, position - countNumberSectionsAndAdsBefore(position)) }
                }

            }
        })

        onItemTouchListeners.add(recyclerItemClickListener)
        recyclerView.recyclerView!!.addOnItemTouchListener(recyclerItemClickListener)
    }

    fun enableMultiSelection(context: Context, SelectedBackroundColorID: Int, menuRecyclerListener: MenuRecyclerListener, menuID: Int) {
        this.menuID = menuID
        this.SelectedBackroundColorID = SelectedBackroundColorID
        this.menuRecyclerListener = menuRecyclerListener
        recyclerView.recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(context, recyclerView.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (mSectionsIndexer!!.indexOfKey(position) >= 0)
                    return
                if (mAdsIndexer!!.indexOfKey(position) >= 0)
                    return
                if (isMultiSelect)
                    multi_select(position - countNumberSectionsAndAdsBefore(position))
                else
                    Toast.makeText(context.applicationContext, "Details Page", Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClick(view: View?, position: Int) {
                if (mSectionsIndexer!!.indexOfKey(position) >= 0)
                    return
                if (mAdsIndexer!!.indexOfKey(position) >= 0)
                    return
                if (!isMultiSelect) {
                    multiselect_list = ArrayList<T>()
                    isMultiSelect = true

                    if (mActionMode == null) {
                        mActionMode = recyclerView.startActionMode(mActionModeCallback)
                    }
                }

                multi_select(position - countNumberSectionsAndAdsBefore(position))

            }
        }))
    }


    internal fun multi_select(position: Int) {
        if (recyclerView.recyclerView!!.adapter is CursorRecyclerViewAdapter<*>) {

        } else if (recyclerView.recyclerView!!.adapter is RecyclerViewAdapter<*, *>) {

            if (mActionMode != null) {
                if (multiselect_list.contains(this.data!![position]))
                    multiselect_list.remove(this.data!![position])
                else
                    multiselect_list.add(this.data!![position])

                if (multiselect_list.size > 0)
                    mActionMode!!.title = "" + multiselect_list.size
                else
                    mActionMode!!.title = ""

                notifyDataSetChanged()

            }
        }

    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolders(viewHolder: VH, position: Int)


    override fun onBindViewHolder(viewHolder: VH, position: Int) {

        if (!isSection) {
            if (getItemViewType(position) == TYPE_FOOTER) {
                if (recyclerView.endlessRecyclerOnScrollListener != null && recyclerView.endlessRecyclerOnScrollListener!!.loading && recyclerView.endlessRecyclerOnScrollListener!!.moreDataAvailable)
                    (viewHolder as FooterHolder).setVisibility(true)
                else
                    (viewHolder as FooterHolder).setVisibility(false)
            } else if (getItemViewType(position) < 0) {
                val pos = countNumberAdsBefore(position) % injection!!.viewTypes.size
                injection!!.onBindViewHolder(viewHolder, if (pos == 0) injection!!.viewTypes[injection!!.viewTypes.size - 1] else pos)
            } else {
                viewHolder.itemPosition = position - countNumberSectionsAndAdsBefore(position)
                onBindViewHolders(viewHolder, position - countNumberSectionsAndAdsBefore(position))
                if (multiSelectedList.contains(this.data!![position - countNumberSectionsAndAdsBefore(position)])) {
                    viewHolder.itemView.setBackgroundColor(SelectedBackroundColorID)
                } else {
                    viewHolder.itemView.setBackgroundColor(mContext.resources.getColor(android.R.color.transparent))
                }
            }

        } else {
            if (getItemViewType(position) == TYPE_FOOTER) {
                if (recyclerView.endlessRecyclerOnScrollListener != null && recyclerView.endlessRecyclerOnScrollListener!!.loading && recyclerView.endlessRecyclerOnScrollListener!!.moreDataAvailable)
                    (viewHolder as FooterHolder).setVisibility(true)
                else
                    (viewHolder as FooterHolder).setVisibility(false)
            } else if (getItemViewType(position) == TYPE_HEADER) {
                createHeaderViewHolder(viewHolder, position - countNumberSectionsAndAdsBefore(position))
            } else if (getItemViewType(position) < 0) {
                val pos = countNumberAdsBefore(position) % injection!!.viewTypes.size
                injection!!.onBindViewHolder(viewHolder, if (pos == 0) injection!!.viewTypes[injection!!.viewTypes.size - 1] else pos)
            } else {
                viewHolder.itemPosition = position - countNumberSectionsAndAdsBefore(position)
                onBindViewHolders(viewHolder, position - countNumberSectionsAndAdsBefore(position))
                if (multiSelectedList.contains(this.data!![position - countNumberSectionsAndAdsBefore(position)])) {
                    viewHolder.itemView.setBackgroundColor(SelectedBackroundColorID)
                } else {
                    viewHolder.itemView.setBackgroundColor(mContext.resources.getColor(android.R.color.transparent))
                }
            }
        }

    }

    abstract fun onCreateViewHolders(parent: ViewGroup, viewType: Int): ViewHolder

    @Deprecated("")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutInflater = LayoutInflater.from(parent.context)
        var v: View? = null

        return if (viewType < 0) {

            injection!!.onCreateViewHolder(parent, viewType * -1) as VH

        } else {
            when (viewType) {
                TYPE_HEADER -> {
                    v = layoutInflater.inflate(headerLayout, parent, false)
                    getHeaderHolder(v) as VH
                }

                TYPE_FOOTER -> {
                    v = layoutInflater.inflate(footerLayout, parent, false)
                    getFooterHolder(v) as VH
                }

                else -> onCreateViewHolders(parent, viewType) as VH
            }
        }

    }

    private fun createHeaderViewHolder(viewHolder: ViewHolder, position: Int): HeaderHolder? {
        if (position < 0)
            return null
        val date = getSectionCondition(position)

        val headerHolder = viewHolder as HeaderHolder
        headerHolder.setHeader(date)

        if (attachAlwaysLastHeader()) {
            if (date == lastHeaderData) {
                if ((recyclerView.recyclerView!!.layoutManager as StickyLayoutManager).findFirstVisibleItemPosition() > headerHolder.adapterPosition) {
                    headerHolder.setVisibility(false)
                } else {
                    headerHolder.setVisibility(true)
                }
                lastHeader = headerHolder

            } else {
                headerHolder.setVisibility(true)
                if (lastHeader != null)
                    lastHeader!!.setVisibility(true)
            }
        }

        return headerHolder
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && recyclerView.isLoadMore) {
            TYPE_FOOTER
        } else if (!isSection && injection == null) {
            getItemType(position)
        } else if (mSectionsIndexer!!.indexOfKey(position) >= 0) {
            TYPE_HEADER
        } else if (mAdsIndexer!!.indexOfKey(position) >= 0) {
            mAdsIndexer!!.get(position)
        } else {
            getItemType(position - countNumberSectionsAndAdsBefore(position))
        }

    }


    fun getAdapterPosition(viewHolder: VH): Int {
        val position = viewHolder.adapterPosition
        return position - countNumberSectionsAndAdsBefore(position)
    }


    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun closeItem(position: Int) {
        mItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        mItemManger.closeAllExcept(layout)
    }

    override fun closeAllItems() {
        mItemManger.closeAllItems()
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mItemManger.isOpen(position)
    }


    fun onSwipeClicked(swipeLayout: SwipeLayout) {
        mItemManger.removeShownLayouts(swipeLayout)
        mItemManger.closeAllItems()
    }

    fun getCustomDividerItemDecoration(divider: Drawable, includeAds: Boolean, includeSections: Boolean): CustomDividerItemDecoration {
        return CustomDividerItemDecoration(divider, includeAds, includeSections)
    }

    inner class CustomDividerItemDecoration(private val mDivider: Drawable, private val includeAds: Boolean, private val includeSections: Boolean) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

        override fun onDraw(canvas: Canvas, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0..childCount - if (recyclerView.isLoadMore) 2 else 1) {
                val child = parent.getChildAt(i)
                if (!includeSections)
                    if (mSectionsIndexer!!.indexOfKey(i) > 0) {
                        continue
                    }

                if (!includeAds)
                    if (mAdsIndexer!!.indexOfKey(i) > 0) {
                        continue
                    }
                val params = child.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams

                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + mDivider.intrinsicHeight

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider.draw(canvas)
            }
        }
    }

    companion object {

        val TYPE_HEADER = 0
        val TYPE_FOOTER = Integer.MAX_VALUE

        val HORIZONTAL = OrientationHelper.HORIZONTAL
        val VERTICAL = OrientationHelper.VERTICAL
    }
}