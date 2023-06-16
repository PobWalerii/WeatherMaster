package com.example.weathermaster.ui.citylist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentCityListBinding
import com.example.weathermaster.databinding.FragmentWeatherBinding
import com.example.weathermaster.ui.citysearch.SearchListAdapter
import com.example.weathermaster.ui.citysearch.SearchViewModel
import com.example.weathermaster.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CityListFragment : Fragment() {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<CityViewModel>()

    private lateinit var adapter: CityListAdapter
    private lateinit var recycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecycler()
        loadCityList()
        setupBackClickListener()
        setupKeyPlusClickListener()
    }

    private fun setupAdapter() {
        adapter = CityListAdapter()
        adapter.setHasStableIds(true)
    }

    private fun setupRecycler() {
        recycler = binding.recycler
        recycler.adapter = adapter
    }

    private fun loadCityList() {
        viewLifecycleOwner.lifecycleScope.launch {
            val list = viewModel.loadCityList()
            adapter.setList(list)
        }

    }

    private fun setupKeyPlusClickListener() {
        binding.floatingActionButton.setOnClickListener {
            startCitySearchFragment()
        }
    }

    private fun startCitySearchFragment() {
        findNavController().navigate(CityListFragmentDirections.actionCityListFragmentToCitySearchFragment())
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