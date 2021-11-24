package com.voinismartiot.voni.ui.fragments.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binjal.wifilibrary.VersionUtils
import com.binjal.wifilibrary.WifiUtils
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.DeviceAdapter
import com.voinismartiot.voni.adapters.spinneradapter.SpinnerAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.*
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.api.model.GetDeviceData
import com.voinismartiot.voni.api.model.GetRoomData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.interfaces.DialogEditListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.common.utils.Utils.clearError
import com.voinismartiot.voni.common.utils.Utils.toEditable
import com.voinismartiot.voni.databinding.FragmentDeviceBinding
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
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
    private lateinit var deviceTypeAdapter: SpinnerAdapter
    private var isSelectedSmarTouch = true
    private var isSelectedSmartAck: Boolean? = null

    private val wifiRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(Constants.REQUEST_WIFI_CODE, result)
        }

    private val openSettingRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(Constants.REQUEST_OPEN_SETTINGS, result)
        }

    private val gpsRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(Constants.REQUEST_GPS_CODE, result)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WifiUtils.init(context)

        val deviceTypeList =
            arrayOf(
                getString(R.string.text_smart_touch),
                getString(R.string.text_smart_tack),
                getString(R.string.text_smart_tap)
            )

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
                if (isSelectedSmarTouch) {
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
        isSelectedSmarTouch = true
    }


    private fun clickEvents() {

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

        binding.layoutSelectDevice.ivDown.setOnClickListener {
            binding.layoutSelectDevice.spinnerDeviceType.performClick()
        }

        binding.layoutSelectDevice.btnSave.setOnClickListener {
            when (binding.layoutSelectDevice.spinnerDeviceType.selectedItem) {
                getString(R.string.text_smart_touch) -> {
                    hidePanel()
                    binding.layoutSelectDevice.linearSelectDevice.isVisible = false
                    binding.layoutRoomPanel.linearPanel.isVisible = true
                    binding.tvBottomViewTitle.text = getString(R.string.text_add_panel)
                    showPanel()
                }
                getString(R.string.text_smart_tack) -> {
                    hidePanel()
                    isSelectedSmarTouch = false
                    isSelectedSmartAck = true
                    checkPermission()
                }
                getString(R.string.text_smart_tap) -> {
                    hidePanel()
                    isSelectedSmarTouch = false
                    isSelectedSmartAck = false
                    checkPermission()
                }
            }
        }

    }

    private fun showPanel() {
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

    private fun checkPermission() {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            context?.let {
                Toast.makeText(it, "Please turn on GPS", Toast.LENGTH_SHORT).show()
                val gpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                gpsRegister.launch(gpsIntent)
            }
        } else {
            Log.d(logTag, "checkPermission: GPS Enable")
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
    }

    @SuppressLint("SetTextI18n")
    private fun redirectToWifiSetting() {
        context?.let {
            val dialog = Dialog(it)

            dialog.setContentView(R.layout.dialog_wifi_info)
            dialog.setCancelable(true)

            val tvInstructionInfo = dialog.findViewById(R.id.tvInstructionsInfo) as TextView
            val tvSSID = dialog.findViewById(R.id.tv_default_ssid) as TextView
            val tvPassword = dialog.findViewById(R.id.tv_default_password) as TextView
            val btnOk = dialog.findViewById(R.id.btn_ok) as MaterialButton
            val btnCancel = dialog.findViewById(R.id.btn_cancel) as MaterialButton

            isSelectedSmartAck?.let { isSmartAck ->
                if (isSmartAck) {
                    tvSSID.text = "SSID: ${getString(R.string.text_smart_tack)}"
                } else {
                    tvSSID.text = "SSID: ${getString(R.string.text_smart_tap)}"
                }
            }

            tvPassword.text = "Password: ${getString(R.string.str_gateway_password)}"

            if (VersionUtils.isAndroidQOrLater) {
                tvInstructionInfo.text = getString(R.string.text_wifi_instruction_10)
            } else {
                tvInstructionInfo.text = getString(R.string.text_wifi_instruction)
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnOk.setOnClickListener {
                dialog.dismiss()
                if (VersionUtils.isAndroidQOrLater) {
                    val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                    wifiRegister.launch(panelIntent)
                } else {
                    val wifiIntent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
                    wifiRegister.launch(wifiIntent)
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
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        Log.e(logTag, "Request code is $requestCode")
        var currentSSID = ""
        if (requestCode == Constants.REQUEST_WIFI_CODE) {
            isSelectedSmartAck?.let { isSmartAck ->
                currentSSID = if (isSmartAck) {
                    getString(R.string.text_smart_tack)
                } else {
                    getString(R.string.text_smart_tap)
                }
            }
            if (WifiUtils.isSSIDWifiConnected(currentSSID)) {
                activity?.let {
                    DialogUtil.loadingAlert(it, isCancelable = false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        DialogUtil.hideDialog()
                        isSelectedSmartAck?.let { isSmarTack ->
                            findNavController().navigate(
                                DeviceFragmentDirections.actionDeviceFragmentToConnectingWifiFragment(
                                    false,
                                    args.roomDetail, isSmarTack
                                )
                            )
                        }
                    }, 1000)
                }
            } else {
                redirectToWifiSetting()
            }
        } else if (requestCode == Constants.REQUEST_GPS_CODE) {
            checkPermission()
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
        openSettingRegister.launch(intent)
    }
}