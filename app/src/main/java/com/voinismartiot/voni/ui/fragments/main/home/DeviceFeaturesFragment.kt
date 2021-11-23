package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentDeviceFeaturesBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConnectionStatus
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class DeviceFeaturesFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceFeaturesBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: DeviceFeaturesFragmentArgs by navArgs()
    private var mqttConnectionDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgTimeFormat.check(
            binding.rgTimeFormat[0].id
        )
        binding.rgTemperatureUnit.check(
            binding.rgTemperatureUnit[0].id
        )
        binding.rgDisplayBrightness.check(
            binding.rgDisplayBrightness[0].id
        )

        binding.seekBarBrightness.isVisible = false

        mqttConnectionDisposable =
            NotifyManager.getMQTTConnectionInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.e(logTag, " MQTTConnectionStatus = $it ")
                    when (it) {
                        MQTTConnectionStatus.CONNECTED -> {
                            Log.e(logTag, " MQTTConnectionStatus.CONNECTED ")
                            subscribeToDevice(args.deviceDetail.deviceSerialNo)
                        }
                        else -> {
                            //We will do nothing here
                        }
                    }
                }

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getDeviceFeatures(args.deviceDetail.id)
            } else {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigate(DeviceCustomizationFragmentDirections.actionGlobalHomeFragment())
                            }

                        }
                    )
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getDeviceFeatureSettingsResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            response.values.data?.let { deviceFeatureData ->

                                try {
                                    binding.switchSleepMode.isChecked =
                                        deviceFeatureData.sleepMode.toBoolean()
                                    binding.switchNightMode.isChecked =
                                        deviceFeatureData.nightMode.toBoolean()
                                    binding.switchOutdoorMode.isChecked =
                                        deviceFeatureData.outdoorMode.toBoolean()
                                    binding.switchTime.isChecked =
                                        deviceFeatureData.time.toBoolean()
                                    binding.switchWeatherReport.isChecked =
                                        deviceFeatureData.weatherReport.toBoolean()
                                    binding.switchRoomTemperature.isChecked =
                                        deviceFeatureData.roomTemperature.toBoolean()
                                    binding.seekBarBrightness.progress =
                                        deviceFeatureData.displayBrightnessValue.toInt()

                                    binding.rgTimeFormat.check(binding.rgTimeFormat[deviceFeatureData.timeFormat].id)
                                    binding.rgTemperatureUnit.check(binding.rgTemperatureUnit[deviceFeatureData.temperatureUnit].id)
                                    binding.rgDisplayBrightness.check(binding.rgDisplayBrightness[deviceFeatureData.displayBrightnessMode.toInt()].id)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            context?.showToast(response.values.message)
                        }
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(
                            logTag,
                            " getDeviceFeatureSettingsResponse Failure ${response.errorBody?.string()} "
                        )
                    }
                    else -> {
                        // We will do nothing here
                    }
                }
            }
        }


        clickEvents()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeviceFeaturesBinding =
        FragmentDeviceFeaturesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    override fun onDestroyView() {
        super.onDestroyView()
        mqttConnectionDisposable?.dispose()
    }

    private fun clickEvents() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rgDisplayBrightness.setOnCheckedChangeListener { _, checkedId ->
            binding.seekBarBrightness.isVisible =
                binding.rgDisplayBrightness.indexOfChild(
                    binding.rgDisplayBrightness.findViewById(
                        checkedId
                    )
                ) != 0
        }

        binding.btnSynchronize.setOnClickListener {
            if (AwsMqttSingleton.isConnected()) {
                try {
                    val payload = JSONObject()
                    payload.put(
                        MQTTConstants.AWS_SLEEP_MODE,
                        binding.switchSleepMode.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_NIGHT_MODE,
                        binding.switchNightMode.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_OUTDOOR_MODE,
                        binding.switchOutdoorMode.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_TIME_DISPLAY,
                        binding.switchTime.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_TIME_FORMAT,
                        binding.rgTimeFormat.indexOfChild(activity?.findViewById(binding.rgTimeFormat.checkedRadioButtonId))
                    )
                    payload.put(
                        MQTTConstants.AWS_WEATHER_REPORT_DISPLAY,
                        binding.switchWeatherReport.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_ROOM_TEMPERATURE_DISPLAY,
                        binding.switchRoomTemperature.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_TEMPERATURE_UNIT,
                        binding.rgTemperatureUnit.indexOfChild(activity?.findViewById(binding.rgTemperatureUnit.checkedRadioButtonId))
                    )
                    payload.put(
                        MQTTConstants.AWS_BRIGHTNESS_MODE,
                        binding.rgDisplayBrightness.indexOfChild(activity?.findViewById(binding.rgDisplayBrightness.checkedRadioButtonId))
                    )
                    payload.put(
                        MQTTConstants.AWS_BRIGHTNESS_VALUE,
                        binding.seekBarBrightness.progress
                    )

                    publish(payload.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                context?.showToast(getString(R.string.error_something_went_wrong))
            }
        }

        binding.ivSleepModeInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_sleep_mode),
                    getString(R.string.description_sleep_mode)
                )
            }
        }
        binding.ivNightModeInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_night_mode),
                    getString(R.string.description_night_mode)
                )
            }
        }
        binding.ivOutdoorModeInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_outdoor_mode),
                    getString(R.string.description_outdoor_mode)
                )
            }
        }
        binding.ivTimeInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_time),
                    getString(R.string.description_time_mode)
                )
            }
        }
        binding.ivWeatherReportInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_weather_report),
                    getString(R.string.description_weather_report)
                )
            }
        }
        binding.ivRoomTemperatureInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_room_temperature),
                    getString(R.string.description_room_temperature)
                )
            }
        }
        binding.ivDisplayBrightnessInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_display_brightness),
                    getString(R.string.description_display_brightness)
                )
            }
        }
    }

    //
    //region MQTT
    //

    private fun subscribeToDevice(deviceId: String) {
        try {

            //Current Device Status Update - Online/Offline
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
                MQTTConstants.DEVICE_STATUS.replace(MQTTConstants.AWS_DEVICE_ID, deviceId),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                activity?.let {
                    it.runOnUiThread {

                        val message = String(data, StandardCharsets.UTF_8)
                        Log.d("$logTag ReceivedData", "$topic    $message")

                        val jsonObject = JSONObject(message)

                        if (jsonObject.has(MQTTConstants.AWS_STATUS)) {
                            val deviceStatus = jsonObject.getString(MQTTConstants.AWS_STATUS)
                            if (deviceStatus == "1") {
                                DialogUtil.hideDialog()
                            } else {
                                DialogUtil.deviceOfflineAlert(
                                    it,
                                    onClick = object : DialogShowListener {
                                        override fun onClick() {
                                            findNavController().navigateUp()
                                        }

                                    })
                            }
                        }
                    }
                }
            }

            //Response of Get Switch status
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
                MQTTConstants.DEVICE_FEATURE_ACK.replace(
                    MQTTConstants.AWS_DEVICE_ID,
                    deviceId
                ),
                AWSIotMqttQos.QOS0
            ) { topic, data ->

                activity?.let {
                    it.runOnUiThread {

                        val message = String(data, StandardCharsets.UTF_8)
                        Log.d("$logTag ReceivedData", "$topic $message")

                        try {

                            it.showToast(getString(R.string.toast_text_device_synchronized))

                            // val topic1 = topic.split("/")
                            // topic [0] = ''
                            // topic [1] = smarttouch
                            // topic [2] = deviceId
                            // topic [3] = features-settings-ack

                            val jsonObject = JSONObject(message)

                            if (jsonObject.has(MQTTConstants.AWS_SLEEP_MODE)) {
                                binding.switchSleepMode.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_SLEEP_MODE).toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_NIGHT_MODE)) {
                                binding.switchNightMode.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_NIGHT_MODE).toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_OUTDOOR_MODE)) {
                                binding.switchOutdoorMode.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_OUTDOOR_MODE).toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TIME_DISPLAY)) {
                                binding.switchTime.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_TIME_DISPLAY).toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TIME_FORMAT)) {
                                binding.rgTimeFormat.check(
                                    binding.rgTimeFormat[jsonObject.getInt(
                                        MQTTConstants.AWS_TIME_FORMAT
                                    )].id
                                )
                            }
                            if (jsonObject.has(MQTTConstants.AWS_WEATHER_REPORT_DISPLAY)) {
                                binding.switchWeatherReport.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_WEATHER_REPORT_DISPLAY)
                                        .toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_ROOM_TEMPERATURE_DISPLAY)) {
                                binding.switchRoomTemperature.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_ROOM_TEMPERATURE_DISPLAY)
                                        .toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TEMPERATURE_UNIT)) {
                                binding.rgTemperatureUnit.check(
                                    binding.rgTemperatureUnit[jsonObject.getInt(
                                        MQTTConstants.AWS_TEMPERATURE_UNIT
                                    )].id
                                )
                            }
                            if (jsonObject.has(MQTTConstants.AWS_BRIGHTNESS_MODE)) {
                                binding.rgDisplayBrightness.check(
                                    binding.rgDisplayBrightness[jsonObject.getInt(
                                        MQTTConstants.AWS_BRIGHTNESS_MODE
                                    )].id
                                )
                            }
                            if (jsonObject.has(MQTTConstants.AWS_BRIGHTNESS_VALUE)) {
                                binding.seekBarBrightness.progress =
                                    jsonObject.getInt(MQTTConstants.AWS_BRIGHTNESS_VALUE)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    private fun publish(payload: String) {

        Log.e(logTag, " payload $payload ")

        AwsMqttSingleton.publish(
            MQTTConstants.UPDATE_DEVICE_FEATURE.replace(
                MQTTConstants.AWS_DEVICE_ID,
                args.deviceDetail.deviceSerialNo
            ), payload
        )
    }

    //
    //endregion
    //
}