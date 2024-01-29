package com.example.raffit.ui.mybox.model

import com.example.raffit.data.model.SearchModel

sealed class MyBoxViewItem {
    data class Header(val title: String) : MyBoxViewItem()
    data class Contents(val content: SearchModel) : MyBoxViewItem()
}
