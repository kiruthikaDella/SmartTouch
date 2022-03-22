package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.SwitchIconsAdapter
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.deviceOfflineAlert
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.databinding.FragmentSwitchIconsBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConnectionStatus
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class SwitchIconsFragment :
    BaseFragment<HomeViewModel, FragmentSwitchIconsBinding, HomeRepository>() {

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

        mqttConnectionDisposable =
            NotifyManager.getMQTTConnectionInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.e(logTag, " MQTTConnectionStatus = $it ")
                    when (it) {
                        MQTTConnectionStatus.CONNECTED -> {
                            Log.e(logTag, " MQTTConnectionStatus.CONNECTED ")
                            subscribeToDevice(args.deviceDetail.deviceSerialNo)
                        }
                        else -> Unit
                    }
                }

        switchList.clear()

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            hideDialog()
                            findNavController().navigate(SwitchIconsFragmentDirections.actionGlobalHomeFragment())
                        }

                    }
                )
            }
        }

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
                            data, args.roomDetail, args.deviceDetail
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
                            val deviceStatus = jsonObject.getString(MQTTConstants.AWS_STATUS)
                            if (deviceStatus == "1") {
                                hideDialog()
                            } else {
                                it.deviceOfflineAlert(
                                    onClick = object : DialogShowListener {
                                        override fun onClick() {
                                            findNavController().navigate(
                                                SwitchIconsFragmentDirections.actionSwitchIconsFragmentToRoomPanelFragment(
                                                    args.roomDetail
                                                )
                                            )
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