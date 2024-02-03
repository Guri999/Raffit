package com.example.raffit.ui.search.model

import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raffit.data.search.ApiRepository
import com.example.raffit.data.model.SearchModel
import com.example.raffit.data.bookmark.BookmarkRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: ApiRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val _itemList: MutableLiveData<MutableList<SearchModel>> = MutableLiveData()
    val itemList: LiveData<MutableList<SearchModel>> get() = _itemList

    private val _searchState: MutableLiveData<SearchState> = MutableLiveData(SearchState(""))
    val searchState: LiveData<SearchState> get() = _searchState

    private val _searchViewItem: MutableLiveData<List<SearchViewItem>> = MutableLiveData()
    val searchViewItem: LiveData<List<SearchViewItem>> get() = _searchViewItem
    fun searchItems(state: SearchState) {
        viewModelScope.launch {
            runCatching {
                val start = System.currentTimeMillis()
                val search = repository.searchData(
                    query = state.query,
                    sort = state.sort,
                    page = state.page,
                    type = state.itemType
                )
                _itemList.postValue(search.toMutableList())

                val end = System.currentTimeMillis()
                Log.d("test_searchItems", "Load Data Time: ${end - start}ms")
            }.onFailure { error ->
                Log.e("SearchViewModel", "Error fetching data: ${error.message}", error)
            }
        }
    }

    fun queryState(query: String) {
        runCatching {
            _searchState.value = _searchState.value?.let {
                it.copy(query = query)
            }
        }.onFailure { error ->
            Log.e("queryState", "Error fetching data: ${error.message}", error)
        }
    }

    fun sortState(sort: String) {
        _searchState.value = _searchState.value?.let {
            it.copy(sort = sort, page = 1)
        }
    }

    fun typeState(type: String) {
        _searchState.value = _searchState.value?.let {
            it.copy(itemType = type, page = 1)
        }
    }

    fun scrollPage() {
        runCatching {
            _searchState.value = _searchState.value?.let {
                it.copy(page = it.page.plus(1))
            }
        }.onFailure { error ->
            Log.e("SearchViewModel", "Error fetching data: ${error.message}", error)
        }
    }

    fun addBookmark(item: SearchModel) {
        runCatching {
            if (item.bookMark.not()) {
                bookmarkRepository.addBookmark(item)
            } else {
                viewModelScope.launch {
                    bookmarkRepository.removeBookmark(item)
                }
            }
        }.onFailure { error ->
            Log.e("SearchViewModelAddBookmark", "Error fetching data: ${error.message}", error)
        }
        setBookmark()
    }
    fun setBookmark() {
        val start = System.currentTimeMillis()
        val bookmarkChk = bookmarkRepository.checkBookmark()
        runCatching {
            val bookmarkItem = itemList.value.orEmpty().map { item ->
                if (bookmarkChk[item.thumnail] != null) {
                    item.copy(bookMark = true)
                } else {
                    item.copy(bookMark = false)
                }
            }
            val viewTypeItems = listOf(
                SearchViewItem.ItemType(searchState.value?.itemType ?: "total"),
                SearchViewItem.SortType(searchState.value?.sort ?: "recency")
            ) + bookmarkItem.map { SearchViewItem.Contents(it.copy()) }

            val end = System.currentTimeMillis()
            Log.d("test_bookmark", "Time: ${end - start}ms")
            _searchViewItem.value = viewTypeItems
        }.onFailure { error ->
            Log.e("setBookmark", "Error fetching data: ${error.message}", error)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(
    private val repository: ApiRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository, bookmarkRepository) as T
        } else throw IllegalArgumentException("Not found ViewModel class.")
    }
}