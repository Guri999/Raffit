package com.example.raffit.data.bookmark

import com.example.raffit.data.model.SearchModel

interface BookmarkRepository {
    fun addBookmark(item: SearchModel)
    suspend fun loadBookmark(): MutableList<SearchModel>
    suspend fun removeBookmark(item: SearchModel): MutableList<SearchModel>
    fun checkBookmark(): Map<String, String>
}