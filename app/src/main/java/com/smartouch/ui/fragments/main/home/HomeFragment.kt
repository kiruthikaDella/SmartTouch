package com.smartouch.ui.fragments.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartouch.R
import com.smartouch.adapters.HomeRoomsAdapter
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.common.utils.dialog
import com.smartouch.databinding.FragmentHomeBinding
import com.smartouch.model.HomeRoomModel
import com.smartouch.ui.activities.AuthenticationActivity
import com.smartouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class HomeFragment : BaseFragment(), AdapterItemClickListener<HomeRoomModel> {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentHomeBinding
    private lateinit var roomsAdapter: HomeRoomsAdapter
    private var roomList = arrayListOf<HomeRoomModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSideNavigation.setOnClickListener {
            openOrCloseDrawer()
        }

        // initializing navigation menu
        setUpNavigationView()

        roomList.add(
            HomeRoomModel(
                R.drawable.img_living_room,
                getString(R.string.text_living_room)
            )
        )
        roomList.add(HomeRoomModel(R.drawable.img_bedroom, getString(R.string.text_bedroom)))
        roomList.add(HomeRoomModel(R.drawable.img_kitchen, getString(R.string.text_kitchen)))
        roomList.add(
            HomeRoomModel(
                R.drawable.img_master_bedroom,
                getString(R.string.text_master_bedroom)
            )
        )
        roomsAdapter = HomeRoomsAdapter(roomList)
        binding.recyclerRooms.adapter = roomsAdapter
        roomsAdapter.setCallback(this)
    }

    override fun onItemClick(data: HomeRoomModel) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToRoomPanelFragment(
                data
            )
        )
    }

    private fun openOrCloseDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setUpNavigationView() {
        binding.sideNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account_settings -> {
                    openOrCloseDrawer()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAccountSettingsFragment())
                }
                R.id.nav_restore_devices -> {
                    activity?.let {
                        dialog.askAlert(
                            it,
                            getString(R.string.dialog_title_restore_device),
                            getString(R.string.text_ok),
                            getString(R.string.text_cancel),
                            null
                        )
                    }
                }
                R.id.nav_profile_reset -> {
                    activity?.let {
                        dialog.askAlert(
                            it,
                            getString(R.string.dialog_title_profile_reset),
                            getString(R.string.text_ok),
                            getString(R.string.text_cancel),
                            null
                        )
                    }
                }
                R.id.nav_faqs -> {
                    openOrCloseDrawer()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFaqsFragment())
                }
                R.id.nav_shop -> {
                    Log.e(logTag, "nav_shop")
                }
                R.id.nav_contact_us -> {
                    openOrCloseDrawer()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToContactUsFragment())
                }
                R.id.nav_logout -> {
                    activity?.let {
                        startActivity(Intent(it, AuthenticationActivity::class.java))
                        it.finishAffinity()
                    }
                }
            }

            true
        }
    }

}