package com.example.weathermaster.ui.citysearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentCitySearchBinding
import com.example.weathermaster.databinding.FragmentWeatherBinding
import com.example.weathermaster.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CitySearchFragment : Fragment() {
    private var _binding: FragmentCitySearchBinding? = null
    private val binding get() = requireNotNull(_binding)

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
    }

    private fun setOnSearchClickListener() {
        binding.search.setOnClickListener {
            Toast.makeText(context,"Click button",Toast.LENGTH_LONG).show()
            /*
            val textView = binding.appBarLayout.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                viewModel.keyWordForSearh = keyWord
                viewModel.isSearhResponse = true
                hideKeyboardFromView(textView.context, textView)
                textView.isCursorVisible = false
                getVideoList(keyWord)
            }

             */
        }

        binding.textSearch.setOnClickListener {
            Toast.makeText(context,"Click",Toast.LENGTH_LONG).show()
            binding.textSearch.isCursorVisible = true
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