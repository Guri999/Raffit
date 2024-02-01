package com.example.raffit.ui.mybox.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.raffit.data.bookmark.BookmarkRepository
import com.example.raffit.utill.SaveImageToFile

class MyBoxViewModelFactory(
    private val bookmarkRepository: BookmarkRepository,
    private val saveImageToFile: SaveImageToFile
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyBoxViewModel::class.java)) {
            return MyBoxViewModel (bookmarkRepository,saveImageToFile) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}