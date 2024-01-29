package com.example.raffit.ui.mybox.adapter

import android.content.ClipData.Item
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.example.raffit.R
import com.example.raffit.data.model.SearchModel
import com.example.raffit.databinding.ItemImgBinding
import com.example.raffit.databinding.ItemMydriveBinding
import com.example.raffit.ui.mybox.model.MyBoxViewItem
import java.lang.IllegalStateException

class MyBoxAdapter(
    private val itemClick: (view: View, position: Int, item: SearchModel) -> Unit
) : ListAdapter<MyBoxViewItem, RecyclerView.ViewHolder>(SearchDiffCallback) {//서치 모델을 실드클래스로


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEAD -> {
                val binding =
                    ItemMydriveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderHolder(binding)
            }

            ITEM_VIEW_TYPE_ITEM -> {
                val binding =
                    ItemImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ContentsHolder(binding, itemClick)
            }

            else -> {
                throw IllegalStateException("Unknown ViewType: $viewType")
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        when (holder) {
            is ContentsHolder -> {
                layoutParams.spanIndex
                val item = getItem(position) as MyBoxViewItem.Contents
                holder.setView(item.content)
            }
            is HeaderHolder -> {
                layoutParams.isFullSpan
                holder.setView()
            }
        }
    }

    class ContentsHolder(
        private val binding: ItemImgBinding,
        private val itemClick: (
            view: View,
            position: Int,
            item: SearchModel
        ) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(item: SearchModel) = with(binding) {
            ivThumbnail.load(item.thumnail)
            tvTitle.text = item.title
            tvDate.text = item.date
            tvSite.text = item.siteName
            ivType.toggleType(item.postType)
            ivBookmark.visibility = View.GONE
            root.setOnClickListener {
                itemClick(it, adapterPosition, item)
            }
        }

        private fun ImageView.toggleType(postType: String) {
            when (postType) {
                "Image" -> setImageResource(R.drawable.baseline_image_24)
                "Video" -> setImageResource(R.drawable.baseline_ondemand_video_24)
                else -> Log.e("MyBoxAdapter", "unknown postType: $postType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MyBoxViewItem.Contents -> ITEM_VIEW_TYPE_ITEM
            is MyBoxViewItem.Header -> ITEM_VIEW_TYPE_HEAD
        }
    }

    class HeaderHolder(private val binding: ItemMydriveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView() = with(binding) {
            tvMyboxTitle.setText(R.string.mybox_mydrive)
        }
    }

    companion object {
        val SearchDiffCallback = object : DiffUtil.ItemCallback<MyBoxViewItem>() {
            override fun areContentsTheSame(oldItem: MyBoxViewItem, newItem: MyBoxViewItem): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: MyBoxViewItem, newItem: MyBoxViewItem): Boolean {
                return when {
                    oldItem is MyBoxViewItem.Contents && newItem is MyBoxViewItem.Contents ->
                        oldItem.content.thumnail == newItem.content.thumnail
                    oldItem is MyBoxViewItem.Header && newItem is MyBoxViewItem.Header ->
                        true // Headers are always the same in this context
                    else ->
                        false
                }
            }
        }

        private const val ITEM_VIEW_TYPE_HEAD = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }
}