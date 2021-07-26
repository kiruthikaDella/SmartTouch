package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binjal.wifilibrary.VersionUtils
import com.binjal.wifilibrary.WifiUtils
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.DeviceAdapter
import com.dellainfotech.smartTouch.adapters.spinneradapter.SpinnerAdapter
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
import com.dellainfotech.smartTouch.databinding.FragmentDeviceBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout

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
    private lateinit var deviceTypeAdapter: SpinnerAdapter
    private val WIFI_REQUEST_CODE = 101
    private var isSelectedSmartAck = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WifiUtils.init(context)

        val deviceTypeList = arrayOf(getString(R.string.text_smart_touch), getString(R.string.text_smart_tack))

        deviceList.clear()
        activity?.let {
            panelAdapter = DeviceAdapter(it, deviceList)
            binding.recyclerRoomPanels.adapter = panelAdapter
        }

        if (viewModel.getDeviceResponse.value == null) {
            showLoading()
            viewModel.getDevice(args.roomDetail.id)
        } else {
            viewModel.getDeviceResponse.value?.let {
                when (it) {
                    is Resource.Success -> {
                        if (it.values.status && it.values.code == Constants.API_SUCCESS_CODE) {
                            it.values.data?.let { deviceData ->
                                deviceList.addAll(deviceData)
                                panelAdapter.notifyDataSetChanged()
                            }
                        }
                        Log.e(logTag, "" + it.values.data)
                    }
                    else -> {
                        panelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.tvTitle.text = args.roomDetail.roomName

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
//                showLoading()
                viewModel.getDevice(args.roomDetail.id)
            } else {
                if (!isSelectedSmartAck) {
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
            }
        })

        activity?.let { mActivity ->
            deviceTypeAdapter = SpinnerAdapter(mActivity, deviceTypeList.toMutableList())
            binding.layoutSelectDevice.spinnerDeviceType.adapter = deviceTypeAdapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateRoomResponse.postValue(null)
        viewModel.addDeviceResponse.postValue(null)
        viewModel.updateDeviceNameResponse.postValue(null)
        viewModel.updateSwitchNameResponse.postValue(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.getDeviceResponse.postValue(null)
    }

    private fun clickEvents() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutSlidingUpPanel.setFadeOnClickListener { hidePanel() }

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
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            if (string.isEmpty()) {
                                Toast.makeText(
                                    it,
                                    "Room name must not be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                    Toast.makeText(
                                        it,
                                        "Device name must not be empty!",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                    Toast.makeText(
                                        it,
                                        "Switch name must not be empty!",
                                        Toast.LENGTH_SHORT
                                    ).show()
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

        binding.ivHidePanel.setOnClickListener {
            hidePanel()
        }

        binding.btnAddPanel.setOnClickListener {
            binding.layoutSelectDevice.linearSelectDevice.isVisible = true
            binding.layoutRoomPanel.linearPanel.isVisible = false
            binding.tvBottomViewTitle.text = getString(R.string.text_select_device)
            showPanel()
        }

        binding.layoutRoomPanel.btnAddPanel.setOnClickListener {
            val deviceName = binding.layoutRoomPanel.edtPanelName.text.toString().trim()
            val serialNumber = binding.layoutRoomPanel.edtSerialNumber.text.toString().trim()

            when {
                deviceName.isEmpty() -> {
                    binding.layoutRoomPanel.edtPanelName.error =
                        getString(R.string.error_text_panel_name)
                }
                serialNumber.isEmpty() -> {
                    binding.layoutRoomPanel.edtSerialNumber.error =
                        getString(R.string.error_text_serial_number)
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

        binding.layoutSelectDevice.ivDown.setOnClickListener {
            binding.layoutSelectDevice.spinnerDeviceType.performClick()
        }

        binding.layoutSelectDevice.btnSave.setOnClickListener {
            if (binding.layoutSelectDevice.spinnerDeviceType.selectedItem == getString(R.string.text_smart_touch)){
                hidePanel()
                binding.layoutSelectDevice.linearSelectDevice.isVisible = false
                binding.layoutRoomPanel.linearPanel.isVisible = true
                binding.tvBottomViewTitle.text = getString(R.string.text_add_panel)
                showPanel()
            }else if (binding.layoutSelectDevice.spinnerDeviceType.selectedItem == getString(R.string.text_smart_tack)){
                hidePanel()
                isSelectedSmartAck = true

                checkPermission()
            }
        }

    }

    private fun showPanel(){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }, 600)
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
                        roomData?.let {
                            args.roomDetail.roomName = it.roomName
                            binding.tvTitle.text = it.roomName
                            roomData = null
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
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
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

                        devicePosition?.let { dPosition ->
                            switchPosition?.let { sPosition ->
                                deviceData?.switchData?.get(sPosition)?.let { sData ->
                                    deviceList[dPosition].switchData?.let {
                                        it[sPosition] = sData
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

    private fun checkPermission() {
        activity?.let {

            Dexter.withContext(it)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let { rep ->
                            if (rep.areAllPermissionsGranted()) {
                                redirectToWifiSetting()
                                return
                            }
                            // check for permanent denial of any permission
                            if (rep.isAnyPermissionPermanentlyDenied) {
                                // show alert dialog navigating to Settings
                                showSettingsDialog()
                            }
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                })
                .withErrorListener { error ->
                    Log.e(logTag, " error $error")
                    Toast.makeText(it, " Error occurred! ", Toast.LENGTH_SHORT).show()
                }
                .onSameThread()
                .check()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun redirectToWifiSetting() {

            var dialog: Dialog? = null
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_wifi_info)
            dialog.setCancelable(true)

            val tvSSID = dialog.findViewById(R.id.tv_default_ssid) as TextView
            val tvPassword = dialog.findViewById(R.id.tv_default_password) as TextView
            val btnOk = dialog.findViewById(R.id.btn_ok) as MaterialButton
            val btnCancel = dialog.findViewById(R.id.btn_cancel) as MaterialButton

            tvSSID.text = "SSID: ${getString(R.string.str_gateway_name)}"
            tvPassword.text = "Password: ${getString(R.string.str_gateway_password)}"

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnOk.setOnClickListener {
                dialog.dismiss()
                if (VersionUtils.isAndroidQOrLater) {
                    val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                    startActivityForResult(panelIntent, WIFI_REQUEST_CODE)
                } else {
                    val wifiIntent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
                    startActivityForResult(wifiIntent, WIFI_REQUEST_CODE)
                }
            }

            val dpHeight: Float
            val dpWidth: Float

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display: Display? = requireContext().display
                val displayMetrics = DisplayMetrics()
                display?.getRealMetrics(displayMetrics)
                dpHeight = displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT
                dpWidth = displayMetrics.widthPixels * 0.85.toFloat()
            } else {
                val display: Display = requireActivity().windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                dpHeight = outMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT
                dpWidth = outMetrics.widthPixels * 0.85.toFloat()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(dpWidth.toInt(), dpHeight.toInt())
            dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("bb", "onActivityResultCde $requestCode")

        if (requestCode == WIFI_REQUEST_CODE) {
            if (WifiUtils.isSSIDWifiConnected(getString(R.string.str_gateway_name))) {
                DialogUtil.loadingAlert(requireActivity(), isCancelable = false)
                Handler(Looper.getMainLooper()).postDelayed({
                    DialogUtil.hideDialog()
                    findNavController().navigate(
                        DeviceFragmentDirections.actionDeviceFragmentToConnectingWifiFragment(
                            false,
                            args.roomDetail
                        )
                    )
                }, 1000)
            } else {
                redirectToWifiSetting()
            }
        }
    }

    private fun showSettingsDialog() {
        activity?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder.setTitle("Need Permissions")
            builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings."
            )
            builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                dialog.cancel()
                openSettings()
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, Constants.REQUEST_OPEN_SETTINGS)
    }
}