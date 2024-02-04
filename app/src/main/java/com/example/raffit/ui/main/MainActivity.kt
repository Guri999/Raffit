package com.example.raffit.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.raffit.R
import com.example.raffit.databinding.ActivityMainBinding
import com.example.raffit.utill.fab.FabRecyclerViewScrollUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fabScrollUp by lazy {
        FabRecyclerViewScrollUp()
    }
    private val navController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        initBottomNav()
        setBottomNav()
    }


    private fun initBottomNav() = with(binding){
        navView.setupWithNavController(navController)
    }

    fun setFab(recyclerView: RecyclerView) {
        recyclerView.let {
            fabScrollUp(this.binding.fbMainFab, it) { it.scrollToPosition(0) }
        }
    }

    private fun setBottomNav() = with(binding) {
        val state = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val color = intArrayOf(
            getColor(R.color.background),
            getColor(R.color.enable)
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    navView.elevation = 0.0f
                    navView.background.setTint(getColor(R.color.background))
                    fbMainFab.isVisible = false
                }
                else -> {
                    navView.elevation = 8.0f
                    navView.background.setTint(getColor(R.color.white))
                    navView.itemIconTintList = ColorStateList(state,color)
                }
            }
        }
    }
}