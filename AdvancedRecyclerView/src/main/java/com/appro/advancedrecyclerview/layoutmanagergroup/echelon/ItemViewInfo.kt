package com.appro.advancedrecyclerview.layoutmanagergroup.echelon

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */
class ItemViewInfo(var top: Int, var scaleXY: Float, var positionOffset: Float, var layoutPercent: Float) {
    private var mIsBottom: Boolean = false

    fun setIsBottom(): ItemViewInfo {
        mIsBottom = true
        return this
    }

}
