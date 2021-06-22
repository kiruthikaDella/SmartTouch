package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentScreenLayoutBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConnectionStatus
import com.dellainfotech.smartTouch.mqtt.MQTTConstants
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */
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
        setStyle(STYLE_NO_TITLE,R.style.DialogTheme)
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
                    Log.e(logTag, " MQTTConnectionStatus = $it ")
                    when (it) {
                        MQTTConnectionStatus.CONNECTED -> {
                            Log.e(logTag, " MQTTConnectionStatus.CONNECTED ")
                            subscribeToDevice(args.deviceDetail.deviceSerialNo)
                        }
                        else -> {
                            //We will do nothing here
                        }
                    }
                }

        when (args.deviceCustomizationDetail.screenLayoutType) {
            screenLayoutModel?.screenLayoutEight -> {
                binding.linearFourIconsView.isVisible = false
                binding.linearEightIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutSix -> {
                binding.linearFourIconsView.isVisible = false
                binding.linearSixIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutFour -> {
                binding.linearEightIconsView.isVisible = false
                binding.linearSixIconsView.isVisible = false
                binding.linearFourIconsView.performClick()
            }
        }

        when (args.deviceCustomizationDetail.screenLayout) {
            screenLayoutModel?.LEFT_MOST -> {
                binding.ivLeftMost.performClick()
            }
            screenLayoutModel?.RIGHT_MOST -> {
                binding.ivRightMost.performClick()
            }
            screenLayoutModel?.LEFT_RIGHT -> {
                binding.ivLeftRight.performClick()
            }
            screenLayoutModel?.MIDDLE_CENTER -> {
                binding.ivMiddleCenter.performClick()
            }
            screenLayoutModel?.TOP_CENTER -> {
                binding.ivTopCenter.performClick()
            }
            screenLayoutModel?.BOTTOM_CENTER -> {
                binding.ivBottomCenter.performClick()
            }
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
                                findNavController().navigate(ScreenLayoutFragmentDirections.actionGlobalHomeFragment())
                            }

                        }
                    )
                }
            }
        })


        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            args.deviceCustomizationDetail.screenLayoutType =
                screenLayoutModel?.storedViewType ?: ""
            args.deviceCustomizationDetail.screenLayout = screenLayoutModel?.screenLayout ?: ""
            context?.let {
                Toast.makeText(it, "Settings Saved!", Toast.LENGTH_SHORT).show()
            }
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
                            val deviceStatus = jsonObject.getInt(MQTTConstants.AWS_STATUS)
                            if (deviceStatus == 1) {
                                DialogUtil.hideDialog()
                            } else {
                                DialogUtil.deviceOfflineAlert(
                                    it,
                                    onClick = object : DialogShowListener {
                                        override fun onClick() {
                                            findNavController().navigate(
                                                ScreenLayoutFragmentDirections.actionScreenLayoutFragmentToRoomPanelFragment(
                                                    args.roomDetail
                                                )
                                            )
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