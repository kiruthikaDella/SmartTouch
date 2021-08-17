package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyFactoryReset
import com.dellainfotech.smartTouch.api.body.BodyRetainState
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.databinding.FragmentDeviceSettingsBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConstants
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
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
                            publishTopic(MQTTConstants.RESTART_DEVICE.replace(MQTTConstants.AWS_DEVICE_ID, args.deviceDetail.deviceSerialNo), MQTTConstants.AWS_RESTART_DEVICE)
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
                            viewModel.factoryReset(BodyFactoryReset(args.deviceDetail.id,args.deviceDetail.deviceType.toString()))
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
                            viewModel.deleteDevice(args.deviceDetail.productGroup, args.roomDetail.id, args.deviceDetail.id)
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deleteDeviceResponse.postValue(null)
        viewModel.retainStateResponse.postValue(null)
        viewModel.factoryResetResponse.postValue(null)
    }

    private fun apiCall() {
        viewModel.deleteDeviceResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        findNavController().navigateUp()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.retainStateResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (!response.values.status || response.values.code != Constants.API_SUCCESS_CODE){
                        binding.switchRetainState.isChecked = !binding.switchRetainState.isChecked

                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " retainStateResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.factoryResetResponse.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE){
                        publishTopic(MQTTConstants.RESTORE_FACTORY_SETTINGS.replace(MQTTConstants.AWS_DEVICE_ID, args.deviceDetail.deviceSerialNo), MQTTConstants.AWS_FACTORY_RESET)
                        activity?.let {
                            DialogUtil.deviceOfflineAlert(it, response.values.message, object : DialogShowListener {
                                override fun onClick() {
                                    DialogUtil.hideDialog()
                                    findNavController().navigateUp()
                                }
                            })
                        }
                    }else {
                        context?.let {
                            Toast.makeText(it,response.values.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " factoryResetResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

}