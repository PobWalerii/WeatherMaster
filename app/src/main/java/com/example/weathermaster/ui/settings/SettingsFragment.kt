package com.example.weathermaster.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weathermaster.databinding.FragmentSettingsBinding
import com.example.weathermaster.ui.main.MainActivity
import com.example.weathermaster.utils.KeyConstants.MEASUREMENT1
import com.example.weathermaster.utils.KeyConstants.MEASUREMENT2
import com.example.weathermaster.utils.KeyConstants.MEASUREMENT3
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        Toast.makeText(context,"${viewModel.measurement.value}",Toast.LENGTH_LONG).show()
        binding.measurement = viewModel.measurement.value
        /*
        binding.firstRun = viewModel.firstRun.value
        binding.defaultHeader = viewModel.defaultHeader.value
        binding.specificationLine = viewModel.specificationLine.value
        binding.defaultAddIfClick = viewModel.defaultAddIfClick.value
        binding.deleteIfSwiped = viewModel.deleteIfSwiped.value
        binding.dateChanged = viewModel.dateChanged.value
        binding.showMessageInternetOk = viewModel.showMessageInternetOk.value
        binding.startDelayValue = viewModel.startDelayValue.value
        binding.queryDelayValue = viewModel.queryDelayValue.value
        binding.requestIntervalValue = viewModel.requestIntervalValue.value
        binding.operationDelayValue = viewModel.operationDelayValue.value
        binding.createBackgroundRecords = viewModel.createBackgroundRecords.value
        binding.intervalCreateRecords = viewModel.intervalCreateRecords.value
         */
    }



    private fun setListenersSettingsChanged() {
        binding.switch1.setOnClickListener {
            binding.measurement = MEASUREMENT1
            definitionOfChange()
        }
        binding.switch2.setOnClickListener {
            binding.measurement = MEASUREMENT2
            definitionOfChange()
        }
        binding.switch3.setOnClickListener {
            binding.measurement = MEASUREMENT3
            definitionOfChange()
        }

 /*

        val changeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(buttonView == binding.switch1 || buttonView == binding.switch2 || buttonView == binding.switch3) {
                binding.measurement = when(buttonView) {
                    binding.switch2 -> MEASUREMENT2
                    binding.switch3 -> MEASUREMENT3
                    else -> MEASUREMENT1
                }
            }
            //definitionOfChange()
        }
        binding.switch1.setOnCheckedChangeListener(changeListener)
        binding.switch2.setOnCheckedChangeListener(changeListener)
        binding.switch3.setOnCheckedChangeListener(changeListener)

  */
    }

    internal fun definitionOfChange() {
        binding.isEdited = getIsChange()
    }

    private fun getIsChange(): Boolean =
        viewModel.isChange(
            getMeasurementValue()

            /*
            binding.switch6.isChecked,
            binding.header.text.toString(),
            binding.switch1.isChecked,
            binding.switch2.isChecked,
            binding.switch3.isChecked,
            binding.switch4.isChecked,
            binding.switch5.isChecked,
            binding.startDelay.text.toString(),
            binding.queryDelay.text.toString(),
            binding.requestInterval.text.toString(),
            binding.operationDelay.text.toString(),
            binding.switch7.isChecked,
            binding.intervalCreate.text.toString()
             */
        )

    private fun getMeasurementValue(): String = when {
        binding.switch2.isChecked -> MEASUREMENT2
        binding.switch3.isChecked -> MEASUREMENT3
        else -> MEASUREMENT1
    }

    private fun saveSettings() {
        viewModel.savePreferences(
            getMeasurementValue()

            /*
            binding.switch6.isChecked,
            binding.header.text.toString(),
            binding.switch1.isChecked,
            binding.switch2.isChecked,
            binding.switch3.isChecked,
            binding.switch4.isChecked,
            binding.switch5.isChecked,
            startDelayValue = binding.startDelay.text.toString().toIntOrNull() ?: 0,
            queryDelayValue = binding.queryDelay.text.toString().toIntOrNull() ?: 0,
            requestIntervalValue = binding.requestInterval.text.toString().toIntOrNull()
                ?: 0,
            operationDelayValue = binding.operationDelay.text.toString().toIntOrNull()
                ?: 0,
            binding.switch7.isChecked,
            intervalCreateRecords = binding.intervalCreate.text.toString().toIntOrNull() ?:
             */
        )
        definitionOfChange()
    }

    override fun onStop() {
        super.onStop()
        //hideKeyboardFromView(requireContext(),requireView())
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