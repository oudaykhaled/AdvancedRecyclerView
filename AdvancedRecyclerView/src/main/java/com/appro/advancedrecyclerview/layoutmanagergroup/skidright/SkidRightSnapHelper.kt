/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific languag`e governing permissions and
 * limitations under the License.
 */

package com.appro.advancedrecyclerview.layoutmanagergroup.skidright

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

class SkidRightSnapHelper : SnapHelper() {
    private var mDirection: Int = 0

    override fun calculateDistanceToFinalSnap(
            layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {

        if (layoutManager is LayoutManagerSkidRight) {
            val out = IntArray(2)
            if (layoutManager.canScrollHorizontally()) {
                out[0] = layoutManager.calculateDistanceToPosition(
                        layoutManager.getPosition(targetView))
                out[1] = 0
            } else {
                out[0] = 0
                out[1] = layoutManager.calculateDistanceToPosition(
                        layoutManager.getPosition(targetView))
            }
            return out
        }
        return null
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int,
                                        velocityY: Int): Int {
        if (layoutManager.canScrollHorizontally()) {
            mDirection = velocityX
        } else {
            mDirection = velocityY
        }
        return RecyclerView.NO_POSITION
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager is LayoutManagerSkidRight) {
            val pos = layoutManager.getFixedScrollPosition(
                    mDirection, if (mDirection != 0) 0.8f else 0.5f)
            mDirection = 0
            if (pos != RecyclerView.NO_POSITION) {
                return layoutManager.findViewByPosition(pos)
            }
        }
        return null
    }
}
