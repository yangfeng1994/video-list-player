package com.recycler.page.recycler

import android.view.View

interface OnSnapHelperCurrentListener {

    /**
     * 选中哪个view
     *
     * @param currentView      当前选中的View
     * @param position         当前选中的position
     * @param previousView     上一次选中的View  为null 暂不使用
     * @param previousPosition 上一次选中的position
     */
    fun setActive(currentView: View?, position: Int, previousView: View?, previousPosition: Int)

    /**
     * view detached的时候调用
     * 避免快速滑动，View不被移除
     *
     * @param detachedView     翻页过后的View
     * @param detachedPosition 翻页过后的position
     */
    fun disActive(detachedView: View?, detachedPosition: Int)
}