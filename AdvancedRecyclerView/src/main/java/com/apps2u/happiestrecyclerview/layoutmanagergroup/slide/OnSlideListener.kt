package com.apps2u.happiestrecyclerview.layoutmanagergroup.slide

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


interface OnSlideListener<T> {

    fun onSliding(viewHolder: RecyclerView.ViewHolder, ratio: Float, direction: Int)

    fun onSlided(viewHolder: RecyclerView.ViewHolder, t: T, direction: Int)

    fun onClear()

}
