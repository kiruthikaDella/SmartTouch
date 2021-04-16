package com.smartouch.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        roomList.add(HomeRoomModel(R.drawable.img_living_room, "Living Room"))
        roomList.add(HomeRoomModel(R.drawable.img_bedroom, "Bedroom"))
        roomList.add(HomeRoomModel(R.drawable.img_kitchen, "Kitchen"))
        roomList.add(HomeRoomModel(R.drawable.img_master_bedroom, "Master Bedroom"))
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

}