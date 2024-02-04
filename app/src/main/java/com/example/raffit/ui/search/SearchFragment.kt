package com.example.raffit.ui.search

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.raffit.R
import com.example.raffit.data.model.SearchModel
import com.example.raffit.databinding.FragmentSearchBinding
import com.example.raffit.ui.main.MainActivity
import com.example.raffit.ui.search.adpater.SearchAdapter
import com.example.raffit.ui.search.model.SearchViewModel
import com.example.raffit.utill.appbar.AppbarRecyclerScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private var _adapter: SearchAdapter? = null

    private val viewModel: SearchViewModel by viewModels()

    private val appbarController by lazy {
        AppbarRecyclerScrollListener(binding.abSearchAppbar)
    }

    private val adapter get() = _adapter

    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            sharedElementReturnTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
        }.onFailure {
            Log.e("searchTransition", "$it")
        }
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setAdapter()
        setupInfiniteScroll()
        setSearch()
        setQueryOnSearchView()

        appbarController(binding.rcSearchGrid)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setupFab()
            }
        }

        viewModel.searchState.observe(viewLifecycleOwner) {
            viewModel.searchItems(it)
        }

        viewModel.itemList.observe(viewLifecycleOwner) {
            viewModel.setBookmark()
        }
    }

    private fun setSearch() = with(binding) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                saveQuery(query)
                submitQuery(query)
                hideKeyboard(requireContext(), searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // 검색어 추천등 추가 하면 좋을 듯
            }
        })
    }

    private fun setQueryOnSearchView() = with(binding) {
        val world = arguments?.getString("searchQuery")
        if (world != null) {
            searchView.setQuery(world, true)
            binding.searchView.isIconified = false
        }
    }

    private fun submitQuery(query: String?): Boolean {
        return if (query == null) {
            false
        } else {
            viewModel.queryState(query)
            true
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm =
            context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun saveQuery(query: String?) {
        val pref = requireActivity().getSharedPreferences("lastQuery", Context.MODE_PRIVATE)
        pref.edit().apply {
            putString("lastQuery", query)
            apply()
        }
    }

    private fun setAdapter() {
        _adapter = SearchAdapter(
            btnClick = this::onClickBtn,
            itemClick = this::onClickItem
        )
        binding.rcSearchGrid.adapter = adapter
        binding.rcSearchGrid.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


        viewModel.searchViewItem.observe(viewLifecycleOwner) { models ->
            adapter?.submitList(models)
            isLoading = false
        }
    }

    private fun setupInfiniteScroll() {
        binding.rcSearchGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0 || isLoading) return

                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition =
                    layoutManager.findLastVisibleItemPositions(null).maxOrNull() ?: 0

                if (isLoading.not() && lastVisibleItemPosition > totalItemCount - 5) {
                    viewModel.scrollPage()
                    isLoading = true
                }
            }
        })
    }


    private fun onClickItem(item: SearchModel) {
        viewModel.addBookmark(item)
    }

    private fun onClickBtn(view: View) {
        when (view.id) {
            R.id.bt_item_total -> viewModel.typeState("total")
            R.id.bt_item_img -> viewModel.typeState("image")
            R.id.bt_item_video -> viewModel.typeState("videos")
            R.id.bt_item_accuracy -> viewModel.sortState("accuracy")
            R.id.bt_item_recency -> viewModel.sortState("recency")
        }
    }

    private fun setupFab() = (activity as MainActivity).setFab(binding.rcSearchGrid)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}