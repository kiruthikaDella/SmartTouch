package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.SwitchIconsDetailAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyUpdateSwitchIcon
import com.dellainfotech.smartTouch.api.model.IconListData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentSwitchIconsDetailBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConnectionStatus
import com.dellainfotech.smartTouch.mqtt.MQTTConstants
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
class SwitchIconsDetailFragment :
    ModelBaseFragment<HomeViewModel, FragmentSwitchIconsDetailBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: SwitchIconsDetailFragmentArgs by navArgs()
    private lateinit var adapter: SwitchIconsDetailAdapter
    private var switchIconList = arrayListOf<IconListData>()
    private var iconData: IconListData? = null
    private var mqttConnectionDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTitle.text = args.switchDetail.name

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
                viewModel.iconList()
            } else {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigate(SwitchIconsDetailFragmentDirections.actionGlobalHomeFragment())
                            }

                        }
                    )
                }
            }
        })

        switchIconList.clear()
        adapter = SwitchIconsDetailAdapter(switchIconList)
        context?.let {
            binding.recyclerSwitchIcons.layoutManager = GridLayoutManager(it, 4)
        }
        binding.recyclerSwitchIcons.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            iconData?.let {
                activity?.let { mActivity ->
                    DialogUtil.loadingAlert(mActivity)
                    viewModel.updateSwitchIcon(
                        BodyUpdateSwitchIcon(
                            args.deviceDetail.id,
                            args.switchDetail.id,
                            it.iconFile
                        )
                    )
                }
            } ?: kotlin.run {
                context?.let {
                    Toast.makeText(it, "Please select icon", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.iconListResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    switchIconList.clear()
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            switchIconList.addAll(it)
                            adapter.notifyDataSetChanged()
                            iconData = adapter.selectIcon(args.switchDetail)
                            adapter.setOnSwitchClickListener(object :
                                AdapterItemClickListener<IconListData> {
                                override fun onItemClick(data: IconListData) {
                                    iconData = data
                                }

                            })
                        }
                    }else {
                        adapter.notifyDataSetChanged()
                    }
                }
                is Resource.Failure -> {
                    switchIconList.clear()
                    adapter.notifyDataSetChanged()
                    DialogUtil.hideDialog()
                    Log.e(logTag, " iconListResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.updateSwitchIconResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        iconData?.let {
                            args.switchDetail.icon = it.icon
                            args.switchDetail.iconFile = it.iconFile
                            findNavController().navigateUp()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " updateSwitchIconResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSwitchIconsDetailBinding =
        FragmentSwitchIconsDetailBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    override fun onDestroyView() {
        super.onDestroyView()
        mqttConnectionDisposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateSwitchIconResponse.postValue(null)
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
                                            findNavController().navigate(
                                                SwitchIconsDetailFragmentDirections.actionSwitchIconsDetailFragmentToRoomPanelFragment(
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