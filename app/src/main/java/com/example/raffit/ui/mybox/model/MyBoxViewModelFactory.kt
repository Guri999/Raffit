package com.example.raffit.ui.mybox.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.raffit.data.bookmark.BookmarkRepository

class MyBoxViewModelFactory(
    private val bookmarkRepository: BookmarkRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyBoxViewModel::class.java)) {
            return MyBoxViewModel (bookmarkRepository) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}