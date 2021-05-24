package com.dellainfotech.smartTouch.ui.fragments.main.controlmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.controlmodeadapter.ControlModeAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentControlModeBinding
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */

class ControlModeFragment : ModelBaseFragment<HomeViewModel, FragmentControlModeBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var controlModeAdapter: ControlModeAdapter
    private var roomList = arrayListOf<ControlModeRoomData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.getControl()

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

        viewModel.getControlResponse.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it,response.values.message,Toast.LENGTH_SHORT).show()
                    }

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE){
                        response.values.data?.let { roomDataList ->
                            roomList.addAll(roomDataList)
                            Log.e(logTag, "  roomList ${roomList.size}")
                            controlModeAdapter = ControlModeAdapter(roomList)
                            binding.recyclerControlModes.adapter = controlModeAdapter
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " getControlResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        DialogUtil.hideDialog()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentControlModeBinding = FragmentControlModeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)
}