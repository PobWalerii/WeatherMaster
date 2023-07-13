package com.example.weathermaster.ui.weather

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.data.database.entity.ForecastWeatherDay
import com.example.weathermaster.data.database.entity.ForecastWeatherHour
import com.example.weathermaster.databinding.FragmentWeatherBinding
import com.example.weathermaster.utils.AnimSubstitution.startAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var adapter: CityStringAdapter
    private lateinit var recycler: RecyclerView

    private lateinit var adapterHour: HourForecastAdapter
    private lateinit var recyclerHour: RecyclerView

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
        loadData()
        setupChart()
        observeScreenState()
        setupSettingsClickListener()
        setupHomeClickListener()
        setupCityButtonClickListener()
        setItemClickListener()
        setupDetailClickListener()
        setupHourArrowClickListener()
    }

    private fun loadData() {
        viewModel.init()
    }
    private fun observeScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenState.collect { state ->
                when (state) {
                    is WeatherScreenState.Loading -> {
                    }
                    is WeatherScreenState.Loaded -> {
                        val cityAndWeatherList = state.currentCityList
                        val selectedCityAndWeather = state.selectedCityAndWeather
                        val selectedCityForecastDay = state.selectedCityForecastDay
                        val selectedCityForecastHour = state.selectedCityForecastHour
                        val isPermission = state.isPermission
                        updatePermissionState(isPermission)
                        updateCurrentData(selectedCityAndWeather)
                        updateCityListData(cityAndWeatherList)
                        updateForecastDay(selectedCityForecastDay)
                        updateForecastHour(selectedCityForecastHour)
                    }
                }
            }
        }
    }

    private fun updatePermissionState(isPermission: Boolean) {
        binding.isPermission = isPermission
    }

    private fun updateForecastHour(detail: List<ForecastWeatherHour>) {
        adapterHour.setList(detail)
        updateChartWithVisibleItems()
    }

    private fun updateCurrentData(cityWeather: CityAndWeatherFormated?) {
        if(cityWeather != null) {
            binding.city = cityWeather
        }
    }
    private fun updateCityListData(cityList: List<CityAndWeatherFormated>) {
        adapter.setList(cityList)
    }

    private fun updateForecastDay(forecast: List<ForecastWeatherDay>) {
            binding.forecast = forecast[0]
            binding.line1.forecast = forecast[1]
            binding.line2.forecast = forecast[2]
            binding.line3.forecast = forecast[3]
    }

    private fun setupDetailClickListener() {
        binding.detail.setOnClickListener {
            observeForecastHour()
            startAnim(binding.forecastDay, binding.forecastHour)
        }
    }

    private fun setupHourArrowClickListener() {
        binding.arrowHour.setOnClickListener {
            unsubscribeForecastHour()
            startAnim(binding.forecastHour, binding.forecastDay)
        }
    }

    private fun setItemClickListener() {
        adapter.setOnItemClickListener(object : CityStringAdapter.OnItemClickListener {
            override fun onItemClick(currentId: Long) {
                viewModel.setCurrentId(currentId)
            }
        })
    }

    private fun setupHomeClickListener() {
        binding.home.setOnClickListener {
            viewModel.setCurrentId(1L)
        }
    }

    private fun observeForecastHour() {
        recyclerHour.addOnScrollListener(scrollListener)
    }

    private fun unsubscribeForecastHour() {
        recyclerHour.removeOnScrollListener(scrollListener)
    }

    private fun updateChartWithVisibleItems() {

        val layoutManager = recyclerHour.layoutManager as LinearLayoutManager
        val start = layoutManager.findFirstVisibleItemPosition()
        val end = layoutManager.findLastVisibleItemPosition()
        val visibleItems = adapterHour.getItems(start, end)
        val entries = getChartData(visibleItems)
        refreshChart(entries)
    }

    private fun getChartData(visibleItems: List<ForecastWeatherHour>): List<Entry> {
        val temperatureData: MutableList<Float> = mutableListOf()
        visibleItems.map {
            temperatureData.add(it.temp)
        }
        val entries: List<Entry> = temperatureData.mapIndexed { index, temperature ->
            Entry(index.toFloat(), temperature)
        }
        return entries
    }

    private fun refreshChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Temperature")
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.lineWidth = 1f
        dataSet.color = Color.LTGRAY
        val lineData = LineData(dataSet)

        val chart = binding.chart
        chart.data = lineData
        chart.invalidate()
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