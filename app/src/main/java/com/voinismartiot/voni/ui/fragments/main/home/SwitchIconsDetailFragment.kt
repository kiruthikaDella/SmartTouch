package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.SwitchIconsDetailAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyUpdateSwitchIcon
import com.voinismartiot.voni.api.model.IconListData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.databinding.FragmentSwitchIconsDetailBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConnectionStatus
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class SwitchIconsDetailFragment :
    BaseFragment<HomeViewModel, FragmentSwitchIconsDetailBinding, HomeRepository>() {

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
                        else -> Unit
                    }
                }

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                activity?.loadingDialog()
                viewModel.iconList()
            } else {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            hideDialog()
                            findNavController().navigate(SwitchIconsDetailFragmentDirections.actionGlobalHomeFragment())
                        }

                    }
                )
            }
        }

        switchIconList.clear()
        adapter = SwitchIconsDetailAdapter(switchIconList)
        context?.let {
            binding.recyclerSwitchIcons.layoutManager = GridLayoutManager(it, 4)
        }
        binding.recyclerSwitchIcons.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            iconData?.let {
                activity?.let { mActivity ->
                    mActivity.loadingDialog()
                    viewModel.updateSwitchIcon(
                        BodyUpdateSwitchIcon(
                            args.deviceDetail.id,
                            args.switchDetail.id,
                            it.iconFile
                        )
                    )
                }
            } ?: kotlin.run {
                context?.showToast("Please select icon")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.iconListResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                switchIconList.clear()
                                hideDialog()
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
                                } else {
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            is Resource.Failure -> {
                                switchIconList.clear()
                                adapter.notifyDataSetChanged()
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " iconListResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.updateSwitchIconResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    iconData?.let {
                                        args.switchDetail.icon = it.icon
                                        args.switchDetail.iconFile = it.iconFile
                                        findNavController().navigateUp()
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " updateSwitchIconResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }

        }


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