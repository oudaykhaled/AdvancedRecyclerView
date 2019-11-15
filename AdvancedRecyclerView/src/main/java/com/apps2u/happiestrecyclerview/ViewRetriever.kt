package com.apps2u.happiestrecyclerview

import androidx.recyclerview.widget.*
import android.view.ViewGroup

internal interface ViewRetriever {

    fun getViewHolderForPosition(headerPositionToShow: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder?

    class RecyclerViewRetriever internal constructor(private val recyclerView: androidx.recyclerview.widget.RecyclerView) : ViewRetriever {

        private var currentViewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        private var currentViewType: Int = 0

        init {
            this.currentViewType = -1
        }

        override fun getViewHolderForPosition(position: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder? {
            if (currentViewType != recyclerView.adapter!!.getItemViewType(position)) {
                currentViewType = recyclerView.adapter!!.getItemViewType(position)
                currentViewHolder = recyclerView.adapter!!.createViewHolder(
                        recyclerView.parent as ViewGroup, currentViewType)
            }
            return currentViewHolder
        }
    }
}
