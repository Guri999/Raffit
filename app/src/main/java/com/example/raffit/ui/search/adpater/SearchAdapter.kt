package com.example.raffit.ui.search.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.raffit.R
import com.example.raffit.data.model.SearchModel
import com.example.raffit.databinding.ItemImgBinding

class SearchAdapter(
    private val itemClick: (view: View, position: Int, item: SearchModel) -> Unit
) : ListAdapter<SearchModel, SearchAdapter.Holder>(SearchDiffCallback) {//서치 모델을 실드클래스로


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.setView(item)
    }

    class Holder(
        private val binding: ItemImgBinding, private val itemClick: (
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
            ivBookmark.toggleFavorite(item.bookMark)
            root.setOnClickListener {
                itemClick(it, adapterPosition, item)
            }
        }

        private fun ImageView.toggleFavorite(bookmark: Boolean) {
            if (bookmark) {
                setColorFilter(ContextCompat.getColor(context,R.color.background))
            } else {
                setColorFilter(ContextCompat.getColor(context,R.color.white))
            }
        }

        private fun ImageView.toggleType(postType: String) {
            when (postType){
                "Image" -> setImageResource(R.drawable.baseline_image_24)
                "Video" -> setImageResource(R.drawable.baseline_ondemand_video_24)
                else -> Log.e("SearchAdapter","unknown postType: $postType")
            }
        }
    }

    companion object {
        val SearchDiffCallback = object : DiffUtil.ItemCallback<SearchModel>() {
            override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem.thumnail == newItem.thumnail
            }
        }
    }

}