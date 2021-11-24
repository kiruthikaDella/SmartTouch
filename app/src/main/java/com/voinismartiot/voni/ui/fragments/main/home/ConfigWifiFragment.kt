package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.databinding.FragmentConfigWifiBinding
import com.voinismartiot.voni.ui.activities.MainActivity
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import org.json.JSONObject


/**
 * Created by Jignesh Dangar on 21-07-2021.
 */

class ConfigWifiFragment :
    ModelBaseFragment<HomeViewModel, FragmentConfigWifiBinding, HomeRepository>() {
    private val logTag = ConfigWifiFragment::class.java.simpleName
    private val args: ConfigWifiFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            if (TCPClientService.getSocket() != null) {
                (activity as MainActivity).disconnectTCPClient()
            } else {
                context?.let {
                    findNavController().navigateUp()
                }
            }
        }

        binding.layoutConfigWifiPanel.btnSubmit.setOnClickListener {
            validateUserInformation()
        }
    }

    private fun validateUserInformation() {
        try {
            val panelName = binding.layoutConfigWifiPanel.edtPanelName.text.toString()
            val ssid = binding.layoutConfigWifiPanel.edtWifiSsid.text.toString()
            val password = binding.layoutConfigWifiPanel.edtWifiPassword.text.toString()

            when {
                panelName.isEmpty() -> {
                    binding.layoutConfigWifiPanel.edtPanelName.error = "Please Enter panel name"
                    binding.layoutConfigWifiPanel.edtPanelName.requestFocus()
                }
                ssid.isEmpty() -> {
                    binding.layoutConfigWifiPanel.edtWifiSsid.error = "Please enter SSID"
                    binding.layoutConfigWifiPanel.edtWifiSsid.requestFocus()
                }
                password.isEmpty() -> {
                    binding.layoutConfigWifiPanel.edtWifiPassword.error = "Please enter password"
                    binding.layoutConfigWifiPanel.edtWifiPassword.requestFocus()
                }
                password.length < 8 -> {
                    binding.layoutConfigWifiPanel.edtWifiPassword.error = "Password length must be 8 characters"
                    binding.layoutConfigWifiPanel.edtWifiPassword.requestFocus()
                }
                else -> {
                    val jObject =  JSONObject()
                    jObject.apply {
                        put("device_name", panelName)
                        put("wifi_ssid", ssid)
                        put("password", password)
                    }
                    val jsonObject = JSONObject()
                    jsonObject.apply {
                        put("deviceConfigure", jObject)
                    }
                    Log.e(logTag, " deviceConfigure $jsonObject ")
                    sendTCPData(jsonObject.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendTCPData(configData: String) {
            TCPClientService.sendDefaultValue(configData, object : ReadWriteValueListener<String> {
                override fun onSuccess(message: String, value: String?) {
                    Log.e(logTag, "$message $value")
                    findNavController().navigate(ConfigWifiFragmentDirections.actionConfigWifiFragmentToConnectingWifiFragment(true, args.roomDetail, args.isSmarTack))
                }

                override fun onFailure(message: String) {
                    Log.e(logTag, "Send data failed $message")

                    activity?.let {
                        DialogUtil.deviceOfflineAlert(it, "Device Disconnected", object: DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigateUp()
                            }
                        })
                    }
                }
            })
    }


    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConfigWifiBinding = FragmentConfigWifiBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}