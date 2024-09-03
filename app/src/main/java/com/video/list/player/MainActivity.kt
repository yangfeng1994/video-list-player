package com.video.list.player

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.recycler.page.recycler.OnSnapHelperCurrentListener
import com.recycler.page.recycler.RecyclerViewScrollerDetection
import com.video.list.player.bean.VideoInfo

class MainActivity : AppCompatActivity(),
    OnSnapHelperCurrentListener {
    val simpleExoPlayer by lazy { SimpleExoPlayer.Builder(this).build() }
    val concatMedia = ConcatenatingMediaSource()
    val playerView by lazy {
        View.inflate(
            this,
            R.layout.view_exo_concat_video_play,
            null
        ) as PlayerView
    }
    val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.mRecyclerView) }
    val lineLayout by lazy { LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) }
    val mAdapter = VideoListAdapter()
    val snapHelper = PagerSnapHelper()
    var currentView: View? = null
    val recyclerViewScrollerDetection = RecyclerViewScrollerDetection()
    override fun onCreate(savedInstanceState: Bundle?) {
        dismissStatusBar()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onInitView()
        setData()
    }

    /**
     *
     */
    private fun onInitView() {
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        playerView.player = simpleExoPlayer
        simpleExoPlayer.addAnalyticsListener(object : AnalyticsListener {
            override fun onRenderedFirstFrame(
                eventTime: AnalyticsListener.EventTime,
                output: Any,
                renderTimeMs: Long
            ) {
                super.onRenderedFirstFrame(eventTime, output, renderTimeMs)
                val mFMVideo = currentView?.findViewById<FrameLayout>(R.id.mFMVideo)
                val mIVCover = currentView?.findViewById<ImageView>(R.id.mIVCover)
                mIVCover?.visibility = View.INVISIBLE
                mFMVideo?.visibility = View.VISIBLE
            }
        })
        mRecyclerView.layoutManager = lineLayout
        mRecyclerView.adapter = mAdapter
        snapHelper.attachToRecyclerView(mRecyclerView)
        recyclerViewScrollerDetection.setCurrentListener(this)
        recyclerViewScrollerDetection.attachToSnapHelper(snapHelper)
        recyclerViewScrollerDetection.addOnScrollListener(mRecyclerView)
    }

    /**
     * 设置数据源
     */
    private fun setData() {
        val json = AssetsUtil.getJson(this, "video_list.json")
        val type = object : TypeToken<ArrayList<VideoInfo>>() {}.type
        val data = Gson().fromJson<ArrayList<VideoInfo>>(json, type)
        data.forEach {
            concatMedia.addMediaSource(getMediaSource(it.mp4Url))
        }
        simpleExoPlayer.setMediaSource(concatMedia)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
        mAdapter.setNewData(data)
    }

    private fun getMediaSource(url: String?): MediaSource {
        // 在播放期间测量带宽。如果不需要，可以为空。
        val bandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
        // 生成用于加载媒体数据的数据源实例。
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, this.packageName), bandwidthMeter
        )
        // 这是媒体资源，代表要播放的媒体。
        val mUri = Uri.parse(url)
        val builder = MediaItem.Builder()
        val mediaMetadata = MediaMetadata.Builder()
        builder.setMediaMetadata(mediaMetadata.build())
        val mediaItem = builder.setUri(mUri).build()
        val videoSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        return videoSource
    }

    /**
     *新增数据源
     */
    private fun addMediaSource(url: String?) {
        val json = AssetsUtil.getJson(this, "新增数据源.json")
        val type = object : TypeToken<ArrayList<VideoInfo>>() {}.type
        val data = Gson().fromJson<ArrayList<VideoInfo>>(json, type)
        val mediaSources = mutableListOf<MediaSource>()
        data.forEach {
            mediaSources.add(getMediaSource(it.mp4Url))
        }
        simpleExoPlayer.addMediaSources(mediaSources)
        mAdapter.addData(data)
    }

    override fun onResume() {
        super.onResume()
        simpleExoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.release()
        recyclerViewScrollerDetection.detachToSnapHelper()
    }


    override fun setActive(
        currentView: View?,
        position: Int,
        previousView: View?,
        previousPosition: Int
    ) {
        this.currentView = currentView
        val mFMVideo = currentView?.findViewById<FrameLayout>(R.id.mFMVideo)
        (playerView?.parent as FrameLayout?)?.removeView(playerView)
        mFMVideo?.removeAllViews()
        mFMVideo?.addView(playerView)
        if (!simpleExoPlayer.isPlaying) {
            simpleExoPlayer.play()
        }
        simpleExoPlayer.seekToDefaultPosition(position)
    }

    override fun disActive(detachedView: View?, detachedPosition: Int) {
        if (null == detachedView) return
        val mIVCover = detachedView.findViewById<ImageView>(R.id.mIVCover)
        val mFMVideo = detachedView.findViewById<FrameLayout>(R.id.mFMVideo)
        mIVCover.visibility = View.VISIBLE
        mFMVideo.visibility = View.INVISIBLE
        if (simpleExoPlayer.isPlaying) {
            simpleExoPlayer.pause()
        }
    }
    private fun dismissStatusBar() {
        BarUtils.transparentStatusBar(this)
        BarUtils.setNavBarVisibility(this, false,this)
    }
}