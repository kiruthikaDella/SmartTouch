package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.MQTTConstants
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.databinding.FragmentDeviceFeaturesBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConnectionStatus
import com.dellainfotech.smartTouch.mqtt.NetworkConnectionLiveData
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivCreateSceneSettings.setOnClickListener {
            findNavController().navigate(DeviceFeaturesFragmentDirections.actionDeviceFeaturesFragmentToSceneFragment(args.deviceDetail,args.roomDetail))
        }

        mqttConnectionDisposable = NotifyManager.getMQTTConnectionInfo().observeOn(AndroidSchedulers.mainThread()).subscribe{
            Log.e(logTag, " MQTTConnectionStatus = $it ")
            when(it){
                MQTTConnectionStatus.CONNECTED -> {
                    Log.e(logTag, " MQTTConnectionStatus.CONNECTED ")
                    subscribeToDevice(args.deviceDetail.deviceSerialNo)
                }
            }
        }

        NetworkConnectionLiveData().observe(viewLifecycleOwner, { isConnected ->
            if (isConnected){
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getDeviceFeatures(args.deviceDetail.id)
            }else {
                Log.e(logTag, " internet is not available")
            }
        })

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

        binding.btnSynchronize.setOnClickListener {
            if (AwsMqttSingleton.isConnected()){
                try {
                    val payload = JSONObject()
                    payload.put(MQTTConstants.AWS_SM,binding.switchSleepMode.isChecked.toInt())
                    payload.put(MQTTConstants.AWS_NM,binding.switchNightMode.isChecked.toInt())
                    payload.put(MQTTConstants.AWS_OM,binding.switchOutdoorMode.isChecked.toInt())
                    payload.put(MQTTConstants.AWS_TI,binding.switchTime.isChecked.toInt())
                    payload.put(MQTTConstants.AWS_TF,binding.rgTimeFormat.indexOfChild(activity?.findViewById(binding.rgTimeFormat.checkedRadioButtonId)))
                    payload.put(MQTTConstants.AWS_WR,binding.switchWeatherReport.isChecked.toInt())
                    payload.put(MQTTConstants.AWS_RT,binding.switchRoomTemperature.isChecked.toInt())
                    payload.put(MQTTConstants.AWS_TU,binding.rgTemperatureUnit.indexOfChild(activity?.findViewById(binding.rgTemperatureUnit.checkedRadioButtonId)))
                    payload.put(MQTTConstants.AWS_BM,binding.rgDisplayBrightness.indexOfChild(activity?.findViewById(binding.rgDisplayBrightness.checkedRadioButtonId)))
                    payload.put(MQTTConstants.AWS_BV,binding.seekBarBrightness.progress)

                    publish(payload.toString())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }else{
                context?.let {
                    Toast.makeText(it,"Please try again later!",Toast.LENGTH_SHORT).show()
                }
            }
        }
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

                        if (jsonObject.has(MQTTConstants.AWS_ST)) {
                            val deviceStatus = jsonObject.getInt(MQTTConstants.AWS_ST)
                            if (deviceStatus == 1){
                                DialogUtil.hideDialog()
                            }else {
                                DialogUtil.deviceOfflineAlert(it, onClick = object : DialogShowListener {
                                    override fun onClick() {
                                        findNavController().navigateUp()
                                    }

                                })
                            }
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