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
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentCitySearchBinding
import com.example.weathermaster.ui.main.MainActivity
import com.example.weathermaster.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher


@AndroidEntryPoint
class CitySearchFragment : Fragment() {
    private var _binding: FragmentCitySearchBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackClickListener()
        setOnSearchClickListener()
        observeIsLoadData()
        observeLoadData()
    }

    private fun observeLoadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.searchList, viewModel.waitingData) {
                    searchList,waitingData -> Pair(searchList,waitingData)
            }.collect {(searchList,waitingData) ->
                if( waitingData ) {
                    if (searchList.isEmpty()) {
                        Toast.makeText(context, R.string.empty_search, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "List - ${searchList.size}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeIsLoadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoadData.collect {
                binding.isLoadData = it
            }
        }
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