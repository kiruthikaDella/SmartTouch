package com.smartouch.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.smartouch.R
import com.smartouch.databinding.ActivityMainBinding

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val logTag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // initializing navigation menu
        setUpNavigationView()

        binding.ivSideNavigation.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment

        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navHostFragment.navController
        )

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.relativeMainHeader.visibility = View.VISIBLE
                    binding.coordinatorBottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.relativeMainHeader.visibility = View.GONE
                    binding.coordinatorBottomNavigation.visibility = View.GONE
                }
            }
        }

    }

    private fun setUpNavigationView() {
        binding.sideNavigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account_settings -> {
                    Log.e(logTag, "nav_account_settings")
                }
                R.id.nav_restore_devices -> {
                    Log.e(logTag, "nav_restore_devices")
                }
                R.id.nav_profile_reset -> {
                    Log.e(logTag, "nav_profile_reset")
                }
                R.id.nav_faqs -> {
                    Log.e(logTag, "nav_faqs")
                }
                R.id.nav_shop -> {
                    Log.e(logTag, "nav_shop")
                }
                R.id.nav_contact_us -> {
                    Log.e(logTag, "nav_contact_us")
                }
                R.id.nav_logout -> {
                    Log.e(logTag, "nav_logout")
                }
            }

            true
        })
    }
}