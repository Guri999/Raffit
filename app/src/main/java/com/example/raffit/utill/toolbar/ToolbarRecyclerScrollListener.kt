package com.example.raffit.utill.toolbar

import androidx.recyclerview.widget.RecyclerView

class ToolbarRecyclerScrollListener(
    private val toolbar: androidx.appcompat.widget.Toolbar
) {
    operator fun invoke(recyclerView: RecyclerView) {
        var toolbarOffset = 0
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                toolbarOffset += dy
                if (toolbarOffset > toolbar.height && toolbar.translationY == 0f) {
                    toolbar.animate().translationY(-toolbar.height.toFloat()).setDuration(300)
                        .start()
                } else if (toolbarOffset < 0 && toolbar.translationY != 0f) {
                    toolbar.animate().translationY(0f).setDuration(300).start()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(-1)) {
                    toolbar.animate().translationY(0f).setDuration(300).start()
                    toolbarOffset = 0 // Reset the offset when toolbar is shown
                }
            }
        })
    }
}