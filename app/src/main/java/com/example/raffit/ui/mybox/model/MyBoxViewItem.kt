package com.example.raffit.ui.mybox.model

import com.example.raffit.data.model.SearchModel

sealed class MyBoxViewItem {
    data object Header : MyBoxViewItem()
    data class Contents(val content: SearchModel) : MyBoxViewItem()
}
