package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.DeviceAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.*
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.api.model.GetRoomData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogEditListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.clearError
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
import com.dellainfotech.smartTouch.common.utils.hideKeyboard
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentDeviceBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

@SuppressLint("ClickableViewAccessibility")
class DeviceFragment : ModelBaseFragment<HomeViewModel, FragmentDeviceBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: DeviceFragmentArgs by navArgs()
    private var deviceList = arrayListOf<GetDeviceData>()
    private lateinit var panelAdapter: DeviceAdapter
    private var devicePosition: Int? = null
    private var switchPosition: Int? = null
    private var deviceData: GetDeviceData? = null
    private var roomData: GetRoomData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceList.clear()
        activity?.let {
            panelAdapter = DeviceAdapter(it, deviceList)
            binding.recyclerRoomPanels.adapter = panelAdapter
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTitle.text = args.roomDetail.roomName

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                showLoading()
                viewModel.getDevice(args.roomDetail.id)
            } else {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigateUp()
                            }

                        }
                    )
                }
            }
        })

        clickEvents()
        apiCall()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeviceBinding = FragmentDeviceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun clickEvents() {

        binding.layoutSlidingUpPanel.setFadeOnClickListener {
            hidePanel()
        }

        binding.layoutSlidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {

            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    binding.layoutRoomPanel.edtPanelName.text = "".toEditable()
                    binding.layoutRoomPanel.edtSerialNumber.text = "".toEditable()
                    binding.layoutRoomPanel.edtPanelName.clearError()
                    binding.layoutRoomPanel.edtSerialNumber.clearError()
                    hideKeyboard()
                }
            }
        })

        binding.iBtnEditRoomName.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it,
                    "Edit Room name",
                    binding.tvTitle.text.toString().trim(),
                    getString(R.string.text_save),
                    getString(R.string.text_cancel),
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            if (string.isEmpty()) {
                                it.showToast("Room name must not be empty!")
                            } else {
                                roomData = args.roomDetail
                                roomData?.roomName = string
                                DialogUtil.hideDialog()
                                DialogUtil.loadingAlert(it)
                                viewModel.updateRoom(BodyUpdateRoom(args.roomDetail.id, string))
                            }
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                            hideKeyboard()
                        }

                    }
                )
            }
        }

        panelAdapter.setOnUpdateDeviceNameClickListener(object :
            DeviceAdapter.DeviceItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData, devicePosition: Int) {
                activity?.let {
                    DialogUtil.editDialog(
                        it,
                        "Edit Panel name",
                        data.deviceName ?: "",
                        getString(R.string.text_save),
                        getString(R.string.text_cancel),
                        onClick = object : DialogEditListener {
                            override fun onYesClicked(string: String) {
                                if (string.isEmpty()) {
                                    it.showToast("Device name must not be empty!")
                                } else {
                                    deviceData = data
                                    deviceData?.deviceName = string
                                    DialogUtil.hideDialog()
                                    this@DeviceFragment.devicePosition = devicePosition
                                    DialogUtil.loadingAlert(it)
                                    viewModel.updateDeviceName(
                                        BodyUpdateDeviceName(
                                            data.id,
                                            string
                                        )
                                    )
                                }
                            }

                            override fun onNoClicked() {
                                DialogUtil.hideDialog()
                            }

                        }
                    )
                }
            }

        })

        panelAdapter.setOnCustomizationClickListener(object :
            AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                findNavController().navigate(
                    DeviceFragmentDirections.actionRoomPanelFragmentToDeviceCustomizationFragment(
                        data, args.roomDetail
                    )
                )
            }
        })

        panelAdapter.setOnFeaturesClickListener(object : AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                findNavController().navigate(
                    DeviceFragmentDirections.actionRoomPanelFragmentToDeviceFeaturesFragment(
                        data, args.roomDetail
                    )
                )
            }
        })

        panelAdapter.setOnEditSwitchNameClickListener(object :
            DeviceAdapter.SwitchItemClickListener<GetDeviceData> {
            override fun onItemClick(
                data: GetDeviceData,
                devicePosition: Int,
                switchData: DeviceSwitchData
            ) {
                activity?.let {
                    DialogUtil.editDialog(
                        it,
                        "Edit Switch name",
                        switchData.name,
                        getString(R.string.text_save),
                        getString(R.string.text_cancel),
                        onClick = object : DialogEditListener {
                            override fun onYesClicked(string: String) {
                                if (string.isEmpty()) {
                                    it.showToast("Switch name must not be empty!")
                                } else {
                                    deviceData = data
                                    DialogUtil.hideDialog()
                                    this@DeviceFragment.devicePosition = devicePosition
                                    switchPosition = switchData.index.toInt() - 1
                                    deviceData?.switchData?.get(switchPosition!!)?.name = string
                                    DialogUtil.loadingAlert(it)
                                    viewModel.updateSwitchName(
                                        BodyUpdateSwitchName(
                                            data.id,
                                            switchData.id,
                                            string
                                        )
                                    )
                                }
                            }

                            override fun onNoClicked() {
                                DialogUtil.hideDialog()
                            }

                        }
                    )

                }
            }

        })

        panelAdapter.setOnSettingsClickListener(object : AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                findNavController().navigate(
                    DeviceFragmentDirections.actionRoomPanelFragmentToDeviceSettingsFragment(
                        data,
                        args.roomDetail
                    )
                )
            }
        })

        binding.layoutRoomPanel.ivHidePanel.setOnClickListener {
            hidePanel()
        }

        binding.btnAddPanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.layoutRoomPanel.btnAddPanel.setOnClickListener {
            val deviceName = binding.layoutRoomPanel.edtPanelName.text.toString().trim()
            val serialNumber = binding.layoutRoomPanel.edtSerialNumber.text.toString().trim()

            when {
                deviceName.isEmpty() -> {
                    binding.layoutRoomPanel.edtPanelName.error =
                        getString(R.string.error_text_panel_name)
                    binding.layoutRoomPanel.edtPanelName.requestFocus()
                }
                serialNumber.isEmpty() -> {
                    binding.layoutRoomPanel.edtSerialNumber.error =
                        getString(R.string.error_text_serial_number)
                    binding.layoutRoomPanel.edtSerialNumber.requestFocus()
                }
                else -> {
                    hidePanel()
                    Handler(Looper.getMainLooper()).postDelayed({
                        showLoading()
                        viewModel.addDevice(
                            BodyAddDevice(
                                serialNumber,
                                args.roomDetail.id,
                                deviceName
                            )
                        )
                    }, 600)
                }
            }
        }

    }

    private fun hidePanel() {
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    private fun showLoading() {
        activity?.let {
            DialogUtil.loadingAlert(it)
        }
    }

    private fun apiCall() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.updateRoomResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)

                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    roomData?.let {
                                        args.roomDetail.roomName = it.roomName
                                        binding.tvTitle.text = it.roomName
                                        roomData = null
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " updateRoomResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.addDeviceResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let {
                                        deviceList.add(it)
                                        panelAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    "addDeviceResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.getDeviceResponse.collectLatest { response ->
                        deviceList.clear()
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { deviceData ->
                                        deviceList.addAll(deviceData)
                                        panelAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    "getDeviceResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> {
                                // We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.updateDeviceNameResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                                    deviceData?.let { data ->
                                        devicePosition?.let { pos ->
                                            deviceList[pos] = data
                                            panelAdapter.notifyDataSetChanged()
                                            devicePosition = null
                                            deviceData = null
                                        }
                                    }

                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " updateDeviceNameResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.updateSwitchNameResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                response.values.message?.let { msg ->
                                    context?.showToast(msg)
                                }
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                                    devicePosition?.let { dPosition ->
                                        switchPosition?.let { sPosition ->
                                            deviceData?.let { dData ->
                                                dData.switchData?.get(sPosition)?.let { sData ->
                                                    deviceList[dPosition].switchData?.let {
                                                        it[sPosition] = sData
                                                        panelAdapter.notifyDataSetChanged()
                                                        panelAdapter.publish(
                                                            dData.deviceSerialNo,
                                                            "SW0${sPosition + 1}",
                                                            sData.switchStatus.toString(),
                                                            sData.name
                                                        )
                                                        devicePosition = null
                                                        switchPosition = null
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " updateSwitchNameResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

            }


        }


    }

}