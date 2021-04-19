package com.smartouch.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.smartouch.R
import com.smartouch.adapters.HomeRoomsAdapter
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.databinding.FragmentHomeBinding
import com.smartouch.model.HomeRoomModel

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class HomeFragment : Fragment(), AdapterItemClickListener<HomeRoomModel> {

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
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // initializing navigation menu
        setUpNavigationView()

        roomList.add(HomeRoomModel(R.drawable.img_living_room, getString(R.string.text_living_room)))
        roomList.add(HomeRoomModel(R.drawable.img_bedroom, getString(R.string.text_bedroom)))
        roomList.add(HomeRoomModel(R.drawable.img_kitchen, getString(R.string.text_kitchen)))
        roomList.add(HomeRoomModel(R.drawable.img_master_bedroom, getString(R.string.text_master_bedroom)))
        roomsAdapter = HomeRoomsAdapter(roomList)
        binding.recyclerRooms.adapter = roomsAdapter
        roomsAdapter.setCallback(this)
    }

    override fun onItemClick(data: HomeRoomModel) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRoomPanelFragment(data))
        /*var intent = Intent(activity, RoomPanelActivity::class.java)
        intent.putExtra("roomDetail", data)
        startActivity(intent)*/
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