package com.example.raffit.ui.search.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.request.CachePolicy
import com.example.raffit.R
import com.example.raffit.data.model.SearchModel
import com.example.raffit.databinding.ItemImgBinding
import com.example.raffit.databinding.ItemSortBtnBinding
import com.example.raffit.databinding.ItemTypeBtnBinding
import com.example.raffit.ui.search.model.SearchViewItem
import java.lang.IllegalStateException
import kotlin.reflect.KFunction1

class SearchAdapter(
    private val btnClick: (view: View) -> Unit,
    private val itemClick: KFunction1<SearchModel, Unit>
) : ListAdapter<SearchViewItem, RecyclerView.ViewHolder>(SearchDiffCallback) {//서치 모델을 실드클래스로

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TYPE -> {
                val binding =
                    ItemTypeBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TypeHolder(binding, btnClick)
            }

            ITEM_VIEW_TYPE_SORT -> {
                val binding =
                    ItemSortBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SortHolder(binding, btnClick)
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
            is TypeHolder -> {
                layoutParams.isFullSpan = true
                val item = getItem(position) as SearchViewItem.ItemType
                holder.setView(item.type)
            }

            is SortHolder -> {
                layoutParams.isFullSpan = true
                val item = getItem(position) as SearchViewItem.SortType
                holder.setView(item.sort)
            }

            is ContentsHolder -> {
                layoutParams.isFullSpan = false
                val item = getItem(position) as SearchViewItem.Contents
                holder.setView(item.content)
            }
        }
    }

    class TypeHolder(
        private val binding: ItemTypeBtnBinding,
        private val btnClick: (
            view: View
        ) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun setButtonStyle(view: View, textView: TextView, imageView: ImageView, isSelected: Boolean) {
            val backgroundColor = if (isSelected) R.color.background else R.color.enable_btn
            val textColor = if (isSelected) R.color.white else R.color.black
            val iconColor = if (isSelected) R.color.white else R.color.enable_icon

            view.background.setTint(ContextCompat.getColor(view.context, backgroundColor))
            textView.setTextColor(ContextCompat.getColor(textView.context, textColor))
            imageView.setColorFilter(ContextCompat.getColor(imageView.context, iconColor))
        }
        fun setView(itemType: String) = with(binding) {
            val isTotalSelected = itemType == "total"
            val isImgSelected = itemType == "image"
            val isVideosSelected = itemType == "videos"

            setButtonStyle(btItemTotal, tvItemTotal, ivItemTotal, isTotalSelected)
            setButtonStyle(btItemImg, tvItemImg, ivItemImg, isImgSelected)
            setButtonStyle(btItemVideo, tvItemVideo, ivItemVideo, isVideosSelected)

            btItemTotal.setOnClickListener { btnClick(it) }
            btItemImg.setOnClickListener { btnClick(it) }
            btItemVideo.setOnClickListener { btnClick(it) }
        }
    }

    class SortHolder(
        private val binding: ItemSortBtnBinding, private val btnClick: (
            view: View
        ) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private fun setButtonStyle(imageView: ImageView, textView: TextView, isSelected: Boolean) {
            val iconColor = if (isSelected) R.color.background else R.color.enable
            val textColor = if (isSelected) R.color.background else R.color.enable

            textView.setTextColor(ContextCompat.getColor(textView.context, textColor))
            imageView.setColorFilter(ContextCompat.getColor(imageView.context, iconColor))
        }
        fun setView(sort: String) = with(binding) {
            val isAccuracy = sort == "accuracy"
            val isRecency = sort == "recency"
            setButtonStyle(ivItemAccuracy, tvItemAccuracy, isAccuracy)
            setButtonStyle(ivItemRecency, tvItemRecency, isRecency)

            btItemAccuracy.setOnClickListener { btnClick(it) }
            btItemRecency.setOnClickListener { btnClick(it) }
        }
    }

    class ContentsHolder(
        private val binding: ItemImgBinding, private val itemClick: KFunction1<SearchModel, Unit>
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(item: SearchModel) = with(binding) {
            val start = System.currentTimeMillis()
            var count = 1
            ivThumbnail.load(item.thumnail) {
                crossfade(true)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                listener(
                    onStart = {
                    },
                    onSuccess = { _, _ ->
                        val end = System.currentTimeMillis()
                        Log.d("test_coil", "Item $position Time: ${end - start}ms")
                        tvError.isVisible = false
                        ivThumbnailError.isVisible = false
                    },
                    onError = { _, _ ->
                        Log.e("test_coil", "failed: ${item.thumnail}")
                        count++
                        tvError.isVisible = true
                        ivThumbnailError.isVisible = true
                    }
                )
            }
            tvTitle.text = item.title
            tvDate.text = item.date
            tvSite.text = item.siteName
            ivType.toggleType(item.postType)
            ivBookmark.toggleFavorite(item.bookMark)
            root.setOnClickListener {
                itemClick(item)
            }
        }
        private fun ImageView.toggleFavorite(bookmark: Boolean) {
            if (bookmark) {
                setColorFilter(ContextCompat.getColor(context, R.color.background))
            } else {
                setColorFilter(ContextCompat.getColor(context, R.color.white))
            }
        }
        private fun ImageView.toggleType(postType: String) {
            when (postType) {
                "Image" -> setImageResource(R.drawable.baseline_image_24)
                "Video" -> setImageResource(R.drawable.baseline_ondemand_video_24)
                else -> Log.e("SearchAdapter", "unknown postType: $postType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchViewItem.ItemType -> ITEM_VIEW_TYPE_TYPE
            is SearchViewItem.SortType -> ITEM_VIEW_TYPE_SORT
            is SearchViewItem.Contents -> ITEM_VIEW_TYPE_ITEM
        }
    }

    companion object {
        val SearchDiffCallback = object : DiffUtil.ItemCallback<SearchViewItem>() {
            override fun areContentsTheSame(
                oldItem: SearchViewItem,
                newItem: SearchViewItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: SearchViewItem,
                newItem: SearchViewItem
            ): Boolean {
                return when {
                    oldItem is SearchViewItem.Contents && newItem is SearchViewItem.Contents ->
                        oldItem.content.thumnail == newItem.content.thumnail

                    oldItem is SearchViewItem.ItemType && newItem is SearchViewItem.ItemType
                            || oldItem is SearchViewItem.SortType && newItem is SearchViewItem.SortType -> true

                    else -> false
                }
            }
        }

        private const val ITEM_VIEW_TYPE_TYPE = 0
        private const val ITEM_VIEW_TYPE_SORT = 1
        private const val ITEM_VIEW_TYPE_ITEM = 2
    }

}