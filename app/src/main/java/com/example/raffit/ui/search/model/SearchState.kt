package com.example.raffit.ui.search.model

data class SearchState(
    val query: String,
    val sort: String = "recency",
    val page: Int = 1,
    val itemType: String = "total",
)
