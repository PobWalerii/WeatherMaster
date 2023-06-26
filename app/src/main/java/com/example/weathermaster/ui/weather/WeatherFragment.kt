package com.example.weathermaster.ui.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weathermaster.databinding.FragmentWeatherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<WeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //observeCityName()
        //observeCurrentWeather()
        observeCurrentData()
        observeForecastData()
        //observeCurrentForecast()
        setupSettingsClickListener()
        setupCityButtonClickListener()
    }

    private fun observeCurrentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCityAndWeather.collect {
                binding.city = it[0]
                viewModel.currentId = it[0].id
            }
        }
    }

    private fun observeForecastData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCityForecastDay.collect { list ->
                //withContext(Dispatchers.Main){
                //    Toast.makeText(context,"${list.size}",Toast.LENGTH_LONG).show()
                //}

                val forecast = list.filter { it.idCity == viewModel.currentId }
                if(forecast.isNotEmpty()) {
                    binding.forecast = forecast[0]
                    binding.line1.forecast = forecast[1]
                    binding.line2.forecast = forecast[2]
                    binding.line3.forecast = forecast[3]
                }
            }
        }
    }



/*
    private fun observeCityName() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myCity.collect {
                binding.city = if(it.isEmpty()) getString(R.string.my_city) else it
            }
        }
    }

    private fun observeCurrentWeather() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWeather.collect {
                if(it != null) {
                    binding.currentWeather = it
                    binding.simbolTemp = viewModel.tempSimbol.value
                }
            }
        }
    }

 */






/*
    private fun observeCurrentForecast() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentForecast.collect {
                if(it != null && it.size != 0) {
                    binding.forecast = it[0]
                    binding.line1.forecast = it[1]
                    binding.line2.forecast = it[2]
                    binding.line3.forecast = it[3]
                }
            }
        }
    }

 */

    private fun setupSettingsClickListener() {
        binding.settings.setOnClickListener {
            startSettingsFragment()
        }
    }

    private fun startSettingsFragment() {
        findNavController().navigate(WeatherFragmentDirections.actionWeatherFragmentToSettingsFragment())
    }

    private fun setupCityButtonClickListener() {
        binding.buttonCity.setOnClickListener {
            startCityListFragment()
        }
    }

    private fun startCityListFragment() {
        findNavController().navigate(WeatherFragmentDirections.actionWeatherFragmentToCityListFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}