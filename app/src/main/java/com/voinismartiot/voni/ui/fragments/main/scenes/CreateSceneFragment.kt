package com.voinismartiot.voni.ui.fragments.main.scenes

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.DeviceSceneAdapter
import com.voinismartiot.voni.adapters.UpdateDeviceSceneAdapter
import com.voinismartiot.voni.adapters.WeeklyDaysAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyAddScene
import com.voinismartiot.voni.api.body.BodySceneData
import com.voinismartiot.voni.api.body.BodyUpdateScene
import com.voinismartiot.voni.api.model.GetSceneData
import com.voinismartiot.voni.api.model.Scene
import com.voinismartiot.voni.api.model.WeeklyDaysModel
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogEditListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.common.utils.Utils.toEditable
import com.voinismartiot.voni.databinding.FragmentCreateSceneBinding
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class CreateSceneFragment :
    BaseFragment<HomeViewModel, FragmentCreateSceneBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: CreateSceneFragmentArgs by navArgs()
    private lateinit var deviceSceneAdapter: DeviceSceneAdapter
    private lateinit var updateDeviceSceneAdapter: UpdateDeviceSceneAdapter
    private lateinit var weeklyDaysAdapter: WeeklyDaysAdapter
    private val createScenesList = arrayListOf<BodySceneData>()
    private val daysList = arrayListOf<WeeklyDaysModel>()
    private var isUpdatingScene: Boolean = false
    private var itemPosition: Int? = null
    private var schedulerTime: Long = Calendar.getInstance().timeInMillis / 1000

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daysList.add(WeeklyDaysModel(getString(R.string.text_sunday), false))
        daysList.add(WeeklyDaysModel(getString(R.string.text_monday), false))
        daysList.add(WeeklyDaysModel(getString(R.string.text_tuesday), false))
        daysList.add(WeeklyDaysModel(getString(R.string.text_wednesday), false))
        daysList.add(WeeklyDaysModel(getString(R.string.text_thursday), false))
        daysList.add(WeeklyDaysModel(getString(R.string.text_friday), false))
        daysList.add(WeeklyDaysModel(getString(R.string.text_saturday), false))

        weeklyDaysAdapter = WeeklyDaysAdapter(daysList)
        binding.layoutFrequencyWeekly.rvDays.adapter = weeklyDaysAdapter

        setTime()
        clickEvents()
        apiResponse()
        args.sceneDetail?.let {
            isUpdatingScene = true
            setSceneData(it)
        } ?: kotlin.run {
            activity?.let {
                deviceSceneAdapter = DeviceSceneAdapter(
                    it,
                    createScenesList
                )
                deviceSceneAdapter.updateRoomList(args.controlModeList.toList())
                binding.recyclerScenes.adapter = deviceSceneAdapter
            }
        }
    }

    private fun setTime() {
        val formatter = SimpleDateFormat("hh:mm a", Locale.US)
        val time = "<font color='#1A8EFF'>${
            formatter.format(Calendar.getInstance().time).dropLast(3)
        }</font><font color='#011B25'> ${
            formatter.format(
                Calendar.getInstance().time
            ).takeLast(2).lowercase(Locale.getDefault())
        }</font>"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvTime.text = Html.fromHtml(time)
        }
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateSceneBinding = FragmentCreateSceneBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun clickEvents() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutSlidingUpPanel.setFadeOnClickListener { hidePanel() }

        binding.layoutFrequencyWeekly.ivHidePanel.setOnClickListener {
            hidePanel()
        }

        binding.tvInterval.setOnClickListener {

            context?.let { ctx ->
                val popup = PopupMenu(ctx, binding.tvInterval)
                popup.menuInflater.inflate(R.menu.scene_frequency_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->

                    when (item.itemId) {
                        R.id.action_daily -> {
                            binding.tvInterval.text = item.title
                        }
                        R.id.action_weekly -> {
                            showPanel()
                        }
                    }

                    true
                }
                popup.show()
            }

        }

        binding.linearAdd.setOnClickListener {
            if (isUpdatingScene) {
                updateDeviceSceneAdapter.addScene()
                binding.recyclerScenes.smoothScrollToPosition(updateDeviceSceneAdapter.itemCount)
            } else {
                deviceSceneAdapter.addScene()
                binding.recyclerScenes.smoothScrollToPosition(createScenesList.size - 1)
            }
        }

        binding.ivEditCreateScene.setOnClickListener {
            activity?.editDialog(
                "Edit Scene name",
                binding.edtSceneName.text.toString().trim(),
                getString(R.string.text_save),
                getString(
                    R.string.text_cancel
                ),
                onClick = object : DialogEditListener {
                    override fun onYesClicked(string: String) {
                        if (string.isEmpty()) {
                            activity?.showToast("Scene name must not be empty!")
                        } else {
                            hideDialog()
                            binding.edtSceneName.text = string.toEditable()
                        }
                    }

                    override fun onNoClicked() {
                        hideDialog()
                    }

                }
            )
        }

        binding.tvTime.setOnClickListener {
            context?.let { mContext ->
                val mTimePicker: TimePickerDialog
                val mCurrentTime = Calendar.getInstance()
                val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mCurrentTime.get(Calendar.MINUTE)

                mTimePicker = TimePickerDialog(
                    mContext,
                    { _, hourOfDay, min ->
                        val cal = Calendar.getInstance()
                        cal[Calendar.HOUR_OF_DAY] = hourOfDay
                        cal[Calendar.MINUTE] = min

                        val formatter: Format
                        formatter = SimpleDateFormat("hh:mm a", Locale.US)
                        val time = "<font color='#1A8EFF'>${
                            formatter.format(cal.time).dropLast(3)
                        }</font><font color='#011B25'> ${
                            formatter.format(
                                cal.time
                            ).takeLast(2).lowercase(Locale.getDefault())
                        }</font>"
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            binding.tvTime.text = Html.fromHtml(time)
                        }

                        cal.timeZone = TimeZone.getTimeZone("gmt")
                        schedulerTime = cal.timeInMillis / 1000
                    },
                    hour,
                    minute,
                    false
                )

                mTimePicker.show()
            }

        }

        binding.ibSave.setOnClickListener {
            val sceneName = binding.edtSceneName.text.toString().trim()
            val sceneTime = binding.tvTime.text.toString()
            val sceneFrequency = binding.tvInterval.text.toString().lowercase(Locale.getDefault())
            when {
                sceneName.isEmpty() -> {
                    context?.showToast(getString(R.string.error_text_scene_name_empty))
                }
                sceneName.length < 3 -> {
                    context?.showToast(getString(R.string.error_text_scene_name_length))
                }
                else -> {

                    if (!Utils.isNetworkConnectivityAvailable()) {
                        context?.showToast(getString(R.string.text_no_internet_available))
                        return@setOnClickListener
                    }

                    if (isUpdatingScene) {

                        when {
                            updateDeviceSceneAdapter.isEmptySwitchInList() -> {
                                context?.showToast(getString(R.string.error_text_empty_switch))
                            }
                            updateDeviceSceneAdapter.isDuplicateSwitchFound() -> {
                                context?.showToast(getString(R.string.error_text_duplicate_scene))
                            }
                            else -> {
                                activity?.loadingDialog()
                                val weeklyDayList = weeklyDaysAdapter.getDayList()
                                if (sceneFrequency != getString(R.string.text_weekly).lowercase()) {
                                    weeklyDayList.clear()
                                }
//                                Log.e(logTag, " updateDeviceSceneAdapter sss ${updateDeviceSceneAdapter.getScenes()} ")
                                viewModel.updateScene(
                                    BodyUpdateScene(
                                        args.sceneDetail!!.id,
                                        sceneName,
                                        sceneTime,
                                        TimeZone.getDefault().id,
                                        sceneFrequency,
                                        schedulerTime,
                                        weeklyDayList,
                                        updateDeviceSceneAdapter.getScenes()
                                    )
                                )
                            }
                        }
                    } else {
                        when {
                            deviceSceneAdapter.isDuplicateSwitchFound() -> {
                                context?.showToast(getString(R.string.error_text_duplicate_scene))
                            }
                            deviceSceneAdapter.isEmptySwitchInList() -> {
                                context?.showToast(getString(R.string.error_text_empty_switch))
                            }
                            else -> {
                                activity?.loadingDialog()

                                val weeklyDayList = weeklyDaysAdapter.getDayList()
                                if (sceneFrequency != getString(R.string.text_weekly).lowercase()) {
                                    weeklyDayList.clear()
                                }
                                Log.e(logTag, " deviceSceneAdapter sss ${deviceSceneAdapter.getScenes()} ")
                                viewModel.addScene(
                                    BodyAddScene(
                                        sceneName,
                                        sceneTime,
                                        TimeZone.getDefault().id,
                                        sceneFrequency,
                                        schedulerTime,
                                        weeklyDayList,
                                        deviceSceneAdapter.getScenes()
                                    )
                                )
                            }
                        }

                    }
                }
            }
        }

        binding.layoutFrequencyWeekly.btnSave.setOnClickListener {

            if (weeklyDaysAdapter.getDayList().size <= 0) {
                context?.showToast(getString(R.string.error_text_empty_weekly_days))
            } else {
                if (weeklyDaysAdapter.getDayList().size >= 7) {
                    binding.tvInterval.text = getString(R.string.text_daily)
                } else {
                    binding.tvInterval.text = getString(R.string.text_weekly)
                }
                hidePanel()
            }
        }
    }

    private fun showPanel() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }, 600)
    }

    private fun hidePanel() {
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    private fun apiResponse() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.addSceneResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    findNavController().navigateUp()
                                } else if (!response.values.status && response.values.code == Constants.API_FAILURE_CODE) {
                                    response.values.errorData?.let { errorData ->
                                        deviceSceneAdapter.setError(errorData)
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " addSceneResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.updateSceneResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    findNavController().navigateUp()
                                } else if (!response.values.status && response.values.code == Constants.API_FAILURE_CODE) {
                                    response.values.errorData?.let { errorData ->
                                        updateDeviceSceneAdapter.setError(errorData)
                                    }
                                }

                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " updateSceneResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.deleteSceneDetailResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    itemPosition?.let {
                                        updateDeviceSceneAdapter.deleteScene(it)
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " deleteSceneDetailResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }

        }


    }

    private fun setSceneData(sceneData: GetSceneData) {
        schedulerTime = sceneData.schedulerTime
        binding.edtSceneName.text = sceneData.sceneName.toEditable()

        val time =
            "<font color='#1A8EFF'>${sceneData.sceneTime.dropLast(3)}</font><font color='#011B25'> ${
                sceneData.sceneTime.takeLast(2).lowercase(Locale.getDefault())
            }</font>"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvTime.text = Html.fromHtml(time)
        }

        binding.tvInterval.text = sceneData.sceneInterval.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        activity?.let { mActivity ->
            sceneData.scene?.let {
                updateDeviceSceneAdapter = UpdateDeviceSceneAdapter(
                    mActivity,
                    it
                )
                binding.recyclerScenes.adapter = updateDeviceSceneAdapter
                updateDeviceSceneAdapter.updateRoomList(args.controlModeList.toList())
                updateDeviceSceneAdapter.notifyDataSetChanged()

                updateDeviceSceneAdapter.setOnDeleteClickListener(object :
                    UpdateDeviceSceneAdapter.DeleteSceneItemClickListener<Scene> {
                    override fun onItemClick(data: Scene, scenePosition: Int) {
                        mActivity.loadingDialog()
                        viewModel.deleteSceneDetail(args.sceneDetail!!.id, data.id)
                        itemPosition = scenePosition
                    }

                })

            }

            daysList.clear()
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_sunday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_sunday).take(3))
                        ?: false
                )
            )
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_monday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_monday).take(3))
                        ?: false
                )
            )
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_tuesday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_tuesday).take(3))
                        ?: false
                )
            )
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_wednesday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_wednesday).take(3))
                        ?: false
                )
            )
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_thursday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_thursday).take(3))
                        ?: false
                )
            )
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_friday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_friday).take(3))
                        ?: false
                )
            )
            daysList.add(
                WeeklyDaysModel(
                    getString(R.string.text_saturday),
                    sceneData.sceneIntervalValue?.contains(getString(R.string.text_saturday).take(3))
                        ?: false
                )
            )
        }
    }

}