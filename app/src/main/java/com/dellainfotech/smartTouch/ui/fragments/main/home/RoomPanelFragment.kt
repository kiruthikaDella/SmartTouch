package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.RoomPanelsAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddDevice
import com.dellainfotech.smartTouch.api.body.BodyRetainState
import com.dellainfotech.smartTouch.api.body.BodyUpdateRoom
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogEditListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.databinding.FragmentRoomPanelBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class RoomPanelFragment :
    ModelBaseFragment<HomeViewModel, FragmentRoomPanelBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: RoomPanelFragmentArgs by navArgs()
    private var deviceList = arrayListOf<GetDeviceData>()
    private lateinit var panelAdapter: RoomPanelsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceList.clear()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTitle.text = args.roomDetail.roomName
        binding.switchRetainState.isChecked = args.roomDetail.retainState.toBoolean()

        binding.switchRetainState.setOnCheckedChangeListener { _, isChecked ->
            viewModel.retainState(BodyRetainState(args.roomDetail.id, isChecked.toInt()))
        }

        shoLoading()
        viewModel.getDevice(args.roomDetail.id)

        binding.iBtnEditRoomName.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it,
                    getString(R.string.text_edit),
                    args.roomDetail.roomName ?: "",
                    getString(R.string.text_save),
                    getString(R.string.text_cancel),
                    object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            if (string.isEmpty()) {
                                Toast.makeText(
                                    it,
                                    "RoomName must not be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                DialogUtil.loadingAlert(it)
                                viewModel.updateRoom(BodyUpdateRoom(args.roomDetail.id, string))
                            }
                        }

                        override fun onNoClicked() {

                        }

                    }
                )
            }
        }

        panelAdapter = RoomPanelsAdapter(deviceList)
        binding.recyclerRoomPanels.adapter = panelAdapter

        panelAdapter.setOnCustomizationClickListener(object :
            AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceCustomizationFragment(data))
            }
        })

        panelAdapter.setOnFeaturesClickListener(object : AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceFeaturesFragment())
            }
        })

        panelAdapter.setOnEditClickListener(object : AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                activity?.let {
                    DialogUtil.editDialog(
                        it,
                        getString(R.string.text_edit),
                        "Switch 1",
                        getString(R.string.text_save),
                        getString(R.string.text_cancel)
                    )
                }
            }

        })

        panelAdapter.setOnSettingsClickListener(object : AdapterItemClickListener<GetDeviceData> {
            override fun onItemClick(data: GetDeviceData) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceSettingsFragment())
            }

        })

        binding.layoutRoomPanel.ivHidePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

        binding.btnAddPanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.layoutRoomPanel.btnAddPanel.setOnClickListener {
            val deviceName = binding.layoutRoomPanel.edtPanelName.text.toString().trim()
            val serialNumber = binding.layoutRoomPanel.edtSerialNumber.text.toString().trim()

            if (deviceName.isEmpty()) {
                binding.layoutRoomPanel.edtPanelName.error =
                    getString(R.string.error_text_panel_name)
            } else if (serialNumber.isEmpty()) {
                binding.layoutRoomPanel.edtSerialNumber.error =
                    getString(R.string.error_text_serial_number)
            } else {
                binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                Handler(Looper.getMainLooper()).postDelayed({
                    shoLoading()
                    viewModel.addDevice(BodyAddDevice(serialNumber, args.roomDetail.id, deviceName))
                }, 600)
            }
        }

        apiCall()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRoomPanelBinding = FragmentRoomPanelBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun shoLoading() {
        activity?.let {
            DialogUtil.loadingAlert(it)
        }
    }

    private fun apiCall() {
        viewModel.updateRoomResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        binding.tvTitle.text = response.values.data?.roomName
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " updateRoomResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.addDeviceResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "addDeviceResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.getDeviceResponse.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE){
                        response.values.data?.let { deviceData ->
                            deviceList.addAll(deviceData)
                            panelAdapter.notifyDataSetChanged()
                        }
                    }
                }
                is Resource.Failure -> {
                    Log.e(logTag,"getDeviceResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

    }

}