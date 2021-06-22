package com.dellainfotech.smartTouch.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.spinneradapter.RoomTypeAdapter
import com.dellainfotech.smartTouch.api.NetworkModule
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddRoom
import com.dellainfotech.smartTouch.api.model.RoomTypeData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.clearError
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
import com.dellainfotech.smartTouch.databinding.ActivityMainBinding
import com.dellainfotech.smartTouch.mqtt.NetworkConnectionLiveData
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.main.home.HomeFragmentDirections
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.dellainfotech.smartTouch.ui.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout


/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: HomeViewModel
    private val networkModel = NetworkModule.provideSmartTouchApi(NetworkModule.provideRetrofit())

    private lateinit var binding: ActivityMainBinding
    private val logTag = this::class.java.simpleName
    private lateinit var navController: NavController

    private var roomTypeList: List<RoomTypeData> = ArrayList()
    private var roomTypeId: String? = null

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
                R.id.homeFragment, R.id.controlModeFragment, R.id.userManagementFragment, R.id.contactUsFragment -> {
                    binding.linearBottomNavigationView.visibility = View.VISIBLE
                    binding.ivAddRoom.visibility = View.VISIBLE
                }
                else -> {
                    binding.linearBottomNavigationView.visibility = View.GONE
                    binding.ivAddRoom.visibility = View.GONE
                }
            }
        }

        if (FastSave.getInstance()
                .getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)
        ) {
            navController.navigate(R.id.controlModeFragment)
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
                    Toast.makeText(this, "Please select Room Type", Toast.LENGTH_SHORT).show()
                }
                roomName.isBlank() -> {
                    binding.layoutAddRoom.edtRoomName.error = "Please enter Room Name"
                }
                else -> {
                    hidePanel()
                    Handler(Looper.getMainLooper()).postDelayed({
                        DialogUtil.loadingAlert(this)
                        viewModel.addRoom(BodyAddRoom(roomTypeId!!, roomName))
                    }, 600)
                }
            }
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
                    binding.layoutAddRoom.edtRoomName.text = "".toEditable()
                    binding.layoutAddRoom.edtRoomName.clearError()
                }
            }
        })

        NotifyManager.internetInfo.observe(this, { isConnected ->
            if (isConnected) {
                roomTypeList.toMutableList().clear()
                viewModel.roomType()
            }
        })

        NetworkConnectionLiveData().observe(this, { isConnected ->
            NotifyManager.internetInfo.postValue(isConnected)
        })

        bottomNavigationClickEvent()
        apiResponses()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

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
            binding.ivContactUs.setColorFilter(
                ContextCompat.getColor(this, R.color.daintree),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            navController.navigate(R.id.homeFragment)
        }

        binding.ivUser.setOnClickListener {
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
            binding.ivContactUs.setColorFilter(
                ContextCompat.getColor(this, R.color.daintree),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            navController.navigate(R.id.userManagementFragment)
        }

        binding.ivControlMode.setOnClickListener {
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
            binding.ivContactUs.setColorFilter(
                ContextCompat.getColor(this, R.color.daintree),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            navController.navigate(R.id.controlModeFragment)
        }

        binding.ivContactUs.setOnClickListener {
            binding.ivContactUs.setColorFilter(
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
            navController.navigate(R.id.contactUsFragment)
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.userManagementFragment || navController.currentDestination?.id == R.id.controlModeFragment || navController.currentDestination?.id == R.id.contactUsFragment) {
            binding.ivHome.performClick()
        } else if (navController.currentDestination?.id == R.id.homeFragment) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    private fun apiResponses() {
        viewModel.roomTypeResponse.observe(this, { response ->
            roomTypeList.toMutableList().clear()
            when (response) {
                is Resource.Success -> {
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        roomTypeList = response.values.data!!

                        if (roomTypeList.isNotEmpty()) {
                            val roomAdapter = RoomTypeAdapter(this, roomTypeList)
                            binding.layoutAddRoom.spinnerRoom.adapter = roomAdapter

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

                                    override fun onNothingSelected(parent: AdapterView<*>?) {

                                    }

                                }
                        }
                    } else {
                        Toast.makeText(this, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    Log.e(logTag, " roomTypeResponse error ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.addRoomResponse.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { roomData ->
                            navController.navigate(
                                HomeFragmentDirections.actionHomeFragmentToRoomPanelFragment(
                                    roomData
                                )
                            )
                        }
                    } else {
                        Toast.makeText(this, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " addRoomResponse ${response.errorBody.toString()} ")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })
    }

}