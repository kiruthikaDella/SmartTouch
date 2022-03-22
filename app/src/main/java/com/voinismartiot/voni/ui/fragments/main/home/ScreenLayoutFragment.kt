package com.voinismartiot.voni.ui.fragments.main.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.voinismartiot.voni.R
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.deviceOfflineAlert
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentScreenLayoutBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConnectionStatus
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.mqtt.NotifyManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ScreenLayoutFragment : DialogFragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentScreenLayoutBinding
    private var screenLayoutModel: ScreenLayoutModel? = null
    private val args: ScreenLayoutFragmentArgs by navArgs()
    private var mqttConnectionDisposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreenLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        context?.let {
            screenLayoutModel = ScreenLayoutModel(it, binding)
            screenLayoutModel?.init()
        }

        mqttConnectionDisposable =
            NotifyManager.getMQTTConnectionInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        MQTTConnectionStatus.CONNECTED -> {
                            subscribeToDevice(args.deviceDetail.deviceSerialNo)
                        }
                        else -> Unit
                    }
                }

        when (args.deviceCustomizationDetail.screenLayoutType) {
            screenLayoutModel?.screenLayoutEight -> {
                binding.tvFourIconsView.isVisible = false
                binding.tvEightIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutSix -> {
                binding.tvFourIconsView.isVisible = false
                binding.tvSixIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutFour -> {
                binding.tvEightIconsView.isVisible = false
                binding.tvSixIconsView.isVisible = false
                binding.tvFourIconsView.performClick()
            }
        }

        when (args.deviceCustomizationDetail.screenLayout) {
            ScreenLayoutModel.LEFT_MOST -> {
                binding.ivLeftMost.performClick()
            }
            ScreenLayoutModel.RIGHT_MOST -> {
                binding.ivRightMost.performClick()
            }
            ScreenLayoutModel.LEFT_RIGHT -> {
                binding.ivLeftRight.performClick()
            }
            ScreenLayoutModel.MIDDLE_CENTER -> {
                binding.ivMiddleCenter.performClick()
            }
            ScreenLayoutModel.TOP_CENTER -> {
                binding.ivTopCenter.performClick()
            }
            ScreenLayoutModel.BOTTOM_CENTER -> {
                binding.ivBottomCenter.performClick()
            }
        }

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            hideDialog()
                            findNavController().navigate(ScreenLayoutFragmentDirections.actionGlobalHomeFragment())
                        }

                    }
                )
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            args.deviceCustomizationDetail.screenLayoutType =
                screenLayoutModel?.storedViewType ?: ""
            args.deviceCustomizationDetail.screenLayout = screenLayoutModel?.screenLayout ?: ""
            context?.showToast("Settings Saved!")
            findNavController().navigateUp()
        }
    }

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
                                            lifecycleScope.launchWhenResumed {
                                                findNavController().navigate(
                                                    ScreenLayoutFragmentDirections.actionScreenLayoutFragmentToRoomPanelFragment(
                                                        args.roomDetail
                                                    )
                                                )
                                            }
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