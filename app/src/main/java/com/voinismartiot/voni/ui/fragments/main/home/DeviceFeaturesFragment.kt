package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
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
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toEditable
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.databinding.FragmentDeviceFeaturesBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConnectionStatus
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class DeviceFeaturesFragment :
    BaseFragment<HomeViewModel, FragmentDeviceFeaturesBinding, HomeRepository>() {

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
                    when (it) {
                        MQTTConnectionStatus.CONNECTED -> {
                            subscribeToDevice(args.deviceDetail.deviceSerialNo)
                        }
                        else -> Unit
                    }
                }

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                activity?.loadingDialog()
                viewModel.getDeviceFeatures(args.deviceDetail.id)
            } else {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            hideDialog()
                            findNavController().navigate(DeviceCustomizationFragmentDirections.actionGlobalHomeFragment())
                        }

                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getDeviceFeatureSettingsResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            response.values.data?.let { deviceFeatureData ->

                                try {
                                    binding.switchSleepMode.isChecked =
                                        deviceFeatureData.sleepMode.toBoolean()
                                    binding.switchNightMode.isChecked =
                                        deviceFeatureData.nightMode.toBoolean()
                                    binding.switchTime.isChecked =
                                        deviceFeatureData.time.toBoolean()
                                    binding.switchDate.isChecked =
                                        deviceFeatureData.date.toBoolean()
                                    binding.switchWeatherReport.isChecked =
                                        deviceFeatureData.weatherReport.toBoolean()
                                    binding.switchRoomTemperature.isChecked =
                                        deviceFeatureData.roomTemperature.toBoolean()
                                    binding.seekBarBrightness.progress =
                                        deviceFeatureData.displayBrightnessValue.toInt()
                                    binding.edtSleepTime.text =
                                        deviceFeatureData.sleepModeSecond?.toEditable()

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
                        hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(
                            logTag,
                            " getDeviceFeatureSettingsResponse Failure ${response.errorBody?.string()} "
                        )
                    }
                    else -> Unit
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

            if (binding.edtSleepTime.text.toString().isEmpty()){
                activity?.showToast(getString(R.string.error_sleep_mode_empty))
                return@setOnClickListener
            }

            if (!binding.edtSleepTime.text.toString().isDigitsOnly()) {
                activity?.showToast(getString(R.string.error_sleep_mode_digit_only))
                return@setOnClickListener
            }

            if (binding.edtSleepTime.text.toString().toInt() < Constants.SLEEP_MODE_LIMIT_MIN) {
                activity?.showToast(getString(R.string.error_sleep_mode_second))
                return@setOnClickListener
            }

            if (binding.edtSleepTime.text.toString().toInt() > Constants.SLEEP_MODE_LIMIT_MAX) {
                activity?.showToast(getString(R.string.error_sleep_mode_limit_reached))
                return@setOnClickListener
            }

            binding.btnSynchronize.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                withContext(Dispatchers.Main) {
                    delay(Constants.SYNC_DELAY)
                    binding.btnSynchronize.isEnabled = true
                }
            }

            if (AwsMqttSingleton.isConnected()) {
                try {

                    var sleepModeSeconds = ""
                    if (binding.edtSleepTime.text.toString().isNotEmpty()) {
                        sleepModeSeconds = binding.edtSleepTime.text.toString()
                    }
                    val payload = JSONObject()
                    payload.put(
                        MQTTConstants.AWS_SLEEP_MODE,
                        binding.switchSleepMode.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_SLEEP_MODE_SECOND,
                        sleepModeSeconds
                    )
                    payload.put(
                        MQTTConstants.AWS_NIGHT_MODE,
                        binding.switchNightMode.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_TIME_DISPLAY,
                        binding.switchTime.isChecked.toInt()
                    )
                    payload.put(
                        MQTTConstants.AWS_DATE_DISPLAY,
                        binding.switchDate.isChecked.toInt()
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
            activity?.featureDetailAlert(
                getString(R.string.text_sleep_mode),
                getString(R.string.description_sleep_mode)
            )
        }
        binding.ivNightModeInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_night_mode),
                getString(R.string.description_night_mode)
            )
        }
        binding.ivTimeInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_time),
                getString(R.string.description_time_mode)
            )
        }
        binding.ivWeatherReportInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_weather_report),
                getString(R.string.description_weather_report)
            )
        }
        binding.ivRoomTemperatureInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_room_temperature),
                getString(R.string.description_room_temperature)
            )
        }
        binding.ivDisplayBrightnessInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_display_brightness),
                getString(R.string.description_display_brightness)
            )
        }
        binding.edtSleepTime.isEnabled = binding.switchSleepMode.isChecked
        binding.switchSleepMode.setOnCheckedChangeListener { p0, p1 ->
            binding.edtSleepTime.isEnabled = p1
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
                                hideDialog()
                            } else {
                                it.deviceOfflineAlert(
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
                            if (jsonObject.has(MQTTConstants.AWS_SLEEP_MODE_SECOND)) {
                                binding.edtSleepTime.text =
                                    jsonObject.getString(MQTTConstants.AWS_SLEEP_MODE_SECOND)
                                        .toEditable()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_NIGHT_MODE)) {
                                binding.switchNightMode.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_NIGHT_MODE).toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TIME_DISPLAY)) {
                                binding.switchTime.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_TIME_DISPLAY).toBoolean()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_DATE_DISPLAY)) {
                                binding.switchDate.isChecked =
                                    jsonObject.getInt(MQTTConstants.AWS_DATE_DISPLAY).toBoolean()
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