package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.net.wifi.WifiInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binjal.wifilibrary.WifiUtils
import com.binjal.wifilibrary.listener.WifiInfoResultListener
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.GetRoomData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.databinding.FragmentConnectingWifiBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.gson.JsonObject
import com.teksun.tcpudplibrary.IPConfigService
import com.teksun.tcpudplibrary.SettingsService
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import org.json.JSONObject
import java.lang.Exception

class ConnectingWifiFragment :
    ModelBaseFragment<HomeViewModel, FragmentConnectingWifiBinding, HomeRepository>(),
    ReadWriteValueListener<String> {
    private val logTag = ConnectingWifiFragment::class.java.simpleName
    private val args: ConnectingWifiFragmentArgs by navArgs()
    private var isRegistering = false
    private var isConnected = false

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        args.isRegistering.let {
            isRegistering = it
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutConfigWifiProcess.pulsator.startRippleAnimation()

        runnable = Runnable {
        if (isRegistering) {
            Log.e("Binjal", args.roomDetail.toString())
            binding.layoutConfigWifiProcess.tvConfigStatus.text = "Registering..."
            binding.layoutConfigWifiProcess.centerImage.isClickable = true


                findNavController().navigate(
                    ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToDeviceFragment(
                        args.roomDetail
                    )
                )
        } else {
            initializeTCP()
        }
        }
        handler?.postDelayed(runnable!!, 5000)

    }

    private fun initializeTCP() {
        try {
            TCPClientService.enableLog(true)

            TCPClientService.setReadWriteListener(this)

            TCPClientService.connectToAddress(requireContext(), WifiUtils.getGatewayIpAddress(), 8881, connectCResultListener = object :
                ConnectCResultListener {
                override fun onSuccess(message: String) {
                    Log.e(logTag, "Connection successful $message")
                    runnable = Runnable {
                        binding.layoutConfigWifiProcess.tvConfigStatus.text = "Connected"
                        binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_wifi_done)
                        binding.layoutConfigWifiProcess.centerImage.isClickable = true

                        binding.layoutConfigWifiProcess.centerImage.setOnClickListener {
                            findNavController().navigate(
                                ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToConfigWifiFragment(
                                    args.roomDetail
                                )
                            )
                        }
                    }
                    handler?.postDelayed(runnable!!, 5000)
                }

                override fun onFailure(message: String) {
                    Log.e(logTag, "Connect failed")
                    Toast.makeText(requireContext(), "Connection Failed", Toast.LENGTH_LONG).show()
                    runnable = Runnable {
                        binding.layoutConfigWifiProcess.tvConfigStatus.text = "Connection Failed"
                        findNavController().navigateUp()
                    }
                    handler?.postDelayed(runnable!!, 5000)

                }

                override fun onServerDisconnect(message: String) {
                    Log.e(logTag, message)
                    runnable = Runnable {
                        binding.layoutConfigWifiProcess.tvConfigStatus.text = "Connection Failed"
                        findNavController().navigateUp()
                    }
                    handler?.postDelayed(runnable!!, 5000)
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(message: String, value: String?) {
        Log.e(logTag, "On Success read $message $value")
    }

    override fun onFailure(message: String) {
        Log.e(logTag, "On Fail $message")
    }

    override fun onPause() {
        super.onPause()

        handler?.let { handler ->
            runnable?.let {
                handler.removeCallbacks(it)
            }
        }
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConnectingWifiBinding =
        FragmentConnectingWifiBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}