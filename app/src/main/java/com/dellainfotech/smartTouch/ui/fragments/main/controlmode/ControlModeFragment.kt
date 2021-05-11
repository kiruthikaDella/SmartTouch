package com.dellainfotech.smartTouch.ui.fragments.main.controlmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.ControlModeAdapter
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentControlModeBinding
import com.dellainfotech.smartTouch.model.RoomPanelModel
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */

class ControlModeFragment : BaseFragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentControlModeBinding
    private lateinit var controlModeAdapter: ControlModeAdapter
    private var roomList = arrayListOf<RoomPanelModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentControlModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (FastSave.getInstance()
                .getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)
        ) {
            binding.ibLogout.isVisible = true
            binding.ibPin.rotation = -45f
        }

        binding.ibPin.setOnClickListener {
            activity?.let {
                DialogUtil.askAlert(
                    it,
                    getString(R.string.dialog_title_pin_control_mode),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            Log.e(logTag, " yes clicked")
                            FastSave.getInstance().saveBoolean(Constants.isControlModePinned, true)
                            Log.e(logTag,"iscontrollpinned ${FastSave.getInstance().getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)} ")
                        }

                        override fun onNoClicked() {
                            Log.e(logTag, " no clicked")
                            FastSave.getInstance().saveBoolean(Constants.isControlModePinned, false)
                        }
                    }
                )
            }
        }

        binding.ibLogout.setOnClickListener {
            activity?.let {
                startActivity(Intent(it, AuthenticationActivity::class.java))
                it.finishAffinity()
            }
        }

        roomList.clear()
        roomList.add(RoomPanelModel(1, getString(R.string.text_living_room)))
        roomList.add(RoomPanelModel(2, getString(R.string.text_bedroom)))
        roomList.add(RoomPanelModel(1, getString(R.string.text_kitchen)))
        roomList.add(RoomPanelModel(1, getString(R.string.text_master_bedroom)))
        roomList.add(RoomPanelModel(2, getString(R.string.text_living_room)))
        roomList.add(RoomPanelModel(2, getString(R.string.text_bedroom)))
        controlModeAdapter = ControlModeAdapter(roomList)
        binding.recyclerControlModes.adapter = controlModeAdapter
    }

    override fun onStop() {
        super.onStop()
        DialogUtil.hideDialog()
    }
}