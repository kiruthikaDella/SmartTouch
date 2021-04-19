package com.smartouch.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.smartouch.R
import com.smartouch.databinding.ActivityMainBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout


/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val logTag = this::class.java.simpleName

    //    private var roomList: List<String> = ArrayList()
    private var roomList = arrayOf("Living Room", "Bedroom", "Kitchen", "Master Bedroom")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment

        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navHostFragment.navController
        )

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.coordinatorBottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.coordinatorBottomNavigation.visibility = View.GONE
                }
            }
        }

        binding.ivAddRoom.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.ivHidePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

        val roomAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown, roomList)
        roomAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        binding.spinnerRoom.adapter = roomAdapter

        binding.spinnerRoom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

}