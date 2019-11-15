package com.apps2u.happiestrecyclerview.swipe.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.apps2u.happiestrecyclerview.swipe.SwipeLayout
import com.apps2u.happiestrecyclerview.swipe.implments.SwipeItemAdapterMangerImpl
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.apps2u.happiestrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.apps2u.happiestrecyclerview.swipe.util.Attributes

abstract class ArraySwipeAdapter<T> : ArrayAdapter<Any>, SwipeItemMangerInterface, SwipeAdapterInterface {

    private val mItemManger = SwipeItemAdapterMangerImpl(this)

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
    }

    constructor(context: Context, resource: Int) : super(context, resource) {}

    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(context, resource, textViewResourceId) {}

    constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects) {}

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: Array<T>) : super(context, resource, textViewResourceId, objects) {}

    constructor(context: Context, resource: Int, objects: List<T>) : super(context, resource, objects) {}

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>) : super(context, resource, textViewResourceId, objects) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertViewIsNull = convertView == null
        val v = super.getView(position, convertView, parent)
        if (convertViewIsNull) {
            mItemManger.initialize(v, position)
        } else {
            mItemManger.updateConvertView(v, position)
        }
        return v
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
}
