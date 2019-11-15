package com.appro.advancedrecyclerview.swipe.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.appro.advancedrecyclerview.swipe.SwipeLayout
import com.appro.advancedrecyclerview.swipe.implments.SwipeItemAdapterMangerImpl
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeAdapterInterface
import com.appro.advancedrecyclerview.swipe.interfaces.SwipeItemMangerInterface
import com.appro.advancedrecyclerview.swipe.util.Attributes

/**
 * Created by Ouday Khaled on 5/23/2018.
 */

abstract class BaseSwipeAdapter : BaseAdapter(), SwipeItemMangerInterface, SwipeAdapterInterface {

    protected var mItemManger = SwipeItemAdapterMangerImpl(this)

    override val openItems: List<Int>
        get() = mItemManger.openItems

    override val openLayouts: List<SwipeLayout>
        get() = mItemManger.openLayouts

    override var mode: Attributes.Mode
        get() = mItemManger.mode
        set(mode) {
            mItemManger.mode = mode
        }

    /**
     * return the [com.appro.advancedrecyclerview.swipe.SwipeLayout] resource id, int the view item.
     * @param position
     * @return
     */
    abstract override fun getSwipeLayoutResourceId(position: Int): Int

    /**
     * generate a new view item.
     * Never bind SwipeListener or fill values here, every item has a chance to fill value or bind
     * listeners in fillValues.
     * to fill it in `fillValues` method.
     * @param position
     * @param parent
     * @return
     */
    abstract fun generateView(position: Int, parent: ViewGroup): View

    /**
     * fill values or bind listeners to the view.
     * @param position
     * @param convertView
     */
    abstract fun fillValues(position: Int, convertView: View)


    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var v: View? = convertView
        if (v == null) {
            v = generateView(position, parent)
            mItemManger.initialize(v, position)
        } else {
            mItemManger.updateConvertView(v, position)
        }
        fillValues(position, v)
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
