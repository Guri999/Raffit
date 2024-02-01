package com.example.raffit.data.search

import com.example.raffit.data.model.SearchModel

interface ApiRepository{
    suspend fun searchData(query: String, sort: String, page: Int, type: String): MutableList<SearchModel>
}