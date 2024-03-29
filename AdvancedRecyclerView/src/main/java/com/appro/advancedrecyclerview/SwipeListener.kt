package com.appro.advancedrecyclerview

interface SwipeListener {

    /**
     * @return The dataset supplied to the [androidx.recyclerview.widget.RecyclerView.Adapter]
     */
    fun onSwipe()

    fun onSwipeConnectionError()
    fun loadMore(current_page: Int)
}
