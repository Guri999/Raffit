package com.example.raffit.ui.search.model

import android.util.Log
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

    private val _page: MutableLiveData<Int> = MutableLiveData(1)
    val page: LiveData<Int> get() = _page

    fun searchItems(query: String, page: Int) {
        viewModelScope.launch {
            runCatching {
                val search = repository.searchData(query, "recency", page)
                _itemList.postValue(search.toMutableList())
            }.onFailure { error ->
                Log.e("SearchViewModel", "Error fetching data: ${error.message}", error)
            }
            setBookmark()
        }
    }

    fun scrollPage() {
        runCatching {
            _page.value = _page.value?.plus(1)
        }.onFailure { error ->
            Log.e("SearchViewModel", "Error fetching data: ${error.message}", error)
        }
        setBookmark()
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

    private fun setBookmark() {
        val bookmarkChk = bookmarkRepository.checkBookmark()
        runCatching {
            val bookmarkItem = _itemList.value.orEmpty().map { item ->
                if (bookmarkChk[item.thumnail] != null) {
                    item.copy(bookMark = true)
                } else {
                    item.copy(bookMark = false)
                }
            }
            _itemList.value = bookmarkItem.toMutableList()
        }.onFailure { error ->
            Log.e("setBookmark", "Error fetching data: ${error.message}", error)
        }
    }
}

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