package com.appro.advancedrecyclerview

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
import android.database.Cursor
import android.database.DataSetObserver
import android.util.SparseArray
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.appro.advancedrecyclerview.swipe.SwipeLayout
import com.appro.advancedrecyclerview.swipe.implments.SwipeItemRecyclerMangerImpl
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.appro.advancedrecyclerview.swipe.util.Attributes
import java.util.*


/**
 * Created by Ouday Khaled on 5/18/2018.
 */
abstract class CursorRecyclerViewAdapter<VH : ViewHolder>(var mContext: Activity, cursor: Cursor?, internal var recyclerView: RecyclerView<*>) : androidx.recyclerview.widget.RecyclerView.Adapter<VH>(), StickyHeaderHandler, SwipeAdapterInterface, SwipeItemMangerInterface {

    private val mSectionsIndexer: SparseArray<String>

    var cursor: Cursor? = null
        private set

    private var mDataValid: Boolean = false

    private var mRowIdColumn: Int = 0

    private val mDataSetObserver: DataSetObserver?

    internal var SelectedBackroundColorID: Int = 0
    internal var menuID: Int = 0
    internal var menuRecyclerListener: MenuRecyclerListener? = null
    internal var isMultiSelect = false
    internal var mActionMode: ActionMode? = null
    private var multiselect_list = ArrayList<Any>()

    internal var lastHeader: HeaderHolder? = null
    internal var mItemManger: SwipeItemRecyclerMangerImpl

    private val mActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            menu.clear()
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

    abstract val orientation: Int

    abstract val isSection: Boolean

    abstract val isStickyHeader: Boolean

    abstract val headerLayout: Int


    override val adapterData: SparseArray<String>?
        get() = if (mSectionsIndexer.size() == 0) null else mSectionsIndexer

    val lastHeaderData: String
        get() = mSectionsIndexer.get(mSectionsIndexer.keyAt(mSectionsIndexer.size() - 1))

    override val openItems: List<Int>
        get() = mItemManger.openItems

    override val openLayouts: List<SwipeLayout>
        get() = mItemManger.openLayouts

    override var mode: Attributes.Mode
        get() = mItemManger.mode
        set(mode) {
            mItemManger.mode = mode
        }

    init {
        this.cursor = cursor
        mDataValid = cursor != null
        mRowIdColumn = if (mDataValid) this.cursor!!.getColumnIndex("_id") else -1
        mDataSetObserver = NotifyingDataSetObserver()
        if (this.cursor != null) {
            this.cursor!!.registerDataSetObserver(mDataSetObserver)
        }
        mSectionsIndexer = SparseArray()

        if (isStickyHeader) {
            val stickyLayoutManager = StickyLayoutManager(mContext, orientation, false, this)
            stickyLayoutManager.elevateHeaders(true)
            recyclerView.recyclerView!!.layoutManager = stickyLayoutManager
        } else {
            val linearLayoutmanager = LinearLayoutManager(mContext, orientation, false)
            recyclerView.recyclerView!!.layoutManager = linearLayoutmanager
        }

        if (recyclerView.isLoadMore) {
            recyclerView.addEndlessRecyclerOnScrollListener(mContext)
        }

        mItemManger = SwipeItemRecyclerMangerImpl(this)
    }


    fun setOnItemClickedListener(context: Context, SelectedBackroundColorID: Int, onItemClickListener: OnItemClickListener) {
        this.SelectedBackroundColorID = SelectedBackroundColorID
        recyclerView.recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(context, recyclerView.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (!isMultiSelect)
                    onItemClickListener.onItemClick(view, position)
            }

            override fun onItemLongClick(view: View?, position: Int) {
                if (!isMultiSelect)
                    view?.let { onItemClickListener.onItemLongClick(it, position) }
            }
        }))
    }

    fun enableMultiSelection(context: Context, SelectedBackroundColorID: Int, menuRecyclerListener: MenuRecyclerListener, menuID: Int) {
        this.menuID = menuID
        this.SelectedBackroundColorID = SelectedBackroundColorID
        this.menuRecyclerListener = menuRecyclerListener
        recyclerView.recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(context, recyclerView.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (mSectionsIndexer.indexOfKey(position) >= 0)
                    return
                if (isMultiSelect)
                    multi_select((position - countNumberSectionsBefore(position)).toString() + "")
                //                else
                //                    Toast.makeText(context.getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            override fun onItemLongClick(view: View?, position: Int) {
                if (mSectionsIndexer.indexOfKey(position) >= 0)
                    return
                if (!isMultiSelect) {
                    multiselect_list = ArrayList<Any>()
                    isMultiSelect = true

                    if (mActionMode == null) {
                        mActionMode = recyclerView.startActionMode(mActionModeCallback)

                    }
                }

                multi_select((position - countNumberSectionsBefore(position)).toString() + "")

            }
        }))
    }

    internal fun multi_select(position: String) {
        if (mActionMode != null) {
            if (multiselect_list.contains(position))
                multiselect_list.remove(position)
            else
                multiselect_list.add(position)

            if (multiselect_list.size > 0) {
                mActionMode!!.title = "" + multiselect_list.size
            } else {
                mActionMode!!.title = ""
                mActionMode!!.finish()
                mActionMode = null
                multiselect_list = ArrayList()
                notifyDataSetChanged()
            }

            notifyDataSetChanged()
        }
    }

    fun <T> getMultiSelectedList(): ArrayList<Any> {
        return multiselect_list
    }

    private fun getHeaderHolder(v: View): HeaderHolder {
        return HeaderHolder(v)
    }

    override fun getItemCount(): Int {
        return if (mDataValid && cursor != null) {
            if (cursor == null) 0 else cursor!!.count + mSectionsIndexer.size()
        } else 0
    }

    abstract fun attachAlwaysLastHeader(): Boolean

    abstract fun getSectionCondition(c: Cursor?): String

    abstract fun getItemType(postion: Int): Int

    private fun calculateSectionHeaders() {
        mSectionsIndexer.clear()
        var i = 0
        var previous: String? = ""
        var temp: String?
        var count = 0
        val c = cursor
        if (c == null || c.isClosed) {
            return
        }

        c.moveToFirst()
        do {
            temp = getSectionCondition(c)
            if (temp == null || previous != temp) {
                mSectionsIndexer.put(i + count, temp)
                previous = temp
                count++
            }
            i++
        } while (c.moveToNext())
    }

    private fun countNumberSectionsBefore(position: Int): Int {
        var count = 0
        for (i in 0 until mSectionsIndexer.size()) {
            if (position > mSectionsIndexer.keyAt(i))
                count++
        }

        return count
    }

    override fun getItemId(position: Int): Long {
        return if (mDataValid && cursor != null && cursor!!.moveToPosition(position)) {
            cursor!!.getLong(mRowIdColumn)
        } else 0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor?, position: Int)


    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        kotlin.check(mDataValid) { "this should only be called when the cursor is valid" }

        if (!isSection) {
            viewHolder.itemPosition = position
            onBindViewHolder(viewHolder, cursor, position)
            if (getMultiSelectedList<Any>().contains(position.toString() + "")) {
                viewHolder.itemView.setBackgroundColor(SelectedBackroundColorID)
            } else {
                viewHolder.itemView.setBackgroundColor(mContext.resources.getColor(android.R.color.transparent))
            }
        } else {
            if (getItemViewType(position) == TYPE_HEADER) {
                createHeaderViewHolder(viewHolder, position - countNumberSectionsBefore(position))
            } else {
                viewHolder.itemPosition = position - countNumberSectionsBefore(position)
                onBindViewHolder(viewHolder, cursor, position - countNumberSectionsBefore(position))
                if (getMultiSelectedList<Any>().contains((position - countNumberSectionsBefore(position)).toString() + "")) {
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

        when (viewType) {
            TYPE_HEADER -> {
                v = layoutInflater.inflate(headerLayout, parent, false)
                return getHeaderHolder(v) as VH
            }
            else -> return onCreateViewHolders(parent, viewType) as VH
        }
    }


    private fun createHeaderViewHolder(viewHolder: ViewHolder, position: Int): HeaderHolder? {
        if (position < 0)
            return null
        cursor!!.moveToPosition(position)
        val date = getSectionCondition(cursor)

        val headerHolder = viewHolder as HeaderHolder
        headerHolder.setHeader(date)

        if (!attachAlwaysLastHeader()) {
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


    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     */
    fun swapCursor(newCursor: Cursor): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver)
        }
        cursor = newCursor
        if (cursor != null) {
            if (mDataSetObserver != null) {
                cursor!!.registerDataSetObserver(mDataSetObserver)
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id")
            mDataValid = true
            notifyDataSetChanged()
        } else {
            mRowIdColumn = -1
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }

        oldCursor?.close()

        if (isSection)
            calculateSectionHeaders()
        recyclerView.setIsRefreshing(false)
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }


    override fun getItemViewType(position: Int): Int {

        if (!isSection) {
            return getItemType(position)
        }
        return if (mSectionsIndexer.indexOfKey(position) >= 0) {
            TYPE_HEADER
        } else {
            getItemType(position - countNumberSectionsBefore(position))
        }

    }

    fun getAdapterPosition(viewHolder: VH): Int {
        val position = viewHolder.adapterPosition
        return position - countNumberSectionsBefore(position)
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

    companion object {
        val TYPE_HEADER = 0

        val HORIZONTAL = OrientationHelper.HORIZONTAL

        val VERTICAL = OrientationHelper.VERTICAL
    }
}