package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.ScenesAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyGetScene
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData
import com.dellainfotech.smartTouch.api.model.GetSceneData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.MQTTConstants
import com.dellainfotech.smartTouch.databinding.FragmentSceneBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConnectionStatus
import com.dellainfotech.smartTouch.mqtt.NetworkConnectionLiveData
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * Created by Jignesh Dangar on 02-06-2021.
 */

class SceneFragment : ModelBaseFragment<HomeViewModel, FragmentSceneBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: SceneFragmentArgs by navArgs()
    private var controlModeRoomData = arrayListOf<ControlModeRoomData>()
    private lateinit var sceneAdapter: ScenesAdapter
    private val sceneList = arrayListOf<GetSceneData>()
    private var mqttConnectionDisposable: Disposable? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        mqttConnectionDisposable = NotifyManager.getMQTTConnectionInfo().observeOn(AndroidSchedulers.mainThread()).subscribe{
            Log.e(logTag, " MQTTConnectionStatus = $it ")
            when(it){
                MQTTConnectionStatus.CONNECTED -> {
                    Log.e(logTag, " MQTTConnectionStatus.CONNECTED ")
                    subscribeToDevice(args.deviceDetail.deviceSerialNo)
                }
            }
        }

        binding.ibCreate.setOnClickListener {
            findNavController().navigate(
                SceneFragmentDirections.actionSceneFragmentToCreateSceneFragment(
                    args.deviceDetail,
                    args.roomDetail,
                    null,
                    controlModeRoomData.toTypedArray()
                )
            )
        }

        sceneAdapter = ScenesAdapter(sceneList)
        binding.recyclerScenes.adapter = sceneAdapter
        sceneAdapter.setOnClickListener(object :
            AdapterItemClickListener<GetSceneData> {
            override fun onItemClick(data: GetSceneData) {
                findNavController().navigate(
                    SceneFragmentDirections.actionSceneFragmentToCreateSceneFragment(
                        args.deviceDetail,
                        args.roomDetail,
                        data,
                        controlModeRoomData.toTypedArray()
                    )
                )
            }

        })

        sceneAdapter.setOnDeleteClickListener(object : AdapterItemClickListener<GetSceneData> {
            override fun onItemClick(data: GetSceneData) {
                activity?.let {
                    DialogUtil.askAlert(
                        it, getString(R.string.dialog_title_delete_scene),
                        getString(R.string.text_yes),
                        getString(R.string.text_no),
                        object : DialogAskListener {
                            override fun onYesClicked() {
                                DialogUtil.loadingAlert(it)
                                viewModel.deleteScene(data.id)
                            }

                            override fun onNoClicked() {
                            }

                        }
                    )
                }
            }

        })

        NetworkConnectionLiveData().observe(viewLifecycleOwner, { isConnected ->
            if (isConnected){
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getScene(BodyGetScene(args.roomDetail.id, args.deviceDetail.id))
                viewModel.getControl()
            }else {
                Log.e(logTag, " internet is not available")
            }
        })

        apiResponse()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSceneBinding = FragmentSceneBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    override fun onDestroyView() {
        super.onDestroyView()
        mqttConnectionDisposable?.dispose()
    }

    private fun apiResponse() {
        viewModel.getControlResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { roomDataList ->
                            controlModeRoomData.addAll(roomDataList)
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " getControlResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.getSceneResponse.observe(viewLifecycleOwner, { response ->
            sceneList.clear()
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            sceneList.addAll(it)
                        }
                    }
                    sceneAdapter.notifyDataSetChanged()
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " getSceneResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.deleteSceneResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        viewModel.getScene(BodyGetScene(args.roomDetail.id, args.deviceDetail.id))
                    } else {
                        DialogUtil.hideDialog()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " deleteSceneResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
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

                        if (jsonObject.has(MQTTConstants.AWS_ST)) {
                            val deviceStatus = jsonObject.getInt(MQTTConstants.AWS_ST)
                            if (deviceStatus == 1){
                                DialogUtil.hideDialog()
                            }else {
                                DialogUtil.deviceOfflineAlert(it, onClick = object : DialogShowListener {
                                    override fun onClick() {
                                        findNavController().navigate(SceneFragmentDirections.actionSceneFragmentToRoomPanelFragment(args.roomDetail))
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