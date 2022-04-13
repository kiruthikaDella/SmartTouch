package com.voinismartiot.voni.ui.fragments.main.scenes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.ScenesAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyGetScene
import com.voinismartiot.voni.api.body.BodyUpdateSceneStatus
import com.voinismartiot.voni.api.model.ControlModeRoomData
import com.voinismartiot.voni.api.model.GetSceneData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.databinding.FragmentSceneBinding
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SceneFragment : BaseFragment<HomeViewModel, FragmentSceneBinding, HomeRepository>() {

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
                activity?.askAlert(
                    getString(R.string.dialog_title_delete_scene),
                    getString(R.string.text_yes),
                    getString(R.string.text_no),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            if (!Utils.isNetworkConnectivityAvailable()) {
                                context?.showToast(getString(R.string.text_no_internet_available))
                            } else {
                                activity?.loadingDialog()
                                viewModel.deleteScene(data.id)
                            }
                        }

                        override fun onNoClicked() = Unit

                    }
                )
            }

        })

        sceneAdapter.setOnSwitchClickListener(object :
            ScenesAdapter.SwitchItemClickListener<GetSceneData> {
            override fun onItemClick(data: GetSceneData, sceneStatus: Int) {

                if (!Utils.isNetworkConnectivityAvailable()) {
                    context?.showToast(getString(R.string.text_no_internet_available))
                } else {
                    activity?.loadingDialog()
                    this@SceneFragment.sceneStatus = sceneStatus
                    sceneData = data
                    viewModel.updateSceneStatus(
                        data.id,
                        BodyUpdateSceneStatus(this@SceneFragment.sceneStatus)
                    )
                }

            }

        })

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                activity?.loadingDialog()
                viewModel.getScene(BodyGetScene("", ""))
                viewModel.getControl()
            }
        }

        binding.pullToRefresh.setOnRefreshListener {
            sceneList.clear()
            sceneAdapter.notifyDataSetChanged()
            viewModel.getScene(BodyGetScene("", ""))
            viewModel.getControl()
        }

        apiResponse()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSceneBinding = FragmentSceneBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun apiResponse() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getControlResponse.collectLatest { response ->
                        controlModeRoomData.clear()
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { roomDataList ->
                                        controlModeRoomData.addAll(roomDataList)
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " getControlResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.getSceneResponse.collectLatest { response ->
                        sceneList.clear()
                        binding.pullToRefresh.isRefreshing = response is Resource.Loading
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let {
                                        sceneList.addAll(it)
                                    }
                                }
                                sceneAdapter.notifyDataSetChanged()
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " getSceneResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.deleteSceneResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    viewModel.getScene(BodyGetScene("", ""))
                                } else {
                                    hideDialog()
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " deleteSceneResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.updateSceneStatusResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)

                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    sceneData?.let { scene ->
                                        val tempScene = sceneList.find { it == scene }
                                        tempScene?.let { tempData ->
                                            tempData.isDeviceDisable = sceneStatus
                                            sceneList.set(sceneList.indexOf(tempData), tempData)
                                        }
                                    }
                                }

                                sceneStatus = 0
                                sceneData = null
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " updateSceneStatusResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }

    }

}