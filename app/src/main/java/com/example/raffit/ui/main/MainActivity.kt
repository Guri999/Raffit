package com.example.raffit.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.raffit.R
import com.example.raffit.utill.fab.FabRecyclerViewScrollUp
import com.example.raffit.databinding.ActivityMainBinding
import com.example.raffit.utill.appbar.AppbarRecyclerScrollListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fabScrollUp by lazy {
        FabRecyclerViewScrollUp()
    }
    private val navController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main)
    }

    private val appbarState by lazy {
        AppbarRecyclerScrollListener(binding.abMainAppbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        initBottomNav()
        setSearch()
        loadQuery()
    }

    private fun setSearch() = with(binding) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                saveQuery(query)
                submitQuery(query)
                hideKeyboard(this@MainActivity, searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // 검색어 추천등 추가 하면 좋을 듯
            }
        })
    }

    private fun submitQuery(query: String?): Boolean {
        return if (query == null) {
            false
        } else {
            val bundle = Bundle().apply {
                putString("searchQuery", query)
            }
            navController.navigate(R.id.nav_search_img, bundle)
            true
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initBottomNav() {
        binding.navView.setupWithNavController(navController)
    }

    private fun saveQuery(query: String?) {
        val pref = getSharedPreferences("lastQuery", Context.MODE_PRIVATE)
        pref.edit().apply {
            putString("lastQuery", query)
            apply()
        }
    }

    fun setFab(recyclerView: RecyclerView) {
        recyclerView.let {
            fabScrollUp(this.binding.fbMainFab, it) { it.scrollToPosition(0) }
            appbarState(it)
        }
    }


    private fun loadQuery() {
        val pref = getSharedPreferences("lastQuery", Context.MODE_PRIVATE)

        binding.searchView.setQuery(pref.getString("lastQuery", ""), false)
    }
}