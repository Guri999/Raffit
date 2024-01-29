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
    private var bookmarksCache: MutableList<SearchModel>? = null

    override fun addBookmark(item: SearchModel) {
        val editor = pref.edit()
        val jsonString = gson.toJson(item)
        editor.putString(item.thumnail, jsonString)
        editor.apply()
        bookmarksCache?.add(item)
    }

    override suspend fun loadBookmark(): MutableList<SearchModel> = withContext(Dispatchers.IO) {
        bookmarksCache?.let { return@withContext it }
        val items = mutableListOf<SearchModel>()
        pref.all.forEach { (_, value) ->
            val jsonString = value as String
            val item = gson.fromJson(jsonString, SearchModel::class.java)
            items.add(item)
        }
        bookmarksCache = items
        items
    }

    override suspend fun removeBookmark(item: SearchModel): MutableList<SearchModel> {
        val editor = pref.edit()
        editor.remove(item.thumnail)
        editor.apply()
        return loadBookmark().also { bookmarksCache = it }
    }

    override fun checkBookmark(): Map<String,String> {
        return pref.all.mapNotNull { (key, value) ->
            val jsonString = value as String
            val item = gson.fromJson(jsonString, SearchModel::class.java)
            item.let { key to it.thumnail }
        }.toMap()
    }
}
/*
* class BookmarkRepositoryImpl(context: Context) : BookmarkRepository {
    private val pref = context.getSharedPreferences("MyBox", Context.MODE_PRIVATE)
    private val gson = Gson()
    private var bookmarksCache: MutableList<SearchModel>? = null

    override fun addBookmark(item: SearchModel) {
        editSharedPreferences {
            putString(item.thumnail, convertSearchModelToJson(item))
        }
        bookmarksCache?.add(item)
    }

    override fun loadBookmark(): MutableList<SearchModel> {
        bookmarksCache?.let { return it }
        val items = pref.all.mapNotNull { (_, value) ->
            convertJsonToSearchModel(value as? String)
        }.toMutableList()
        bookmarksCache = items
        return items
    }

    override fun removeBookmark(item: SearchModel): MutableList<SearchModel> {
        editSharedPreferences {
            remove(item.thumnail)
        }
        return loadBookmark().also {
            bookmarksCache = it
        }
    }

    override fun checkBookmark(): Map<String, String> {
        return pref.all.mapNotNull { (key, value) ->
            convertJsonToSearchModel(value as? String)?.let { key to it.thumnail }
        }.toMap()
    }

    private fun convertSearchModelToJson(item: SearchModel): String {
        return gson.toJson(item)
    }

    private fun convertJsonToSearchModel(jsonString: String?): SearchModel? {
        return jsonString?.let { gson.fromJson(it, SearchModel::class.java) }
    }

    private fun editSharedPreferences(action: SharedPreferences.Editor.() -> Unit) {
        with(pref.edit()) {
            action()
            apply()
        }
    }
}
* */