package com.recycler.page.recycler

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import androidx.recyclerview.widget.SnapHelper
import java.lang.Exception

/**
 * Author:yangfeng
 * Data：yangfeng on 2021/12/12 10:14
 * Email：yf767044771@163.com
 *
 *
 * val recyclerViewScrollerDetection = RecyclerViewScrollerDetection()
 *  recyclerViewScrollerDetection.setCurrentListener(this)
 *  recyclerViewScrollerDetection.attachToSnapHelper(snapHelper)
 *  recyclerViewScrollerDetection.addOnScrollListener(mRecyclerView)
 */
class RecyclerViewScrollerDetection : ListItemsVisibilityCalculator {

    private var mSnapHelper: SnapHelper? = null
    private var mCurrentListener: OnSnapHelperCurrentListener? = null
    var position = RecyclerView.NO_POSITION
    private var previousPosition = RecyclerView.NO_POSITION
    private var isFirstAttached = false
    private var recyclerView: RecyclerView? = null
    val mSnapScrollListener = SnapScrollListener()
    var mSnapChildAttachStateListener: SnapChildAttachStateChangeListener? = null

    /**
     * 第一次进来，是否调用setActive
     */
    var isFirstActive = true
    private var currentItemView: View? = null
    private var previousView: View? = null


    fun attachToSnapHelper(snapHelper: SnapHelper) {
        mSnapHelper = snapHelper
    }

    /**
     * 设置监听
     *
     * @param recyclerView
     */
    fun addOnScrollListener(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(mSnapScrollListener)
        mSnapChildAttachStateListener = SnapChildAttachStateChangeListener(recyclerView)
        mSnapChildAttachStateListener?.run {
            recyclerView.addOnChildAttachStateChangeListener(this)
        }
    }

    inner class SnapScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            this@RecyclerViewScrollerDetection.onScrollStateChanged(recyclerView, newState)
        }
    }

    inner class SnapChildAttachStateChangeListener(var recyclerView: RecyclerView) :
        OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (!isFirstActive || isFirstAttached) return
            position = recyclerView.getChildAdapterPosition(view)
            currentItemView = view
            mCurrentListener?.setActive(view, position, null, previousPosition)
            previousPosition = position
            previousView = view
            isFirstAttached = true
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            val previousPosition = recyclerView.getChildAdapterPosition(view)
            currentItemView?.run {
                val position = recyclerView.getChildAdapterPosition(
                    this
                )
                mCurrentListener?.let {
                    if (position == previousPosition) {
                        it.disActive(view, previousPosition)
                    }
                }
            }
        }

    }

    fun setCurrentListener(mCurrentListener: OnSnapHelperCurrentListener?) {
        this.mCurrentListener = mCurrentListener
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        try {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    val layoutManager = recyclerView.layoutManager
                    currentItemView = mSnapHelper?.findSnapView(layoutManager)
                    if (null == currentItemView) return
                    val currentPosition = recyclerView.getChildAdapterPosition(currentItemView!!)
                    if (previousPosition == currentPosition || null == mCurrentListener) return
                    position = recyclerView.getChildAdapterPosition(currentItemView!!)
                    val mPreviousView = previousView
                    val mPreviousPosition = previousPosition
                    mCurrentListener?.setActive(
                        currentItemView,
                        position,
                        mPreviousView,
                        mPreviousPosition
                    )
                    previousPosition = currentPosition
                    previousView = currentItemView
                }
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                }
                RecyclerView.SCROLL_STATE_SETTLING -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("yyyyy", Log.getStackTraceString(e))
        }
    }

    override fun detachToSnapHelper() {
        recyclerView?.removeOnScrollListener(mSnapScrollListener)
        mSnapChildAttachStateListener?.run {
            recyclerView?.removeOnChildAttachStateChangeListener(
                this
            )
        }
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {}

}