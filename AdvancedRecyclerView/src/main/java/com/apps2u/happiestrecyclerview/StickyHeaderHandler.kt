package com.apps2u.happiestrecyclerview

import android.util.SparseArray

internal interface StickyHeaderHandler {

    /**
     * @return The dataset supplied to the [RecyclerView.Adapter]
     */
    val adapterData: SparseArray<String>?
}
