package com.dellainfotech.smartTouch.ui.fragments.main.scenes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.ScenesAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyGetScene
import com.dellainfotech.smartTouch.api.body.BodyUpdateSceneStatus
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData
import com.dellainfotech.smartTouch.api.model.GetSceneData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentSceneBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 02-06-2021.
 */

class SceneFragment : ModelBaseFragment<HomeViewModel, FragmentSceneBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private var controlModeRoomData = arrayListOf<ControlModeRoomData>()
    private lateinit var sceneAdapter: ScenesAdapter
    private val sceneList: MutableList<GetSceneData> = ArrayList()
    private var sceneData: GetSceneData? = null
    private var sceneStatus: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibCreate.setOnClickListener {
            findNavController().navigate(
                SceneFragmentDirections.actionSceneFragmentToCreateSceneFragment(
                    null,
                    controlModeRoomData.toTypedArray()
                )
            )
        }

        sceneAdapter = ScenesAdapter(sceneList)
        binding.recyclerScenes.adapter = sceneAdapter
        sceneAdapter.setOnClickListener(object :
            AdapterItemClickListener<GetSceneData> {
            override fun onItemClick(data: GetSceneData) {
                findNavController().navigate(
                    SceneFragmentDirections.actionSceneFragmentToCreateSceneFragment(
                        data,
                        controlModeRoomData.toTypedArray()
                    )
                )
            }

        })

        sceneAdapter.setOnDeleteClickListener(object : AdapterItemClickListener<GetSceneData> {
            override fun onItemClick(data: GetSceneData) {
                activity?.let {
                    DialogUtil.askAlert(
                        it, getString(R.string.dialog_title_delete_scene),
                        getString(R.string.text_yes),
                        getString(R.string.text_no),
                        object : DialogAskListener {
                            override fun onYesClicked() {
                                DialogUtil.loadingAlert(it)
                                viewModel.deleteScene(data.id)
                            }

                            override fun onNoClicked() {
                            }

                        }
                    )
                }
            }

        })

        sceneAdapter.setOnSwitchClickListener(object :
            ScenesAdapter.SwitchItemClickListener<GetSceneData> {
            override fun onItemClick(data: GetSceneData, sceneStatusValue: Int) {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                sceneStatus = sceneStatusValue
                sceneData = data
                viewModel.updateSceneStatus(data.id, BodyUpdateSceneStatus(sceneStatus))
            }

        })

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getScene(BodyGetScene("", ""))
                viewModel.getControl()
            }
        })

        apiResponse()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSceneBinding = FragmentSceneBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    override fun onStop() {
        super.onStop()
        viewModel.deleteSceneResponse.postValue(null)
    }

    private fun apiResponse() {
        viewModel.getControlResponse.observe(viewLifecycleOwner, { response ->
            controlModeRoomData.clear()
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
                    context?.let {
                        Toast.makeText(it, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                    Log.e(logTag, " getControlResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.getSceneResponse.observe(viewLifecycleOwner, { response ->
            sceneList.clear()
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            sceneList.addAll(it)
                        }
                    }
                    sceneAdapter.notifyDataSetChanged()
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                    Log.e(logTag, " getSceneResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.deleteSceneResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        viewModel.getScene(BodyGetScene("", ""))
                    } else {
                        DialogUtil.hideDialog()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                    Log.e(logTag, " deleteSceneResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.updateSceneStatusResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let { mContext ->
                        Toast.makeText(mContext, response.values.message, Toast.LENGTH_SHORT)
                    }

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        sceneData?.let { scene ->
                            val tempScene = sceneList.find { it == scene }
                            tempScene?.let { tempData ->
                                tempData.isDeviceDisable = sceneStatus
                                sceneList.set(sceneList.indexOf(tempData),tempData)
                            }
                        }
                    }

                    sceneStatus = 0
                    sceneData = null
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                    Log.e(
                        logTag,
                        " updateSceneStatusResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

}