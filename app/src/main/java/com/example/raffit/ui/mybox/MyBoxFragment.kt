package com.example.raffit.ui.mybox

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.raffit.data.bookmark.BookmarkRepositoryImpl
import com.example.raffit.data.model.SearchModel
import com.example.raffit.databinding.FragmentMyboxBinding
import com.example.raffit.ui.main.MainActivity
import com.example.raffit.ui.mybox.adapter.MyBoxAdapter
import com.example.raffit.ui.mybox.model.MyBoxViewItem
import com.example.raffit.ui.mybox.model.MyBoxViewModel
import com.example.raffit.ui.mybox.model.MyBoxViewModelFactory
import com.example.raffit.utill.SaveImageToFile
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MyBoxFragment : Fragment() {

    private var _binding: FragmentMyboxBinding? = null

    private val binding get() = _binding!!

    private val adapter by lazy {
        MyBoxAdapter(
            this::onLongClickItem,
            this::onClickItem
        )
    }

    private val viewModel: MyBoxViewModel by viewModels {
        MyBoxViewModelFactory(
            BookmarkRepositoryImpl(requireContext().applicationContext),
            SaveImageToFile(requireActivity())
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setupFab()
            }
        }
    }

    private fun setAdapter() = with(binding) {
        rcMyGrid.adapter = adapter
        rcMyGrid.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.itemList.observe(viewLifecycleOwner) { searchModels ->
            val item =
                listOf(MyBoxViewItem.Header) + searchModels.map { MyBoxViewItem.Contents(it.copy()) }
            adapter.submitList(item)
        }
    }

    private fun setupFab() = (activity as MainActivity).setFab(binding.rcMyGrid)
    private fun onClickItem(item: SearchModel) {
        removeItems(item)
    }

    private fun onLongClickItem(item: SearchModel): Boolean {

        val menu = arrayOf(
            "열기",
            "URL 복사하기",
            "원본 이미지 보기",
            "이미지 다운로드"
        )
        AlertDialog.Builder(requireContext())
            .setItems(menu) { _, position ->
                setDialog(
                    item = item,
                    menu = menu[position]
                )
            }.show()
        return true
    }

    private fun setDialog(item: SearchModel, menu: String) {
        when (menu) {
            "열기" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(item.docUrl)
                startActivity(intent)
            }

            "URL 복사하기" -> {
                val clipboard =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("copy", item.docUrl)
                clipboard.setPrimaryClip(clip)

                Snackbar.make(binding.rcMyGrid, "클립보드에 복사 되었습니다.", Snackbar.LENGTH_SHORT).show()
            }

            "원본 이미지 보기" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(item.thumnail)
                startActivity(intent)
            }

            "이미지 다운로드" -> {
                viewModel.saveImage(item.thumnail)
            }
        }
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