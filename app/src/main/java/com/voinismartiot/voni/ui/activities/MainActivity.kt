package com.voinismartiot.voni.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.appizona.yehiahd.fastsave.FastSave
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.teksun.tcpudplibrary.TCPClientService
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.spinneradapter.RoomTypeAdapter
import com.voinismartiot.voni.api.NetworkModule
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyAddRoom
import com.voinismartiot.voni.api.model.DeviceAppliances
import com.voinismartiot.voni.api.model.RoomTypeData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.common.utils.Utils.clearError
import com.voinismartiot.voni.common.utils.Utils.isControlModePin
import com.voinismartiot.voni.common.utils.Utils.toEditable
import com.voinismartiot.voni.databinding.ActivityMainBinding
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.NetworkConnectionLiveData
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.main.home.HomeFragmentDirections
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import com.voinismartiot.voni.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private val networkModel = NetworkModule.provideSmartTouchApi(NetworkModule.provideRetrofit())

    private lateinit var binding: ActivityMainBinding
    private val logTag = this::class.java.simpleName
    private lateinit var navController: NavController

    private var roomTypeList = arrayListOf<RoomTypeData>()
    private var roomTypeId: String? = null
    private lateinit var roomTypeAdapter: RoomTypeAdapter

    private var appliancesList = arrayListOf<DeviceAppliances>()
    private var isApiCalled = false

    //
    //region override methods
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.statusBarColor = getColor(R.color.app_background_color)

        val factory = ViewModelFactory(HomeRepository(networkModel))
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        FastSave.getInstance().saveBoolean(Constants.IS_LOGGED_IN, true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment

        navController = navHostFragment.navController

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.controlModeFragment, R.id.userManagementFragment, R.id.sceneFragment -> {
                    binding.linearBottomNavigationView.visibility = View.VISIBLE
                    binding.ivAddRoom.visibility = View.VISIBLE
                }
                else -> {
                    binding.linearBottomNavigationView.visibility = View.GONE
                    binding.ivAddRoom.visibility = View.GONE
                }
            }
        }

        roomTypeAdapter = RoomTypeAdapter(this, roomTypeList)
        binding.layoutAddRoom.spinnerRoom.adapter = roomTypeAdapter

        binding.layoutAddRoom.spinnerRoom.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val room = parent?.selectedItem as RoomTypeData
                    roomTypeId = room.roomTypeId
                }

                override fun onNothingSelected(parent: AdapterView<*>?) =
                    Unit

            }

        bottomNavigationClickEvent()

        if (isControlModePin()) {
            binding.ivControlMode.performClick()
            hideBottomNavigation()
        }

        binding.ivAddRoom.setOnClickListener {
            showPanel()
        }

        binding.layoutAddRoom.ivHidePanel.setOnClickListener {
            hidePanel()
        }

        binding.layoutAddRoom.ivDown.setOnClickListener {
            binding.layoutAddRoom.spinnerRoom.performClick()
        }

        binding.layoutAddRoom.btnAddRoom.setOnClickListener {
            val roomName = binding.layoutAddRoom.edtRoomName.text.toString()
            when {
                roomTypeId == null -> {
                    showToast("Please select Room Type")
                }
                roomName.isBlank() -> {
                    binding.layoutAddRoom.edtRoomName.error = "Please enter Room Name"
                    binding.layoutAddRoom.edtRoomName.requestFocus()
                }
                else -> {
                    hidePanel()
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog()
                        viewModel.addRoom(BodyAddRoom(roomTypeId!!, roomName))
                    }, 600)
                }
            }
        }

        binding.layoutSlidingUpPanel.setFadeOnClickListener { hidePanel() }

        binding.layoutSlidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) = Unit

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    binding.layoutAddRoom.edtRoomName.text = "".toEditable()
                    binding.layoutAddRoom.edtRoomName.clearError()
                    hideSoftKeyboard()
                }
            }
        })

        NetworkConnectionLiveData().observe(this) { isConnected ->
            NotifyManager.internetInfo.postValue(isConnected)

            if (isConnected && navController.currentDestination?.id == R.id.homeFragment) {
                roomTypeList.clear()
                viewModel.roomType()
            }

            if (isConnected) {
                appliancesList.clear()
                viewModel.getDeviceAppliances()
            }

            AwsMqttSingleton.connectAWS()

        }

        apiResponses()
    }

    override fun onResume() {
        super.onResume()
        hideSoftKeyboard()
    }

    override fun onStart() {
        super.onStart()
        hideSoftKeyboard()
    }

    override fun onDestroy() {
        hideDialog()
        super.onDestroy()
    }

    override fun onBackPressed() {

        if (navController.currentDestination?.id == R.id.userManagementFragment || navController.currentDestination?.id == R.id.controlModeFragment || navController.currentDestination?.id == R.id.sceneFragment) {
            binding.ivHome.performClick()
        } else if (navController.currentDestination?.id == R.id.homeFragment) {
            finishAffinity()
        } else if (navController.currentDestination?.id == R.id.connectingWifiFragment && TCPClientService.getSocket() != null) {
            disconnectTCPClient()
        } else if (navController.currentDestination?.id == R.id.configWifiFragment && TCPClientService.getSocket() != null) {
            disconnectTCPClient()
        } else {
            super.onBackPressed()
        }
    }

    fun disconnectTCPClient() {
        TCPClientService.closeSocket(object : CloseSocketListener {
            override fun onSuccess(message: String) {
                Log.e(logTag, message)
                navController.navigateUp()
            }

            override fun onFailure(message: String) {
                Log.e(logTag, message)
            }
        })
    }

    //
    //endregion
    //

    //
    //region private methods
    //

    fun showBottomNavigation() {
        binding.linearBottomNavigationView.visibility = View.VISIBLE
        binding.ivAddRoom.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding.linearBottomNavigationView.visibility = View.GONE
        binding.ivAddRoom.visibility = View.GONE
    }

    private fun hidePanel() {
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    private fun showPanel() {
        binding.layoutAddRoom.edtRoomName.text = "".toEditable()
        binding.layoutAddRoom.spinnerRoom.setSelection(0)
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun bottomNavigationClickEvent() {
        binding.ivHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                binding.ivHome.setColorFilter(
                    ContextCompat.getColor(this, R.color.theme_color),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivUser.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivControlMode.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivScene.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                navController.navigate(R.id.homeFragment)
            }
        }

        binding.ivUser.setOnClickListener {
            if (navController.currentDestination?.id != R.id.userManagementFragment) {
                if (Utils.isMasterUser()) {
                    binding.ivUser.setColorFilter(
                        ContextCompat.getColor(this, R.color.theme_color),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.ivHome.setColorFilter(
                        ContextCompat.getColor(this, R.color.daintree),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.ivControlMode.setColorFilter(
                        ContextCompat.getColor(this, R.color.daintree),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.ivScene.setColorFilter(
                        ContextCompat.getColor(this, R.color.daintree),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    navController.navigate(R.id.userManagementFragment)
                } else {
                    deviceOfflineAlert(
                        getString(R.string.error_text_unauthorized),
                        object : DialogShowListener {
                            override fun onClick() {
                                hideDialog()
                            }
                        })
                }

            }

        }

        binding.ivControlMode.setOnClickListener {
            if (navController.currentDestination?.id != R.id.controlModeFragment) {
                binding.ivControlMode.setColorFilter(
                    ContextCompat.getColor(this, R.color.theme_color),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivHome.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivUser.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivScene.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                navController.navigate(R.id.controlModeFragment)
            }
        }

        binding.ivScene.setOnClickListener {
            if (navController.currentDestination?.id != R.id.sceneFragment) {
                binding.ivScene.setColorFilter(
                    ContextCompat.getColor(this, R.color.theme_color),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivHome.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivUser.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.ivControlMode.setColorFilter(
                    ContextCompat.getColor(this, R.color.daintree),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                navController.navigate(R.id.sceneFragment)
            }
        }
    }

    private fun apiResponses() {

        lifecycleScope.launchWhenStarted {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.roomTypeResponse.collectLatest { response ->
                        roomTypeList.clear()
                        when (response) {
                            is Resource.Success -> {
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { listData ->
                                        roomTypeList.addAll(listData)
                                        roomTypeAdapter.notifyDataSetChanged()
                                    }

                                } else {
                                    this@MainActivity.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                if (navController.currentDestination?.id != R.id.connectingWifiFragment && navController.currentDestination?.id != R.id.configWifiFragment) {
                                    Log.e(
                                        logTag,
                                        " roomTypeResponse error ${response.errorBody?.string()}"
                                    )
                                }
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.addRoomResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { roomData ->
                                        if (navController.currentDestination?.id == R.id.homeFragment) {
                                            navController.navigate(
                                                HomeFragmentDirections.actionHomeFragmentToRoomPanelFragment(
                                                    roomData
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, " addRoomResponse ${response.errorBody?.string()} ")
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.getDeviceAppliancesResponse.collectLatest { response ->
                        isApiCalled = true
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { appliancesData ->
                                        appliancesList.addAll(appliancesData)
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                Log.e(
                                    logTag,
                                    " getDeviceAppliancesResponse ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

            }

        }

    }

    fun getAppliances() = appliancesList

    //
    //endregion
    //

}