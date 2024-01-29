package com.example.raffit.ui.mybox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.raffit.data.model.SearchModel
import com.example.raffit.data.bookmark.BookmarkRepositoryImpl
import com.example.raffit.databinding.FragmentMyboxBinding
import com.example.raffit.ui.mybox.adapter.MyBoxAdapter
import com.example.raffit.ui.mybox.model.MyBoxViewItem
import com.example.raffit.ui.mybox.model.MyBoxViewModel
import com.example.raffit.ui.mybox.model.MyBoxViewModelFactory

class MyBoxFragment : Fragment() {

    private var _binding: FragmentMyboxBinding? = null

    private val binding get() = _binding!!

    private val adapter by lazy {
        MyBoxAdapter(this::onClickItem)
    }

    private val viewModel: MyBoxViewModel by viewModels {
        MyBoxViewModelFactory(
            BookmarkRepositoryImpl(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyboxBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        setAdapter()
        loadItems()
    }

    private fun setAdapter() = with(binding) {
        rcMyGrid.adapter = adapter
        rcMyGrid.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.itemList.observe(viewLifecycleOwner) { searchModels ->
            val item = listOf(MyBoxViewItem.Header(title = "MyDrive")) + searchModels.map { MyBoxViewItem.Contents(it.copy()) }
            adapter.submitList(item)
        }
    }

    private fun onClickItem(view: View, position: Int, item: SearchModel) {
        removeItems(item)
    }

    private fun loadItems() {
        viewModel.loadBookmark()
    }

    private fun removeItems(item: SearchModel) {
        viewModel.removeBookmark(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}