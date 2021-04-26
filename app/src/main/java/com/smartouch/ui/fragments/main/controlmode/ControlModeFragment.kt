package com.smartouch.ui.fragments.main.controlmode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smartouch.R
import com.smartouch.adapters.ControlModeAdapter
import com.smartouch.common.utils.dialog
import com.smartouch.databinding.FragmentControlModeBinding
import com.smartouch.model.HomeRoomModel

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */

class ControlModeFragment : Fragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentControlModeBinding
    private lateinit var controlModeAdapter: ControlModeAdapter
    private var roomList = arrayListOf<HomeRoomModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentControlModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ibPin.setOnClickListener {
            activity?.let {
                dialog.askAlert(
                    it,
                    getString(R.string.dialog_title_pin_control_mode),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    null
                )
            }
        }
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
        controlModeAdapter = ControlModeAdapter(roomList)
        binding.recyclerControlModes.adapter = controlModeAdapter
    }

    override fun onStop() {
        super.onStop()
        dialog.hideDialog()
    }
}