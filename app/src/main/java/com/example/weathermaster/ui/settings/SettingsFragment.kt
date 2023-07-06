package com.example.weathermaster.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weathermaster.databinding.FragmentSettingsBinding
import com.example.weathermaster.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackClickListener()
        setupSaveClickListener()
        observeSettingsLoaded()
        setListenersSettingsChanged()
    }

    private fun observeSettingsLoaded() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoadedPreferences.collect {
                if (it) {
                    showSettings()
                }
            }
        }
    }

    private fun setupBackClickListener() {
        binding.arrow.setOnClickListener {
            (activity as MainActivity).onSupportNavigateUp()
        }
    }

    private fun setupSaveClickListener() {
        binding.save.setOnClickListener {
            saveSettings()
        }
    }

    private fun showSettings() {
        binding.measurement = viewModel.measurement.value
    }

    private fun setListenersSettingsChanged() {
        val switches = listOf(binding.switch1, binding.switch2, binding.switch3)
        switches.forEachIndexed { index, switch ->
            switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switches.forEachIndexed { innerIndex, innerSwitch ->
                        if (innerIndex != index) {
                            innerSwitch.isChecked = false
                        }
                    }
                    binding.measurement = index + 1
                    definitionOfChange()
                }
            }
        }
    }

    private fun definitionOfChange() {
        binding.isEdited = getIsChange()
    }

    private fun getIsChange(): Boolean {
        return viewModel.isChange(
            getMeasurementValue()
        )
    }

    private fun getMeasurementValue(): Int =
        if(binding.switch1.isChecked) 1
        else if(binding.switch2.isChecked) 2
        else 3

    private fun saveSettings() {
        viewModel.savePreferences(
            getMeasurementValue()
        )
        definitionOfChange()
    }

    override fun onResume() {
        super.onResume()
        definitionOfChange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}