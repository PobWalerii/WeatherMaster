package com.example.weathermaster.ui.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.databinding.FragmentWeatherBinding
import com.example.weathermaster.notification.NotificationManager
import com.example.weathermaster.ui.citylist.CityListAdapter
import com.example.weathermaster.utils.KeyConstants
import com.example.weathermaster.utils.LoadImage.loadImageFromUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var adapter: CityStringAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecycler()
        observeCurrentData()
        observeForecastData()
        setupSettingsClickListener()
        setupCityButtonClickListener()
    }

    private fun observeCurrentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCityAndWeather.distinctUntilChanged().collect { list ->
                val current = list[0]
                binding.city = current
                viewModel.currentId = current.id

                adapter.setList(list)


            }
        }
    }

    private fun setupAdapter() {
        adapter = CityStringAdapter()
        adapter.setHasStableIds(true)
    }

    private fun setupRecycler() {
        recycler = binding.recycler
        recycler.adapter = adapter
    }


    private fun observeForecastData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCityForecastDay.distinctUntilChanged().collect { list ->
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