package com.apps2u.happiestrecyclerview

import android.view.ViewGroup
import java.util.*

abstract class Injection(viewType: ArrayList<Int>, val starting: Int, val concurrency: Int, tag: String) {
    var tag: String
        internal set
    var viewTypes: ArrayList<Int>
        internal set

    init {
        this.viewTypes = viewType
        this.tag = tag
    }

    fun onCreateViewHolders(parent: ViewGroup, viewType: Int, tag: String): ViewHolder? {
        return if (this.tag == tag)
            onCreateViewHolder(parent, viewType)
        else
            null
    }

    fun onBindViewHolders(viewHolder: ViewHolder, position: Int, tag: String) {
        if (this.tag == tag)
            onBindViewHolder(viewHolder, position)
    }

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder

    abstract fun onBindViewHolder(viewHolder: ViewHolder, position: Int)
}
