package com.apps2u.happiestrecyclerview

import android.view.View
import android.widget.FrameLayout

import androidx.coordinatorlayout.widget.CoordinatorLayout

internal object Preconditions {

    fun <T> checkNotNull(item: T?, message: String): T {
        if (item == null) {
            throw NullPointerException(message)
        }
        return item
    }

    fun validateParentView(recyclerView: View) {
        val parentView = recyclerView.parent as View
        kotlin.require(!(parentView !is FrameLayout && parentView !is CoordinatorLayout)) { "RecyclerView parent must be either a FrameLayout or CoordinatorLayout" }
    }
}
