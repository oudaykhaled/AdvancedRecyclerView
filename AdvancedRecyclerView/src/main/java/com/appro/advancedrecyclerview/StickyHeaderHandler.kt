package com.appro.advancedrecyclerview

import android.util.SparseArray

internal interface StickyHeaderHandler {

    /**
     * @return The dataset supplied to the [RecyclerView.Adapter]
     */
    val adapterData: SparseArray<String>?
}
