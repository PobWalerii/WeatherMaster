package com.example.weathermaster.ui.citylist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentCityListBinding
import com.example.weathermaster.databinding.FragmentWeatherBinding
import com.example.weathermaster.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityListFragment : Fragment() {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = requireNotNull(_binding)

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
        setupBackClickListener()
        setupKeyPlusClickListener()
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