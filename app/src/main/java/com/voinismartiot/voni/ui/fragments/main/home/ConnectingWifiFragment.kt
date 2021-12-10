package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binjal.wifilibrary.WifiUtils
import com.bumptech.glide.Glide
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyRegisterDevice
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.databinding.FragmentConnectingWifiBinding
import com.voinismartiot.voni.ui.activities.MainActivity
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONException
import org.json.JSONObject

class ConnectingWifiFragment :
    ModelBaseFragment<HomeViewModel, FragmentConnectingWifiBinding, HomeRepository>(),
    ReadWriteValueListener<String>, ConnectCResultListener {
    private val logTag = this::class.java.simpleName
    private val args: ConnectingWifiFragmentArgs by navArgs()
    private var isRegistering = true
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var triedToConnectTCP = 0
    private var isConnection: Boolean? = null
    private var isApiResponseSuccess: Boolean? = null
    private var getDeviceStr: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        args.isRegistering.let {
            isRegistering = it
        }

        TCPClientService.enableLog(true)

        TCPClientService.setReadWriteListener(this)

        TCPClientService.setConnectionListener(this)

        binding.ivBack.setOnClickListener {
            if (TCPClientService.getSocket() != null) {
                (activity as MainActivity).disconnectTCPClient()
            } else {
                context?.let {
                    findNavController().navigateUp()
                }
            }
        }

        binding.layoutConfigWifiProcess.pulsator.startRippleAnimation()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deviceRegistrationResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e(logTag, "Response ${response.values.message}")
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_LONG).show()
                        }

                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            isApiResponseSuccess = true
                            findNavController().navigate(
                                ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToDeviceFragment(
                                    args.roomDetail
                                )
                            )
                        } else {
                            isApiResponseSuccess = false
                            findNavController().navigateUp()
                        }

                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        Log.e(
                            logTag,
                            "deviceRegistrationResponse Failure ${response.errorBody?.string()}"
                        )
                        if (response.isNetworkError) {
                            showOfflineAlert()
                        }
                    }
                    else -> {
                        //We will do nothing here
                    }
                }
            }

        }

        if (isRegistering) {

            //@TODO Configure
            context?.let {
                binding.layoutConfigWifiProcess.tvConfigStatus.text =
                    getString(R.string.text_configuring)

                Glide.with(it)
                    .asGif()
                    .load(R.raw.ic_configure)
                    .placeholder(R.drawable.ic_wifi)
                    .into(binding.layoutConfigWifiProcess.centerImage)
            }

            runnable = Runnable {
                sendRequestForPanelAndDeviceInfo()
            }
            handler?.postDelayed(runnable!!, 3000)

        } else {
            //@TODO Connecting
            context?.let {
                Glide.with(it)
                    .asGif()
                    .load(R.raw.ic_connecting)
                    .placeholder(R.drawable.ic_wifi)
                    .into(binding.layoutConfigWifiProcess.centerImage)
            }

            runnable = Runnable {
                connectTCP()
            }
            handler?.postDelayed(runnable!!, 3000)
        }
    }

    //
    //region Connection
    //
    private fun connectTCP() {
        try {
            TCPClientService.connectToAddress(
                requireContext(),
                WifiUtils.getGatewayIpAddress(),
                getString(R.string.receiver_port).toInt(),
                1000 * 60
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(logTag, "Exception in connectTCP ${e.message}")
            retryTCPConnection()
        }
        triedToConnectTCP++
    }

    override fun onSuccess(message: String) {
        Log.e(logTag, "Connection successful $message")
        isConnection = true
        activity?.runOnUiThread {
            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                getString(R.string.text_connected)
            binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_wifi_done)

            runnable = Runnable {
                if (isConnection!!) {
                    findNavController().navigate(
                        ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToConfigWifiFragment(
                            args.roomDetail, args.isSmarTack
                        )
                    )
                }
            }
            handler?.postDelayed(runnable!!, 3000)

        }
    }

    override fun onConnectFailure(message: String) {
        Log.e(logTag, "On Failure $triedToConnectTCP")

        retryTCPConnection()
    }

    private fun retryTCPConnection() {
        activity?.runOnUiThread {
            if (triedToConnectTCP == 4) {
                triedToConnectTCP = 0
                isConnection = false

                context?.let {
                    binding.layoutConfigWifiProcess.tvConfigStatus.text =
                        getString(R.string.text_connection_failed)
                    binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_cancel)

                    runnable = Runnable {
                        if (!isConnection!!) findNavController().navigateUp()
                    }
                    handler?.postDelayed(runnable!!, 2000)
                }

            } else {
                runnable = Runnable {
                    connectTCP()
                }
                handler?.postDelayed(runnable!!, 5000)
            }
        }
    }

    override fun onServerDisconnect(message: String) {
        Log.e(logTag, "Disconnect")

        isConnection = false

        activity?.runOnUiThread {

            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                activity?.getString(R.string.text_connection_failed)
            binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_cancel)

            runnable = Runnable {
                if (!isConnection!!) findNavController().navigateUp()
            }
            handler?.postDelayed(runnable!!, 2000)
        }
    }

    //
    //endregion
    //

    private fun sendRequestForPanelAndDeviceInfo() {
        TCPClientService.sendDefaultValue(
            Constants.GET_DEVICE_INFO,
            object : ReadWriteValueListener<String> {
                override fun onSuccess(message: String, value: String?) {
                    Log.e(logTag, "sendRequestForPanelAndDeviceInfo $message")
                }

                override fun onFailure(message: String) {
                    Log.e(logTag, "sendRequestForPanelAndDeviceInfo $message")
                }
            })
    }

    override fun onSuccess(message: String, value: String?) {
        Log.e(logTag, message + value)
        try {
            value?.let {
                val jsonObject = JSONObject(value)
                if (jsonObject.has("data")) {
                    val secondJsonObject = JSONObject(jsonObject.getString("data"))
                    if (secondJsonObject.has(Constants.GET_DEVICE_INFO)) {
                        getDeviceStr = secondJsonObject.get(Constants.GET_DEVICE_INFO).toString()

                        Log.e(logTag, "Get Device info $getDeviceStr")

                        runnable = Runnable {

                            //@TODO Registering
                            context?.let {
                                binding.layoutConfigWifiProcess.tvConfigStatus.text =
                                    getString(R.string.text_registering)

                                Glide.with(it)
                                    .asGif()
                                    .load(R.raw.ic_register)
                                    .placeholder(R.drawable.ic_wifi_done)
                                    .into(binding.layoutConfigWifiProcess.centerImage)
                            }

                            if (TCPClientService.getSocket() != null) disconnectTCPClient()

                            sendDataToCloud()
                        }
                        handler?.postDelayed(runnable!!, 2000)
                    }
                }
            }
        } catch (e: JSONException) {
            Log.e(logTag, "JSONException $e")
        }
    }

    override fun onFailure(message: String) {
        Log.e(logTag, "Read failed $message")
    }

    private fun sendDataToCloud() {
        val productGroup = if (args.isSmarTack) {
            getString(R.string.text_smart_tack)
        } else {
            getString(R.string.text_smart_tap)
        }

        if (isInternetConnected()) {
            runnable = Runnable {
                try {
                    getDeviceStr?.let {
                        val jsonObject = JSONObject(it)
                        viewModel.deviceRegister(
                            BodyRegisterDevice(
                                deviceSerialNum = jsonObject.get("device_serial_number").toString(),
                                roomId = args.roomDetail.id,
                                deviceName = jsonObject.get("device_name").toString(),
                                wifiSSID = jsonObject.get("wifi_ssid").toString(),
                                password = jsonObject.get("password").toString(),
                                macImei = jsonObject.get("mac_imei").toString(),
                                productGroup = productGroup,
                                manufactureDate = jsonObject.get("vManufactureDate").toString(),
                                firmwareVersion = jsonObject.get("vFirmwareVersion").toString()
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            handler?.postDelayed(runnable!!, 1500)
        } else {
            showOfflineAlert()
        }
    }

    private fun showOfflineAlert() {
        activity?.let {
            DialogUtil.deviceOfflineAlert(it, "Please check your internet connection",
                object : DialogShowListener {
                    override fun onClick() {
                        DialogUtil.hideDialog()
                        sendDataToCloud()
                    }
                })
        }
    }

    private fun disconnectTCPClient() {
        if (TCPClientService.getSocket() != null) {
            TCPClientService.closeSocket(object : CloseSocketListener {
                override fun onSuccess(message: String) {
                    Log.e(logTag, message)
                }

                override fun onFailure(message: String) {
                    Log.e(logTag, message)
                }
            })
        }
    }

    // When app goes to background in handler and navigation can't navigate
    override fun onResume() {
        super.onResume()

        viewModel.checkInternetConnection(5000)

        isConnection?.let {
            if (it) {
                findNavController().navigate(
                    ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToConfigWifiFragment(
                        args.roomDetail, args.isSmarTack
                    )
                )
            } else {
                findNavController().navigateUp()
            }
        }
        isApiResponseSuccess?.let {
            if (it) {
                findNavController().navigate(
                    ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToDeviceFragment(
                        args.roomDetail
                    )
                )
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        handler?.let { handler ->
            runnable?.let {
                handler.removeCallbacks(it)
            }
        }

        TCPClientService.setReadWriteListener(null)
        TCPClientService.setConnectionListener(null)

        Log.d(logTag, "OnDestroy view called")
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConnectingWifiBinding =
        FragmentConnectingWifiBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)
}












