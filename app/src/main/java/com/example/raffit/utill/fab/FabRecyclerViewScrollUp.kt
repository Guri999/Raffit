package com.example.raffit.utill.fab

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabRecyclerViewScrollUp {
    operator fun invoke(
        fab: FloatingActionButton,
        recyclerView: RecyclerView,
        scrollUpAction: () -> Unit
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.canScrollVertically(-1).not()){
                    fab.animate().scaleX(0.0f).scaleY(0.0f).setDuration(200).withEndAction{
                        fab.visibility = View.GONE
                    }
                } else {
                    fab.visibility = View.VISIBLE
                    fab.animate().scaleX(1.0f).scaleY(1.0f).duration = 200
                }
            }
        })

        fab.setOnClickListener{
            scrollUpAction()
        }
    }
}
/**
 * TODO 플로팅버튼 클릭시 스크롤 이동 + 애니메이션
 *
 * 사용 방법
 * setupFabWithRecyclerView(
 *     fab = binding.faMainFab,
 *     recyclerView = binding.rcMainPost,
 *     scrollUpAction = { binding.rcMainPost.scrollToPosition(0) }
 * )
 */