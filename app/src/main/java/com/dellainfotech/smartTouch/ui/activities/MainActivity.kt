package com.dellainfotech.smartTouch.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.dellainfotech.smartTouch.adapters.spinneradapter.RoomTypeAdapter
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.databinding.ActivityMainBinding
import com.dellainfotech.smartTouch.model.HomeRoomModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val logTag = this::class.java.simpleName
    private lateinit var navController: NavController

    private var roomList = arrayOf("Living Room", "Bedroom", "Kitchen", "Master Bedroom")
    private var roomTypeList = arrayListOf<HomeRoomModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.statusBarColor = getColor(R.color.app_background_color)

        FastSave.getInstance().saveBoolean(Constants.IS_LOGGED_IN,true)

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
            binding.linearBottomNavigationView.visibility = View.GONE
            binding.ivAddRoom.visibility = View.GONE
        }

        binding.ivAddRoom.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.layoutAddRoom.ivHidePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

/*        val roomAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown, roomList)
        roomAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        binding.layoutAddRoom.spinnerRoom.adapter = roomAdapter*/

        roomTypeList.clear()
        roomTypeList.add(
            HomeRoomModel(
                R.drawable.img_living_room,
                getString(R.string.text_living_room)
            )
        )
        roomTypeList.add(HomeRoomModel(R.drawable.img_bedroom, getString(R.string.text_bedroom)))
        roomTypeList.add(HomeRoomModel(R.drawable.img_kitchen, getString(R.string.text_kitchen)))
        roomTypeList.add(
            HomeRoomModel(
                R.drawable.img_master_bedroom,
                getString(R.string.text_master_bedroom)
            )
        )

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

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        bottomNavigationClickEvent()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
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

}