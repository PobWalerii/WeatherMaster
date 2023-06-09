package com.example.weathermaster.ui.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentWeatherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCityName()
        observeCurrentTemp()
        setupSettingsClickListener()
    }

    private fun observeCityName() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myCity.collect {
                binding.city = if(it.isEmpty()) getString(R.string.my_city) else it
            }
        }
    }

    private fun observeCurrentTemp() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentTemp.collect {
                binding.currentTemp = it
                binding.simbolTemp = viewModel.tempSimbol.value
                binding.description = viewModel.description.value
                binding.icon = viewModel.icon.value
            }
        }
    }

    private fun setupSettingsClickListener() {
        binding.settings.setOnClickListener {
            startSettingsFragment()
        }
    }

    private fun startSettingsFragment() {
        findNavController().navigate(WeatherFragmentDirections.actionWeatherFragmentToSettingsFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}