package com.recycler.page.recycler

import androidx.recyclerview.widget.RecyclerView

/**
 * Author:yangfeng
 * Data：yangfeng on 2021/12/12 10:09
 * Email：yf767044771@163.com
 */
interface ListItemsVisibilityCalculator {
    /**
     * 滑动监听事件
     */
    fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
    fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int)
    fun detachToSnapHelper()
}