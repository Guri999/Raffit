package com.example.raffit.ui.main

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.raffit.R
import com.example.raffit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    private var _binding : FragmentHomeBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        loadQuery()
    }

    private fun setSearchView() = with(binding) {
        searchViewHome.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                transitionSearchView(searchViewHome.query.toString())
            }
        }
    }

    private fun transitionSearchView(query: String?) = with(binding) {
        val extras = FragmentNavigatorExtras(searchViewHome to "trans")

        if (query != null) {
            findNavController().navigate(
                resId = R.id.action_homeFragment_to_nav_search_img,
                args = Bundle().apply { putString("searchQuery", query) },
                navOptions = null,
                navigatorExtras = extras
            )
        } else {

        }
    }

    private fun loadQuery() = with(binding) {
        val pref = requireActivity().getSharedPreferences("lastQuery", Context.MODE_PRIVATE)
        searchViewHome.setQuery(pref.getString("lastQuery", null), false)
        searchViewHome.isIconified = false
        searchViewHome.clearFocus()
        setSearchView()
    }

}