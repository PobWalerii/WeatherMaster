package com.example.weathermaster.ui.citysearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.data.database.entity.SearchListItem
import com.example.weathermaster.databinding.FragmentCitySearchBinding
import com.example.weathermaster.ui.main.MainActivity
import com.example.weathermaster.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CitySearchFragment : Fragment() {
    private var _binding: FragmentCitySearchBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<SearchViewModel>()

    private lateinit var adapter: SearchListAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecycler()
        setupBackClickListener()
        setOnSearchClickListener()
        observeSearchState()
        setupAddClickListener()
        observeAddCityResult()
    }

    private fun observeSearchState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collect { state ->
                when (state) {
                    is SearchCityState.Loading -> {
                        showLoading(true)
                    }
                    is SearchCityState.Success -> {
                        showSearchList(state.searchList)
                    }
                    is SearchCityState.Empty -> {
                        showEmptyResult()
                    }
                    is SearchCityState.Error -> {
                        showError(state.errorMessage)
                    }
                    is SearchCityState.Loaded -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.isLoadData = isLoading
    }
    private fun showSearchList(searchList: List<SearchListItem>) {
        adapter.setList(searchList)
    }
    private fun showEmptyResult() {
        adapter.setList(emptyList())
        Toast.makeText(context, R.string.empty_search, Toast.LENGTH_SHORT).show()
    }
    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun setupAdapter() {
        adapter = SearchListAdapter()
    }

    private fun setupRecycler() {
        recycler = binding.recycler
        recycler.adapter = adapter
    }

    private fun setupAddClickListener() {
        adapter.setOnItemClickListener(object : SearchListAdapter.OnItemClickListener {
            override fun onItemClick(currentItem: SearchListItem) {
                addCityToList(currentItem)
            }
        })
    }

    private fun addCityToList(current: SearchListItem) {
        viewModel.addCity(current)
    }

    private fun setOnSearchClickListener() {

        binding.search.setOnClickListener {
            val textView = binding.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                hideKeyboardFromView(textView.context, textView)
                textView.isCursorVisible = false
                viewModel.getSearchList(keyWord)
            }
        }

        binding.textSearch.setOnClickListener {
            binding.textSearch.isCursorVisible = true
        }

        binding.textSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textView.isCursorVisible = false
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun observeAddCityResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addCityResult.collect {
                if (it) {
                    (activity as MainActivity).onSupportNavigateUp()
                }
            }
        }
    }

    private fun setupBackClickListener() {
        binding.arrow.setOnClickListener {
            (activity as MainActivity).onSupportNavigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}