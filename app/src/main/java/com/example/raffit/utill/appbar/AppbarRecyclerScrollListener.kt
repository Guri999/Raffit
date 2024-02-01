package com.example.raffit.utill.appbar

import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class AppbarRecyclerScrollListener(
    private val appbar: AppBarLayout
) {
    private var isScrolling = false
    operator fun invoke(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    appbar.visibility = View.VISIBLE
                    appbar.animate().translationY(0f).setDuration(300).start()

                } else {
                    appbar.animate().translationY(-appbar.height.toFloat()).setDuration(300)
                        .withEndAction {
                            appbar.visibility = View.GONE
                        }.start()
                    isScrolling = true
                }
            }
        })
    }
}