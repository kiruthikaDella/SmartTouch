package com.smartouch.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
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

        binding.bottomNavigationView.setupWithNavController(navController)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.controlModeFragment, R.id.userManagementFragment, R.id.contactUsFragment -> {
                    binding.coordinatorBottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.coordinatorBottomNavigation.visibility = View.GONE
                }
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        if (FastSave.getInstance()
                .getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)
        ) {
            binding.bottomNavigationView.menu.getItem(3).isChecked = true
            navController.navigate(R.id.controlModeFragment)
            binding.coordinatorBottomNavigation.visibility = View.GONE
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
                    (parent?.getChildAt(0) as TextView).setTextColor(getColor(R.color.theme_color))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}