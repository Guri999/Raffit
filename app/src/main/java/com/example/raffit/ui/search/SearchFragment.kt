package com.example.raffit.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.raffit.ui.main.MainActivity
import com.example.raffit.data.search.ApiRepositoryImpl
import com.example.raffit.data.model.SearchModel
import com.example.raffit.data.bookmark.BookmarkRepositoryImpl
import com.example.raffit.databinding.FragmentSearchBinding
import com.example.raffit.ui.search.adpater.SearchAdapter
import com.example.raffit.ui.search.model.SearchViewModel
import com.example.raffit.ui.search.model.SearchViewModelFactory

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private var _adapter: SearchAdapter? = null

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(
            ApiRepositoryImpl(),
            BookmarkRepositoryImpl(requireContext())
        )
    }

    private val adapter get() = _adapter

    private var isLoading = false

    private lateinit var item: List<SearchModel>

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
        searchQuery()
        setupInfiniteScroll()
    }

    private fun setAdapter() {
        _adapter = SearchAdapter(this::onClickItem)
        binding.rcSearchGrid.adapter = adapter
        binding.rcSearchGrid.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.itemList.observe(viewLifecycleOwner){ models ->
            item = models.map { it.copy() }

            adapter?.submitList(item)
            isLoading = false
        }
    }

    private fun setupInfiniteScroll() {
        binding.rcSearchGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0 || isLoading) return

                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPositions(null).maxOrNull() ?: 0

                if (isLoading.not() && lastVisibleItemPosition > totalItemCount - 5) {
                    viewModel.scrollPage()
                    isLoading = true
                }
            }
        })
    }

    private fun searchQuery() {
        val word = arguments?.getString("searchQuery")
        if (word != null) {
            viewModel.searchItems(word,1)
            viewModel.page.observe(viewLifecycleOwner) {page->
                viewModel.searchItems(word, page)
            }
        }
    }

    private fun onClickItem(view: View, position: Int, item: SearchModel) {
        viewModel.addBookmark(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupFab()
    }//라이프사이클 옵저버: 디벨로퍼를 보자

    private fun setupFab() = (activity as MainActivity).setFab(binding.rcSearchGrid)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}