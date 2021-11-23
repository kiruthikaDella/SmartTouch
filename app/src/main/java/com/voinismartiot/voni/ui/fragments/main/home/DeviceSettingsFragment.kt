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
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyFactoryReset
import com.voinismartiot.voni.api.body.BodyRetainState
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.DialogUtil
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
import org.json.JSONObject

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class DeviceSettingsFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceSettingsBinding, HomeRepository>() {

    private val args: DeviceSettingsFragmentArgs by navArgs()
    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvRestart.setOnClickListener {
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

        binding.tvFactoryReset.setOnClickListener {
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
                                    args.deviceDetail.deviceType.toString()
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

        binding.tvRemove.setOnClickListener {
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

        binding.switchRetainState.isChecked = args.deviceDetail.retainState.toBoolean()

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

        /*  binding.tvUpdate.setOnClickListener {
              activity?.let {
                  DialogUtil.loadingAlert(
                      it,
                      getString(R.string.text_verify_update),
                      true
                  )
              }
          }*/

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

        apiCall()
    }

    private fun publishTopic(topicName: String, stringIndex: String) {
        val payload = JSONObject()
        payload.put(stringIndex, 1)

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

}