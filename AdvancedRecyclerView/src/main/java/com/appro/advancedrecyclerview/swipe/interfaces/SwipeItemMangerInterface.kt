package com.appro.advancedrecyclerview.swipe.interfaces


import com.appro.advancedrecyclerview.swipe.SwipeLayout
import com.appro.advancedrecyclerview.swipe.util.Attributes

/**
 * Created by Ouday Khaled on 5/23/2018.
 */

interface SwipeItemMangerInterface {

    val openItems: List<Int>

    val openLayouts: List<SwipeLayout>

    var mode: Attributes.Mode

    fun openItem(position: Int)

    fun closeItem(position: Int)

    fun closeAllExcept(layout: SwipeLayout)

    fun closeAllItems()

    fun removeShownLayouts(layout: SwipeLayout)

    fun isOpen(position: Int): Boolean
}

