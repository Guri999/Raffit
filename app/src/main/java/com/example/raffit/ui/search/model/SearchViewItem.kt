package com.example.raffit.ui.search.model

import com.example.raffit.data.model.SearchModel

sealed class SearchViewItem {
    data class SortType(val sort: String): SearchViewItem()
    data class ItemType(val type: String): SearchViewItem()
    data class Contents(val content: SearchModel) : SearchViewItem()
}
