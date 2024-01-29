package com.example.raffit.utill.appbar

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class AppbarRecyclerScrollListener (
    private val appbar: AppBarLayout
) {
    operator fun invoke(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0 && appbar.visibility == View.VISIBLE) {
                    // 스크롤이 감지되면 AppBar를 위로 슬라이드하며 숨깁니다.
                    appbar.animate().translationY(-appbar.height.toFloat()).setDuration(300)
                        .withEndAction {
                            appbar.visibility = View.GONE
                        }.start()
                } else {
                    appbar.visibility = View.VISIBLE
                    appbar.animate().translationY(0f).setDuration(300).start()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && appbar.visibility == View.GONE) {
                    // 스크롤이 정지되고 AppBar가 숨겨져 있을 때 다시 보이게 합니다.
                    appbar.visibility = View.VISIBLE
                    appbar.animate().translationY(0f).setDuration(300).start()
                }
            }
        })
    }
}