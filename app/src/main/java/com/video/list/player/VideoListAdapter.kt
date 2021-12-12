package com.video.list.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.video.list.player.bean.VideoInfo

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.VideoListHolder>() {

    val data = mutableListOf<VideoInfo>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        val holder = VideoListHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: VideoListHolder, position: Int) {
        val item = data[position]
        val context = holder.itemView.context
        Glide.with(context).load(item.cover).into(holder.mIVCover)
    }

    fun setNewData(newData: Collection<VideoInfo>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: Collection<VideoInfo>) {
        data.addAll(newData)
        val newSize = newData.size
        val size = data.size
        notifyItemRangeInserted(size - newSize, newSize)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class VideoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIVCover = itemView.findViewById<ImageView>(R.id.mIVCover)
    }
}