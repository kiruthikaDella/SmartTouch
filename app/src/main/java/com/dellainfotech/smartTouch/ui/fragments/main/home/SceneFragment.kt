package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.adapters.ScenesAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyGetScene
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData
import com.dellainfotech.smartTouch.api.model.GetSceneData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentSceneBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 02-06-2021.
 */

class SceneFragment : ModelBaseFragment<HomeViewModel, FragmentSceneBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: SceneFragmentArgs by navArgs()
    private var controlModeRoomData = arrayListOf<ControlModeRoomData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ibCreate.setOnClickListener {
            findNavController().navigate(
                SceneFragmentDirections.actionSceneFragmentToCreateSceneFragment(
                    args.deviceDetail,
                    args.roomDetail,
                    null,
                    controlModeRoomData.toTypedArray()
                )
            )
        }

        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.getScene(BodyGetScene(args.roomDetail.id, args.deviceDetail.id))
        viewModel.getControl()

        viewModel.getControlResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { roomDataList ->
                           controlModeRoomData.addAll(roomDataList)
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

        viewModel.getSceneResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            val scenesAdapter = ScenesAdapter(it)
                            binding.recyclerScenes.adapter = scenesAdapter
                            scenesAdapter.setOnClickListener(object :
                                AdapterItemClickListener<GetSceneData> {
                                override fun onItemClick(data: GetSceneData) {
                                    findNavController().navigate(
                                        SceneFragmentDirections.actionSceneFragmentToCreateSceneFragment(
                                            args.deviceDetail,
                                            args.roomDetail,
                                            data,
                                            controlModeRoomData.toTypedArray()
                                        )
                                    )
                                }

                            })
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSceneBinding = FragmentSceneBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}