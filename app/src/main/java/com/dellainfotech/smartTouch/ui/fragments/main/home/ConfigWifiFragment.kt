package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentConfigWifiBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.gson.JsonObject
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener

/**
 * Created by Jignesh Dangar on 21-07-2021.
 */

class ConfigWifiFragment :
    ModelBaseFragment<HomeViewModel, FragmentConfigWifiBinding, HomeRepository>() {
    private val logTag = ConfigWifiFragment::class.java.simpleName
    private val args: ConfigWifiFragmentArgs by navArgs()
    private var jsonObject = JsonObject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
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
                }
                ssid.isEmpty() -> {
                    binding.layoutConfigWifiPanel.edtWifiSsid.error = "Please enter SSID"
                }
                password.isEmpty() -> {
                    binding.layoutConfigWifiPanel.edtWifiPassword.error = "Please enter password"
                }
                password.length < 8 -> {
                    binding.layoutConfigWifiPanel.edtWifiPassword.error = "Password length must be 8 characters"
                }
                else -> {
                    jsonObject.apply {
                        addProperty("panel_name", panelName)
                        addProperty("ssid", ssid)
                        addProperty("password", password)
                    }

                    TCPClientService.sendDefaultValue(jsonObject.toString(), object : ReadWriteValueListener<String> {
                        override fun onSuccess(message: String, value: String?) {
                            Log.e(logTag, "$message $value")
                            findNavController().navigate(ConfigWifiFragmentDirections.actionConfigWifiFragmentToConnectingWifiFragment(true, args.roomDetail))
//                            findNavController().navigateUp()
                        }

                        override fun onFailure(message: String) {
                            Log.e(logTag, "Send data failed $message")

                            DialogUtil.deviceOfflineAlert(requireActivity(), "Device Disconnected", object: DialogShowListener {
                                override fun onClick() {
                                    DialogUtil.hideDialog()
                                    findNavController().navigateUp()
                                }
                            })
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConfigWifiBinding = FragmentConfigWifiBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}