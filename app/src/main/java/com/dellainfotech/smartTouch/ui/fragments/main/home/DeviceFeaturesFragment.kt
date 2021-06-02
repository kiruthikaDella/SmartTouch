package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.databinding.FragmentDeviceFeaturesBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class DeviceFeaturesFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceFeaturesBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: DeviceFeaturesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivCreateSceneSettings.setOnClickListener {
            findNavController().navigate(DeviceFeaturesFragmentDirections.actionDeviceFeaturesFragmentToSceneFragment())
        }

        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.getDeviceFeatures(args.deviceDetail.id)

        viewModel.getDeviceFeatureSettingsResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { deviceFeatureData ->
                            binding.switchSleepMode.isChecked = deviceFeatureData.sleepMode.toBoolean()
                            binding.switchNightMode.isChecked = deviceFeatureData.nightMode.toBoolean()
                            binding.switchOutdoorMode.isChecked = deviceFeatureData.outdoorMode.toBoolean()
                            binding.switchTime.isChecked = deviceFeatureData.time.toBoolean()
                            binding.switchWeatherReport.isChecked = deviceFeatureData.weatherReport.toBoolean()
                            binding.switchRoomTemperature.isChecked = deviceFeatureData.roomTemperature.toBoolean()
                            binding.seekBarBrightness.progress = deviceFeatureData.displayBrightnessValue.toInt()

                            binding.rgTimeFormat.check(binding.rgTimeFormat[deviceFeatureData.timeFormat].id)
                            binding.rgTemperatureUnit.check(binding.rgTemperatureUnit[deviceFeatureData.temperatureUnit].id)
                            binding.rgDisplayBrightness.check(binding.rgDisplayBrightness[deviceFeatureData.displayBrightnessMode.toInt()].id)
                        }
                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " getDeviceFeatureSettingsResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeviceFeaturesBinding =
        FragmentDeviceFeaturesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)
}