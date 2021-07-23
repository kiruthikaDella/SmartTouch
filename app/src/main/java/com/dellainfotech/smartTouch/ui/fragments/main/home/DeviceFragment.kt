package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.dellainfotech.smartTouch.ui.activities.MainActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception

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

        /*val wifiIntentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (WifiUtils.isSSIDWifiConnected("Binjal")) {
                Log.e(logTag, "Connected ")
                findNavController().navigate(DeviceFragmentDirections.actionDeviceFragmentToConfigWifiFragment())
            } else {
                Log.e(logTag, "Not Connected")

                val wifiIntent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
                wifiIntentLauncher.launch(wifiIntent)
            }
        }*/

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

            Dexter.withActivity(it)
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

    private fun redirectToWifiSetting() {
        DialogUtil.featureDetailAlert(requireActivity(), "Wifi SSID and Password",
            "1. Click on ok button and redirect to wifi settings\n2. Select gateway default wifi SSID and enter password and connect\n3. And go back to application\nSSID is : XYZ\nPassword is : XYZ", object: DialogShowListener {
                override fun onClick() {

                   /* val intentFilter = IntentFilter()
                    intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
                    requireContext().registerReceiver(wifiReceiver, intentFilter)*/

                    val wifiIntent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
//                wifiIntentLauncher.launch(wifiIntent)
                    startActivityForResult(wifiIntent, WIFI_REQUEST_CODE)
                }

            })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("bb", "onActivityResultCde $requestCode")

        if (requestCode == WIFI_REQUEST_CODE) {
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(
                    DeviceFragmentDirections.actionDeviceFragmentToConnectingWifiFragment(
                        false,
                        args.roomDetail
                    )
                )
            }, 600)
        }



       /* if (WifiUtils.isSSIDWifiConnected("Binjal")) {
            Log.e(logTag, "Connected ")

            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(DeviceFragmentDirections.actionDeviceFragmentToConnectingWifiFragment(false, args.roomDetail))
            }, 600)
        } else {
            Log.e(logTag, "Not Connected")

            redirectToWifiSetting()
        }*/
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

    val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) {
                val state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE) as SupplicantState?

                if ((SupplicantState.isValidState(state)) && state == SupplicantState.COMPLETED) {
                    if (WifiUtils.isSSIDWifiConnected("Binjal")) {
                        Log.e("Binjal", "OnRecive called")

                        /*startActivity(Intent(context, MainActivity::class.java).apply {
                            putExtra("WIFI_CONNECTED", true)
                        })*/

//                        findNavController().navigate(findNavController().currentDestination?.id!!)

                        /*val launchIntent = requireContext().packageManager.getLaunchIntentForPackage("com.dellainfotech.smartTouch")
                        launchIntent?.let {
                            startActivity(launchIntent)
                            findNavController().navigate(findNavController().currentDestination?.id!!)
                        }*/

                        val first = findNavController().currentDestination

                        Log.e("Binjal", (first?.id == R.id.deviceFragment).toString())
                        /*findNavController().resumedNavigation(
                            lifecycle, R.id.main_fragment_container)
*/
                        /*startActivity(Intent(requireActivity(), MainActivity::class.java))
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(DeviceFragmentDirections.actionDeviceFragmentToConnectingWifiFragment(false, args.roomDetail))
                        }, 600)*/
                    }
                }
            }

        }
    }

    fun NavController.resumedNavigation(lifecycle: Lifecycle, destination: Int) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            //App is resumed, continue navigation.
                Log.e("Binjal", "App is resumed, continue navigation.")
            navigate(destination)
        } else {
            //When app is resumed, remove observer and navigate to destination/
            Log.e("Binjal", "app is resumed, remove observer and navigate to destination")

            lifecycle.addObserver(object: LifecycleObserver {
                @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
                fun onResume() {
                    lifecycle.removeObserver(this)
                    navigate(destination)
                }
            })
        }
    }
}