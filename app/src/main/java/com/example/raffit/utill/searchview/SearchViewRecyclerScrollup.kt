package com.example.raffit.utill.searchview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING

class SearchViewRecyclerScrollup {
    operator fun invoke (
        recyclerView: RecyclerView,
        searchView: androidx.appcompat.widget.SearchView
    ){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_DRAGGING){
                    searchView.animate().translationY(-searchView.height.toFloat()).duration = 300
                    searchView.visibility = View.INVISIBLE
                } else {
                    searchView.animate().translationY(0f).duration = 300
                    searchView.visibility = View.VISIBLE
                }
            }
        })
    }
}