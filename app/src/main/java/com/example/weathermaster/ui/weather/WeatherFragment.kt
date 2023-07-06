package com.example.weathermaster.ui.weather

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentWeatherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Job

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var adapter: CityStringAdapter
    private lateinit var recycler: RecyclerView

    private lateinit var adapterHour: HourForecastAdapter
    private lateinit var recyclerHour: RecyclerView

    private var forecastHourJob: Job? = null
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            updateChartWithVisibleItems()
        }
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
        setupAdapter()
        setupRecycler()
        setupChart()

        observeCityListData()
        observeCurrentData()
        observeForecastData()
        //observeForecastHour()

        setupSettingsClickListener()
        setupCityButtonClickListener()
        setItemClickListener()
        setupDetailClickListener()
        setupHourArrowClickListener()
    }

    private fun setupDetailClickListener() {
        binding.detail.setOnClickListener {
            observeForecastHour()
            val fadeOut = ObjectAnimator.ofFloat(binding.forecastDay, "alpha", 1f, 0f)
            fadeOut.duration = 300
            fadeOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.forecastDay.visibility = GONE
                    binding.forecastHour.visibility = VISIBLE
                    val fadeIn = ObjectAnimator.ofFloat(binding.forecastHour, "alpha", 0f, 1f)
                    fadeIn.duration = 300
                    fadeIn.start()
                }
            })
            fadeOut.start()
        }
    }
    private fun setupHourArrowClickListener() {
        binding.arrowHour.setOnClickListener {
            unsubscribeForecastHour()
            val fadeOut = ObjectAnimator.ofFloat(binding.forecastHour, "alpha", 1f, 0f)
            fadeOut.duration = 300
            fadeOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.forecastHour.visibility = GONE
                    binding.forecastDay.visibility = VISIBLE
                    val fadeIn = ObjectAnimator.ofFloat(binding.forecastDay, "alpha", 0f, 1f)
                    fadeIn.duration = 300
                    fadeIn.start()
                }
            })
            fadeOut.start()
        }
    }

    private fun setItemClickListener() {
        adapter.setOnItemClickListener(object : CityStringAdapter.OnItemClickListener {
            override fun onItemClick(currentId: Long) {
                viewModel.setCurrentId(currentId)
            }
        })
    }

    private fun observeCityListData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCityAndWeather.distinctUntilChanged().collect { list ->
                adapter.setList(list)
            }
        }
    }

    private fun observeForecastHour() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCityForecastHour.distinctUntilChanged().collect { list ->
                adapterHour.setList(list)
                updateChartWithVisibleItems()
            }
        }
        recyclerHour.addOnScrollListener(scrollListener)
        //recyclerHour.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        //        updateChartWithVisibleItems()
        //    }
        //})
    }

    private fun unsubscribeForecastHour() {
        forecastHourJob?.cancel()
        recyclerHour.removeOnScrollListener(scrollListener)
    }

    private fun updateChartWithVisibleItems() {

        val layoutManager = recyclerHour.layoutManager as LinearLayoutManager
        val start = layoutManager.findFirstVisibleItemPosition()
        val end = layoutManager.findLastVisibleItemPosition()

        if ( start >= 0 && adapterHour.itemCount != 0) {

            val visibleItems = adapterHour.getItems(start, end)

            if (visibleItems.isNotEmpty()) {

                val temperatureData: MutableList<Float> = mutableListOf()

                visibleItems.map {
                    temperatureData.add(it.temp)
                }

                val entries: List<Entry> = temperatureData.mapIndexed { index, temperature ->
                    Entry(index.toFloat(), temperature)
                }

                val dataSet = LineDataSet(entries, "Temperature")
                dataSet.setDrawValues(false)
                dataSet.setDrawCircles(false)
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet.lineWidth = 1f
                dataSet.color = Color.YELLOW
                val lineData = LineData(dataSet)

                val chart = binding.chart
                chart.data = lineData
                chart.invalidate()
            }
        }
    }

    private fun observeCurrentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCityAndWeather.distinctUntilChanged().collect {
                binding.city = it
            }
        }
    }

    private fun observeForecastData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCityForecastDay.distinctUntilChanged().collect { forecast ->
                if(forecast.isNotEmpty()) {
                    binding.forecast = forecast[0]
                    binding.line1.forecast = forecast[1]
                    binding.line2.forecast = forecast[2]
                    binding.line3.forecast = forecast[3]
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = CityStringAdapter()
        adapter.setHasStableIds(true)
        adapterHour = HourForecastAdapter()
    }

    private fun setupRecycler() {
        recycler = binding.recycler
        recycler.adapter = adapter
        recyclerHour = binding.recyclerHour
        recyclerHour.adapter = adapterHour
    }

    private fun setupChart() {
        val chart = binding.chart

        val xAxis = chart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)

        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(false)

        val rightAxis = chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)

        chart.legend.isEnabled = false

        val description = Description()
        description.isEnabled = false
        chart.description = description

        chart.setTouchEnabled(false)
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