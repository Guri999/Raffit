package com.example.raffit.ui.mybox.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raffit.data.model.SearchModel
import com.example.raffit.data.bookmark.BookmarkRepository
import com.example.raffit.utill.SaveImageToFile
import com.google.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class MyBoxViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val saveImageToFile: SaveImageToFile) : ViewModel() {
    private val _itemList: MutableLiveData<MutableList<SearchModel>> = MutableLiveData()
    val itemList: LiveData<MutableList<SearchModel>> get() = _itemList

    fun loadBookmark() {
        viewModelScope.launch {
            runCatching {
                _itemList.postValue(
                    bookmarkRepository.loadBookmark().map { it.copy() }.toMutableList()
                )
            }.onFailure { error ->
                Log.e("MyBoxViewModel", "Error fetching data: ${error.message}", error)
            }
        }
    }

    fun removeBookmark(item: SearchModel) {
        viewModelScope.launch {
            runCatching {
                _itemList.postValue(
                    bookmarkRepository.removeBookmark(item).map { it.copy() }.toMutableList()
                )
            }.onFailure { error ->
                Log.e("MyBoxViewModel", "Error fetching data: ${error.message}", error)
            }
        }
    }

    fun saveImage(url: String) {
        viewModelScope.launch {
            saveImageToFile(url)
        }
    }
}