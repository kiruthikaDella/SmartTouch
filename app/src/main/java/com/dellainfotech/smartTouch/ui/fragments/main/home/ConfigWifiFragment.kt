package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binjal.wifilibrary.WifiUtils
import com.dellainfotech.smartTouch.api.repository.HomeRepository
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutConfigWifiPanel.edtWifiSsid.setText(WifiUtils.getCurrentSSID())

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
                else -> {
                    val jsonObject = JsonObject()
                    jsonObject.apply {
                        addProperty("panel_name", panelName)
                        addProperty("ssid", ssid)
                        addProperty("password", password)
                    }

                    TCPClientService.sendDefaultValue(jsonObject.toString(), object : ReadWriteValueListener<String> {
                        override fun onSuccess(message: String, value: String?) {
                            Log.e(logTag, "$message $value")
                            findNavController().navigate(ConfigWifiFragmentDirections.actionConfigWifiFragmentToConnectingWifiFragment(true, args.roomDetail))
                        }

                        override fun onFailure(message: String) {
                            Log.e(logTag, "Send data failed $message")
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