package com.example.raffit.data.bookmark

import android.content.Context
import android.content.SharedPreferences
import com.example.raffit.data.model.SearchModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarkRepositoryImpl(context: Context) : BookmarkRepository {
    private val pref = context.getSharedPreferences("MyBox", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun addBookmark(item: SearchModel) {
        val editor = pref.edit()
        val jsonString = gson.toJson(item)
        editor.putString(item.thumnail, jsonString)
        editor.apply()
    }

    override suspend fun loadBookmark(): MutableList<SearchModel> = withContext(Dispatchers.IO) {
        val items = mutableListOf<SearchModel>()
        pref.all.forEach { (_, value) ->
            val jsonString = value as String
            val item = gson.fromJson(jsonString, SearchModel::class.java)
            items.add(item)
        }
        items
    }

    override suspend fun removeBookmark(item: SearchModel): MutableList<SearchModel> {
        val editor = pref.edit()
        editor.remove(item.thumnail)
        editor.apply()
        return loadBookmark()
    }

    override fun checkBookmark(): Map<String,String> {
        return pref.all.mapNotNull { (key, value) ->
            val jsonString = value as String
            val item = gson.fromJson(jsonString, SearchModel::class.java)
            item.let { key to it.thumnail }
        }.toMap()
    }
}