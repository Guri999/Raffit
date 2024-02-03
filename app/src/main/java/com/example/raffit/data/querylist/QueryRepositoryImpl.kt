package com.example.raffit.data.querylist

import android.content.Context

interface QueryRepository{
    fun addQueryList(query: String)
}
class QueryRepositoryImpl(context: Context): QueryRepository {
    private val prefs = context.getSharedPreferences("QueryList", Context.MODE_PRIVATE)

    override fun addQueryList(query: String){
        prefs.edit().apply{
            putString(query, query)
            apply()
        }
    }

    private fun getQueryHistory(): List<String> {
        return prefs.getStringSet("terms", null)?.toList() ?: listOf()
    }
}