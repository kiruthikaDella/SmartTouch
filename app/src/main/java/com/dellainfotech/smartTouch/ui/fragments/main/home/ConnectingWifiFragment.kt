package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binjal.wifilibrary.WifiUtils
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddDevice
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentConnectingWifiBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.activities.MainActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
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
    private var isInternetConnected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        Log.e(logTag, "onViewCreated activity $activity")

        args.isRegistering.let {
            isRegistering = it
        }

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            Log.e("$logTag Is Internet connected ", isConnected.toString())
            isInternetConnected = isConnected
        })

        TCPClientService.enableLog(true)

        TCPClientService.setReadWriteListener(this)

        TCPClientService.setConnectionListener(this)

        binding.ivBack.setOnClickListener {

            if (isRegistering) {
                findNavController().navigateUp()
            } else {
                if (TCPClientService.getSocket() != null) (activity as MainActivity).disconnectTCPClient()
            }
        }

        binding.layoutConfigWifiProcess.pulsator.startRippleAnimation()

        /*viewModel.deviceRegistrationResponse.observe(viewLifecycleOwner, { response ->
           when (response) {
               is Resource.Success -> {
                   DialogUtil.hideDialog()
                   findNavController().navigate(ConfigWifiFragmentDirections.actionConfigWifiFragmentToConnectingWifiFragment(true, args.roomDetail))
               }
               is Resource.Failure -> {
                   DialogUtil.hideDialog()
                   Log.e(logTag, "device registration error ${response.errorBody?.string()}")
                   if (response.isNetworkError) {
                       sendDataToCloud()
                   }
               } else -> {}
           }
       })*/

        viewModel.addDeviceResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e(logTag, "Response success")
                    context?.let {
                        Toast.makeText(it, "Response success", Toast.LENGTH_LONG).show()
                    }
                    findNavController().navigate(
                        ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToDeviceFragment(
                            args.roomDetail
                        )
                    )
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "addDeviceResponse Failure ${response.errorBody?.string()}")
                    if (response.isNetworkError) {
                        showOfflineAlert()
                    }
                }
                else -> {
                    //We will do nothing here
                }
            }
        })


        if (isRegistering) {
            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                getString(R.string.text_configuring)
        } else {
            runnable = Runnable {
                connectTCP()
            }
            handler?.postDelayed(runnable!!, 2000)
        }

    }

    private fun connectTCP() {
        try {
            TCPClientService.connectToAddress(
                WifiUtils.getGatewayIpAddress(),
                getString(R.string.receiver_port).toInt())
            triedToConnectTCP++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(message: String) {
        Log.e(logTag, "Connection successful $message")
        activity?.runOnUiThread {
            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                getString(R.string.text_connected)
            binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_wifi_done)
            binding.layoutConfigWifiProcess.centerImage.isClickable = false

            runnable = Runnable {
                findNavController().navigate(
                    ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToConfigWifiFragment(
                        args.roomDetail
                    )
                )
            }
            handler?.postDelayed(runnable!!, 3000)

        }
    }

    override fun onConnectFailure(message: String) {
        Log.e(logTag, "On Failure $triedToConnectTCP")
        activity?.runOnUiThread {
            if (triedToConnectTCP == 4) {
                triedToConnectTCP = 0

                context?.let {
                    binding.layoutConfigWifiProcess.tvConfigStatus.text =
                        getString(R.string.text_connection_failed)
                    binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_cancel)
                    binding.layoutConfigWifiProcess.centerImage.isClickable = false

                    runnable = Runnable {
                        findNavController().navigateUp()
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

        Log.e(logTag, "Server disconnect $activity")

        /*Handler(Looper.getMainLooper()).postDelayed({
            Log.e(logTag, "Server disconnect $activity")
        }, 2000)
        test()
*/

        activity?.runOnUiThread {

            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                activity?.getString(R.string.text_connection_failed)
            binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_cancel)
            binding.layoutConfigWifiProcess.centerImage.isClickable = false

            runnable = Runnable {
                findNavController().navigateUp()
            }
            handler?.postDelayed(runnable!!, 2000)
        }
    }

    /*  private fun connectTCP() {
        Log.e(logTag, "Server connectTCP ")
        try {
//            TCPClientService.connectToAddress(WifiUtils.getGatewayIpAddress(), getString(R.string.receiver_port).toInt(), connectCResultListener = this)
            TCPClientService.connectToAddress(
                WifiUtils.getGatewayIpAddress(),
                getString(R.string.receiver_port).toInt(),
                connectCResultListener = object :
                    ConnectCResultListener {
                    override fun onSuccess(message: String) {
                        Log.e(logTag, "Connection successful $message")
                        activity?.runOnUiThread {
                            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                                getString(R.string.text_connected)
                            binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_wifi_done)
                            binding.layoutConfigWifiProcess.centerImage.isClickable = false

                            runnable = Runnable {
                                findNavController().navigate(
                                    ConnectingWifiFragmentDirections.actionConnectingWifiFragmentToConfigWifiFragment(
                                        args.roomDetail
                                    )
                                )
                            }
                            handler?.postDelayed(runnable!!, 3000)

                        }
                    }

                    override fun onConnectFailure(message: String) {
                        Log.e(logTag, "On Failure $triedToConnectTCP")
                        activity?.runOnUiThread {
                            if (triedToConnectTCP == 4) {
                                triedToConnectTCP = 0

                                context?.let {
                                    binding.layoutConfigWifiProcess.tvConfigStatus.text =
                                        getString(R.string.text_connection_failed)
                                    binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_cancel)
                                    binding.layoutConfigWifiProcess.centerImage.isClickable = false

                                    runnable = Runnable {

                                        findNavController().navigateUp()
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
                        Log.e(logTag, "Server disconnect $activity")
                        activity?.runOnUiThread {

                            binding.layoutConfigWifiProcess.tvConfigStatus.text =
                                activity?.getString(R.string.text_connection_failed)
                            binding.layoutConfigWifiProcess.centerImage.setImageResource(R.drawable.ic_cancel)
                            binding.layoutConfigWifiProcess.centerImage.isClickable = false

                            runnable = Runnable {
                                findNavController().popBackStack()
                            }
                            handler?.postDelayed(runnable!!, 2000)
                        }
                    }
                })
            triedToConnectTCP++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/

    override fun onSuccess(message: String, value: String?) {
        Log.e(logTag, " onSuccess activity $activity")
        Log.e(logTag, message + value)
        var msg = ""
        value?.let {
            val jsonObject = JSONObject(value)
            if (jsonObject.has("data")) {
                msg = jsonObject.getString("data")
            }

            if (msg == "1234") {
                runnable = Runnable {
                    binding.layoutConfigWifiProcess.tvConfigStatus.text =
                        getString(R.string.text_registering)

                    if (TCPClientService.getSocket() != null) disconnectTCPClient()

                    sendDataToCloud()
                }
                handler?.postDelayed(runnable!!, 3000)
            }
        }
    }

    fun test(){
        Log.e(logTag, " test activity $activity")
    }

    override fun onFailure(message: String) {
        Log.e(logTag, "Read $message")
    }

    private fun sendDataToCloud() {
        runnable = Runnable {
            if (isInternetConnected) {
//                 viewModel.deviceRegister(jsonObject)

                viewModel.addDevice(
                    BodyAddDevice(
                        "SR0005",
                        args.roomDetail.id,
                        "Panel 5"
                    )
                )
            } else {
                showOfflineAlert()
            }
        }
        handler?.postDelayed(runnable!!, 2000)
    }

    private fun showOfflineAlert() {
        DialogUtil.deviceOfflineAlert(
            requireActivity(),
            "Please check your internet connection",
            object :
                DialogShowListener {
                override fun onClick() {
                    DialogUtil.hideDialog()
                    sendDataToCloud()
                }
            })
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


    override fun onDestroyView() {

        super.onDestroyView()

        handler?.let { handler ->
            runnable?.let {
                handler.removeCallbacks(it)
            }
        }
        viewModel.faqResponse.postValue(null)
        viewModel.addDeviceResponse.postValue(null)

        TCPClientService.setReadWriteListener(null)
        TCPClientService.setConnectionListener(null)

    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConnectingWifiBinding =
        FragmentConnectingWifiBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}