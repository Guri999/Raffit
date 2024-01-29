package com.example.raffit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class SearchModel(
    val thumnail: String,
    val title: String,
    var postType: String,
    val siteName: String,
    val date: String,
    val docUrl: String,
    var bookMark: Boolean = false
): Parcelable
