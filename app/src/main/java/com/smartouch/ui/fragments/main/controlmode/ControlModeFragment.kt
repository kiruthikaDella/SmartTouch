package com.smartouch.ui.fragments.main.controlmode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.appizona.yehiahd.fastsave.FastSave
import com.smartouch.R
import com.smartouch.adapters.ControlModeAdapter
import com.smartouch.common.interfaces.DialogAskListener
import com.smartouch.common.utils.Constants
import com.smartouch.common.utils.DialogUtil
import com.smartouch.databinding.FragmentControlModeBinding
import com.smartouch.model.RoomPanelModel
import com.smartouch.ui.activities.AuthenticationActivity
import com.smartouch.ui.fragments.BaseFragment

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
                            FastSave.getInstance().saveBoolean(Constants.isControlModePinned, true)
                        }

                        override fun onNoClicked() {
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