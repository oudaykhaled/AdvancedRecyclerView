package com.apps2u.happiestrecyclerview

import android.view.View

class FooterHolder(itemView: View) : ViewHolder(itemView) {


    fun setVisibility(visibility: Boolean) {
        val param = itemView.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
        if (visibility) {
            param.height = androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
            param.width = androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT
            itemView.visibility = View.VISIBLE
        } else {
            itemView.visibility = View.GONE
            param.height = 0
            param.width = 0
        }
        itemView.layoutParams = param
    }
}
