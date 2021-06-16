package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.DeviceAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.*
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogEditListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.clearError
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.databinding.FragmentDeviceBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout


/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class DeviceFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: DeviceFragmentArgs by navArgs()
    private var deviceList = arrayListOf<GetDeviceData>()
    private lateinit var panelAdapter: DeviceAdapter
    private var devicePosition: Int? = null
    private var switchPosition: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceList.clear()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTitle.text = args.roomDetail.roomName
        binding.switchRetainState.isChecked = args.roomDetail.retainState.toBoolean()

        binding.switchRetainState.setOnCheckedChangeListener { _, isChecked ->
            viewModel.retainState(BodyRetainState(args.roomDetail.id, isChecked.toInt()))
        }

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                deviceList.clear()
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

        activity?.let {
            panelAdapter = DeviceAdapter(it, deviceList)
            binding.recyclerRoomPanels.adapter = panelAdapter
        }

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
                    object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            if (string.isEmpty()) {
                                Toast.makeText(
                                    it,
                                    "Room name must not be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                DialogUtil.hideDialog()
                                DialogUtil.loadingAlert(it)
                                viewModel.updateRoom(BodyUpdateRoom(args.roomDetail.id, string))
                            }
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
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
                        object : DialogEditListener {
                            override fun onYesClicked(string: String) {
                                if (string.isEmpty()) {
                                    Toast.makeText(
                                        it,
                                        "Device name must not be empty!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
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
                        object : DialogEditListener {
                            override fun onYesClicked(string: String) {
                                if (string.isEmpty()) {
                                    Toast.makeText(
                                        it,
                                        "Switch name must not be empty!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    DialogUtil.hideDialog()
                                    this@DeviceFragment.devicePosition = devicePosition
                                    switchPosition = switchData.index.toInt() - 1
                                    DialogUtil.loadingAlert(it)
                                    viewModel.updateSwitchName(
                                        BodyUpdateSwitchName(
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
                        data
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

            if (deviceName.isEmpty()) {
                binding.layoutRoomPanel.edtPanelName.error =
                    getString(R.string.error_text_panel_name)
            } else if (serialNumber.isEmpty()) {
                binding.layoutRoomPanel.edtSerialNumber.error =
                    getString(R.string.error_text_serial_number)
            } else {
                hidePanel()
                Handler(Looper.getMainLooper()).postDelayed({
                    showLoading()
                    viewModel.addDevice(BodyAddDevice(serialNumber, args.roomDetail.id, deviceName))
                }, 600)
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

        viewModel.updateRoomResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            args.roomDetail.roomName = it.roomName
                            binding.tvTitle.text = it.roomName
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " updateRoomResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.addDeviceResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            deviceList.add(it)
                            panelAdapter.notifyDataSetChanged()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "addDeviceResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.getDeviceResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { deviceData ->
                            deviceList.addAll(deviceData)
                            panelAdapter.notifyDataSetChanged()
                        }
                    }
                }
                is Resource.Failure -> {
                    Log.e(logTag, "getDeviceResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.updateDeviceNameResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let { myContext ->
                        Toast.makeText(myContext, response.values.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        response.values.data?.let { deviceData ->
                            devicePosition?.let { pos ->
                                deviceList[pos] = deviceData
                                panelAdapter.notifyDataSetChanged()
                                devicePosition = null
                            }
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " updateDeviceNameResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.updateSwitchNameResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    response.values.message?.let { msg ->
                        context?.let { mContext ->
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        response.values.data?.let { dData ->
                            devicePosition?.let { dPosition ->
                                switchPosition?.let { sPosition ->
                                    deviceList[dPosition].switchData?.let {
                                        it[sPosition] = dData
                                        panelAdapter.notifyDataSetChanged()
                                        devicePosition = null
                                        switchPosition = null
                                    }

                                }
                            }
                        }

                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " updateSwitchNameResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

    }

}