package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.MQTTConstants
import com.dellainfotech.smartTouch.databinding.FragmentScreenLayoutBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConnectionStatus
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
class ScreenLayoutFragment :
    ModelBaseFragment<HomeViewModel, FragmentScreenLayoutBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName

    private var screenLayoutModel: ScreenLayoutModel? = null
    private val args: ScreenLayoutFragmentArgs by navArgs()
    private var mqttConnectionDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            screenLayoutModel = ScreenLayoutModel(it, binding)
            screenLayoutModel?.init()
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

        when (args.deviceCustomizationDetail.screenLayoutType) {
            screenLayoutModel?.screenLayoutEight -> {
                binding.linearFourIconsView.isVisible = false
                binding.linearEightIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutSix -> {
                binding.linearFourIconsView.isVisible = false
                binding.linearSixIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutFour -> {
                binding.linearEightIconsView.isVisible = false
                binding.linearSixIconsView.isVisible = false
                binding.linearFourIconsView.performClick()
            }
        }

        when (args.deviceCustomizationDetail.screenLayout) {
            screenLayoutModel?.LEFT_MOST -> {
                binding.ivLeftMost.performClick()
            }
            screenLayoutModel?.RIGHT_MOST -> {
                binding.ivRightMost.performClick()
            }
            screenLayoutModel?.LEFT_RIGHT -> {
                binding.ivLeftRight.performClick()
            }
            screenLayoutModel?.MIDDLE_CENTER -> {
                binding.ivMiddleCenter.performClick()
            }
            screenLayoutModel?.TOP_CENTER -> {
                binding.ivTopCenter.performClick()
            }
            screenLayoutModel?.BOTTOM_CENTER -> {
                binding.ivBottomCenter.performClick()
            }
        }


        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentScreenLayoutBinding = FragmentScreenLayoutBinding.inflate(inflater, container, false)

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
                                        findNavController().navigate(ScreenLayoutFragmentDirections.actionScreenLayoutFragmentToRoomPanelFragment(args.roomDetail))
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

    //
    //endregion
    //
}