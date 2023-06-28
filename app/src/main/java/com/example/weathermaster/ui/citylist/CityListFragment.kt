package com.example.weathermaster.ui.citylist

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.databinding.FragmentCityListBinding
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
    private lateinit var itemTouchHelper: ItemTouchHelper

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
        observeCityList()
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
        setupTouchHelper()
    }

    private fun loadCityList() {
        viewModel.loadCityList()
    }

    private fun observeCityList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cityList.collect { cityList ->
                adapter.setList(cityList)
                checkScroll()
            }
        }
    }

    private fun checkScroll() {
        if (viewModel.addCityResult.value) {
            recycler.layoutManager?.scrollToPosition(adapter.itemCount - 1)
            viewModel.isScrolled()
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

    private fun setupTouchHelper() {

        itemTouchHelper = ItemTouchHelper(
            object : SimpleCallback(UP or DOWN, RIGHT or END) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder,
                ): Boolean {
                    adapter.onMove(
                        viewHolder.absoluteAdapterPosition,
                        target.absoluteAdapterPosition
                    )
                    updateCityList()
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    deleteCity(viewHolder.absoluteAdapterPosition)
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean,
                ) {
                    val itemView = viewHolder.itemView
                    val itemHeight = itemView.bottom - itemView.top
                    val icon =
                        ContextCompat.getDrawable(
                            recyclerView.context,
                            if (actionState == ACTION_STATE_SWIPE) R.drawable.delete else R.drawable.move
                        )!!

                    val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + icon.intrinsicHeight

                    val iconLeft = itemView.left + 10*iconMargin
                    val iconRight =
                        itemView.left + 10*iconMargin + icon.intrinsicWidth
                    icon.setBounds(
                        iconLeft,
                        iconTop,
                        iconRight,
                        iconBottom
                    )
                    icon.draw(c)

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })
        itemTouchHelper.attachToRecyclerView(recycler)
    }

    private fun deleteCity(position: Int) {
        val city = adapter.getCityFromPosition(position)
        adapter.onSwiped(position)
        viewModel.deleteCity(city)
    }

    private fun updateCityList() {
        val list = adapter.getCityList()
        viewModel.updateCityList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}