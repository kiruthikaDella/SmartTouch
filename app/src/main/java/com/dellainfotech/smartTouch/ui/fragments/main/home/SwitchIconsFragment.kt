package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.SwitchIconsAdapter
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.mqtt.MQTTConstants
import com.dellainfotech.smartTouch.databinding.FragmentSwitchIconsBinding
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
 * Created by Jignesh Dangar on 27-04-2021.
 */
class SwitchIconsFragment :
    ModelBaseFragment<HomeViewModel, FragmentSwitchIconsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: SwitchIconsFragmentArgs by navArgs()
    private var switchList = arrayListOf<DeviceSwitchData>()
    private lateinit var adapter: SwitchIconsAdapter
    private var mqttConnectionDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
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

        switchList.clear()

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (!isConnected) {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigate(SwitchIconsFragmentDirections.actionGlobalHomeFragment())
                            }

                        }
                    )
                }
            }
        })

        args.deviceDetail.switchData?.let {

            for (switch in it) {
                if (switch.typeOfSwitch == 0) {
                    switchList.add(switch)
                }
            }

            adapter = SwitchIconsAdapter(switchList)
            binding.recyclerSwitchIcons.adapter = adapter
            adapter.setOnSwitchClickListener(object : AdapterItemClickListener<DeviceSwitchData> {
                override fun onItemClick(data: DeviceSwitchData) {
                    findNavController().navigate(
                        SwitchIconsFragmentDirections.actionSwitchIconsFragmentToSwitchIconsDetailFragment(
                            data,args.roomDetail,args.deviceDetail
                        )
                    )
                }
            })
        }


    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSwitchIconsBinding = FragmentSwitchIconsBinding.inflate(inflater, container, false)

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

                        if (jsonObject.has(MQTTConstants.AWS_STATUS)) {
                            val deviceStatus = jsonObject.getInt(MQTTConstants.AWS_STATUS)
                            if (deviceStatus == 1){
                                DialogUtil.hideDialog()
                            }else {
                                DialogUtil.deviceOfflineAlert(it, onClick = object : DialogShowListener {
                                    override fun onClick() {
                                        findNavController().navigate(SwitchIconsFragmentDirections.actionSwitchIconsFragmentToRoomPanelFragment(args.roomDetail))
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