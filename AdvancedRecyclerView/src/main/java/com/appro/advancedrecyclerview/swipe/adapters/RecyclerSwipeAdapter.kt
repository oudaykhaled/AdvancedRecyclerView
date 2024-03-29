package com.appro.advancedrecyclerview.swipe.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appro.advancedrecyclerview.swipe.SwipeLayout
import com.appro.advancedrecyclerview.swipe.implments.SwipeItemRecyclerMangerImpl
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.appro.advancedrecyclerview.swipe.util.Attributes

/**
 * Created by Ouday Khaled on 5/23/2018.
 */
abstract class RecyclerSwipeAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), SwipeItemMangerInterface, SwipeAdapterInterface {

    var mItemManger = SwipeItemRecyclerMangerImpl(this)

    override val openItems: List<Int>
        get() = mItemManger.openItems

    override val openLayouts: List<SwipeLayout>
        get() = mItemManger.openLayouts

    override var mode: Attributes.Mode
        get() = mItemManger.mode
        set(mode) {
            mItemManger.mode = mode
        }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract override fun onBindViewHolder(viewHolder: VH, position: Int)

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
}
