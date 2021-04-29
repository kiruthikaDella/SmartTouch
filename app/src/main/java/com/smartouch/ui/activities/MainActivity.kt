package com.smartouch.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartouch.R
import com.smartouch.common.utils.Constants
import com.smartouch.databinding.ActivityMainBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val logTag = this::class.java.simpleName
    private lateinit var navController: NavController

    private var roomList = arrayOf("Living Room", "Bedroom", "Kitchen", "Master Bedroom")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.statusBarColor = getColor(R.color.app_background_color)

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

        val roomAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown, roomList)
        roomAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        binding.layoutAddRoom.spinnerRoom.adapter = roomAdapter

        binding.layoutAddRoom.spinnerRoom.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent?.getChildAt(0)?.let { mView ->
                        val textView = mView as TextView
                        textView.setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.theme_color
                            )
                        )
                        textView.gravity = Gravity.CENTER
                        textView.setBackgroundColor(Color.TRANSPARENT)
                    }
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

}