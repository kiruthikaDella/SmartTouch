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
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.SwitchesAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyFactoryReset
import com.voinismartiot.voni.api.body.BodyRetainState
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.common.utils.DialogUtil.featureDetailAlert
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentDeviceSettingsBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class DeviceSettingsFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceSettingsBinding, HomeRepository>() {

    private val args: DeviceSettingsFragmentArgs by navArgs()
    private val logTag = this::class.java.simpleName
    private val switchList = arrayListOf<DeviceSwitchData>()
    private lateinit var switchAdapter: SwitchesAdapter
    private var isOutdoorSaved = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchRetainState.isChecked = args.deviceDetail.retainState.toBoolean()

        subscribeToDevice(args.deviceDetail.deviceSerialNo)

        args.deviceDetail.switchData?.let { switches ->
            for (switch in switches) {
                if (switch.typeOfSwitch != 1) {
                    switchList.add(
                        DeviceSwitchData(
                            switch.id,
                            switch.typeOfSwitch,
                            switch.index,
                            switch.name,
                            switch.icon,
                            switch.switchStatus,
                            switch.desc,
                            switch.iconFile,
                            args.deviceDetail.outdoorModeSwitch?.contains(switch.id) ?: false
                        )
                    )
                }
            }
            switchAdapter = SwitchesAdapter(switchList)
            binding.layoutSwitches.tvTitle.text = getString(R.string.text_select_switch)
            binding.layoutSwitches.rvDays.adapter = switchAdapter
        }


        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (!isConnected) {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigate(DeviceSettingsFragmentDirections.actionGlobalHomeFragment())
                            }

                        }
                    )
                }
            }
        })

        clickEvents()

        apiCall()
    }

    private fun clickEvents() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutRestart.setOnClickListener {
            activity?.let {
                DialogUtil.askAlert(
                    it,
                    getString(R.string.dialog_title_restart_device),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            DialogUtil.hideDialog()
                            publishTopic(
                                MQTTConstants.RESTART_DEVICE.replace(
                                    MQTTConstants.AWS_DEVICE_ID,
                                    args.deviceDetail.deviceSerialNo
                                ), MQTTConstants.AWS_RESTART_DEVICE
                            )
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
        }

        binding.layoutFactoryReset.setOnClickListener {
            activity?.let {
                DialogUtil.askAlert(
                    it,
                    getString(R.string.dialog_title_factory_reset),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            DialogUtil.loadingAlert(it)
                            viewModel.factoryReset(
                                BodyFactoryReset(
                                    args.deviceDetail.id,
                                    args.deviceDetail.deviceType.toString(),
                                    args.deviceDetail.productGroup
                                )
                            )
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
        }

        binding.layoutRemove.setOnClickListener {
            activity?.let {
                DialogUtil.askAlert(
                    it,
                    getString(R.string.dialog_title_remove_device),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            activity?.let { myActivity ->
                                DialogUtil.loadingAlert(myActivity)
                            }
                            viewModel.deleteDevice(
                                args.deviceDetail.productGroup,
                                args.roomDetail.id,
                                args.deviceDetail.id
                            )
                        }

                        override fun onNoClicked() {
                        }

                    }
                )
            }
        }

        binding.switchRetainState.setOnClickListener {
            activity?.let {
                DialogUtil.loadingAlert(it)
                viewModel.retainState(
                    BodyRetainState(
                        args.deviceDetail.id,
                        binding.switchRetainState.isChecked.toInt()
                    )
                )
            }
        }

        binding.switchOutdoorMode.setOnClickListener {

            if (binding.switchOutdoorMode.isChecked) {
                showPanel()
            } else {
                publishOutdoorMode(
                    MQTTConstants.OUTDOOR_MODE_SETTINGS.replace(
                        MQTTConstants.AWS_DEVICE_ID,
                        args.deviceDetail.deviceSerialNo
                    ),
                    binding.switchOutdoorMode.isChecked.toInt().toString(),
                    switchAdapter.getSelectedSwitchNames()
                )
            }
        }

        binding.ivRestartInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_restart),
                getString(R.string.description_restart)
            )
        }

        binding.ivFactoryResetInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_factory_reset),
                getString(R.string.description_factory_reset)
            )
        }

        binding.ivRemoveInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_remove),
                getString(R.string.description_remove)
            )
        }

        binding.ivRetainStateInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_retain_state),
                getString(R.string.description_retain_state)
            )
        }

        binding.ivOutdoorModeInfo.setOnClickListener {
            activity?.featureDetailAlert(
                getString(R.string.text_outdoor_mode),
                getString(R.string.description_outdoor_mode)
            )
        }

        binding.layoutSwitches.btnSave.setOnClickListener {
            publishOutdoorMode(
                MQTTConstants.OUTDOOR_MODE_SETTINGS.replace(
                    MQTTConstants.AWS_DEVICE_ID,
                    args.deviceDetail.deviceSerialNo
                ), binding.switchOutdoorMode.isChecked.toInt().toString(),
                switchAdapter.getSelectedSwitchNames()
            )
            isOutdoorSaved = true
            hidePanel()
        }

        binding.layoutSlidingUpPanel.addPanelSlideListener( object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {

            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED && !isOutdoorSaved){
                    binding.switchOutdoorMode.isChecked = false
                }
            }

        })
    }

    private fun showPanel() {
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun hidePanel() {
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    private fun publishTopic(topicName: String, stringIndex: String) {
        val payload = JSONObject()
        payload.put(stringIndex, "1")

        if (AwsMqttSingleton.isConnected()) {
            Log.e(logTag, " publish settings topic $topicName payload $payload")
            AwsMqttSingleton.publish(topicName, payload.toString())
        }
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeviceSettingsBinding =
        FragmentDeviceSettingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun apiCall() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.deleteDeviceResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    findNavController().navigateUp()
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.retainStateResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)
                                if (!response.values.status || response.values.code != Constants.API_SUCCESS_CODE) {
                                    binding.switchRetainState.isChecked =
                                        !binding.switchRetainState.isChecked

                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " retainStateResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.factoryResetResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    publishTopic(
                                        MQTTConstants.RESTORE_FACTORY_SETTINGS.replace(
                                            MQTTConstants.AWS_DEVICE_ID,
                                            args.deviceDetail.deviceSerialNo
                                        ), MQTTConstants.AWS_FACTORY_RESET
                                    )
                                    activity?.let {
                                        DialogUtil.deviceOfflineAlert(
                                            it,
                                            response.values.message,
                                            object : DialogShowListener {
                                                override fun onClick() {
                                                    DialogUtil.hideDialog()
                                                    findNavController().navigateUp()
                                                }
                                            })
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
                                    " factoryResetResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToDevice(deviceId: String) {
        try {
            AwsMqttSingleton.mqttManager?.subscribeToTopic(
                MQTTConstants.OUTDOOR_MODE_ACK.replace(
                    MQTTConstants.AWS_DEVICE_ID,
                    deviceId
                ),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                activity?.runOnUiThread {

                    val message = String(data, StandardCharsets.UTF_8)
                    Log.d("$logTag ReceivedData", "$topic $message")

                    try {
                        val jsonObject = JSONObject(message)
                        if (jsonObject.has(MQTTConstants.AWS_OUTDOOR_MODE)) {
                            binding.switchOutdoorMode.isChecked =
                                jsonObject.getString(MQTTConstants.AWS_OUTDOOR_MODE).toInt()
                                    .toBoolean()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun publishOutdoorMode(
        topicName: String,
        value: String,
        selectedSwitch: ArrayList<String>
    ) {
        isOutdoorSaved = false
        val payload = JSONObject()
        payload.put(MQTTConstants.AWS_OUTDOOR_MODE, value)
        payload.put(MQTTConstants.AWS_SWITCH, JSONArray(selectedSwitch))

        if (AwsMqttSingleton.isConnected()) {
            AwsMqttSingleton.publish(topicName, payload.toString())
        }
    }

}