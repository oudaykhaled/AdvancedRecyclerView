package com.apps2u.happiestrecyclerview

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

class AdsHolder(itemView: View) : ViewHolder(itemView) {
    var relativeLayout: RelativeLayout

    init {
        relativeLayout = itemView.findViewById<View>(R.id.parent_layout) as RelativeLayout
    }


}
