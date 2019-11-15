package com.apps2u.happiestrecyclerview.layoutmanagergroup.viewpager

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 * 用于ViewPagerLayoutManager的监听
 */

interface OnViewPagerListener {

    /*初始化完成*/
    fun onInitComplete()

    /*释放的监听*/
    fun onPageRelease(isNext: Boolean, position: Int)

    /*选中的监听以及判断是否滑动到底部*/
    fun onPageSelected(position: Int, isBottom: Boolean)


}
