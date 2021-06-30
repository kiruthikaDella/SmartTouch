package com.dellainfotech.smartTouch.ui.fragments.main.scenes

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.DeviceSceneAdapter
import com.dellainfotech.smartTouch.adapters.UpdateDeviceSceneAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddScene
import com.dellainfotech.smartTouch.api.body.BodySceneData
import com.dellainfotech.smartTouch.api.body.BodyUpdateScene
import com.dellainfotech.smartTouch.api.model.GetSceneData
import com.dellainfotech.smartTouch.api.model.Scene
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogEditListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
import com.dellainfotech.smartTouch.databinding.FragmentCreateSceneBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import org.json.JSONObject
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jignesh Dangar on 23-04-2021.
 */

@Suppress("DEPRECATION")
class CreateSceneFragment :
    ModelBaseFragment<HomeViewModel, FragmentCreateSceneBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: CreateSceneFragmentArgs by navArgs()
    private lateinit var deviceSceneAdapter: DeviceSceneAdapter
    private lateinit var updateDeviceSceneAdapter: UpdateDeviceSceneAdapter
    private val createScenesList = arrayListOf<BodySceneData>()
    private var isUpdatingScene: Boolean = false
    private var itemPosition: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
                    createScenesList,
                    "",
                    ""
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
            ).takeLast(2).toLowerCase(Locale.getDefault())
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

        binding.tvDaily.setOnClickListener {

            context?.let { ctx ->
                val popup = PopupMenu(ctx, binding.tvDaily)
                popup.menuInflater.inflate(R.menu.scene_frequency_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    binding.tvDaily.text = item.title
                    true
                }
                popup.show()
            }

        }

        binding.tvAdd.setOnClickListener {
            if (isUpdatingScene) {
                updateDeviceSceneAdapter.addScene()
            } else {
                createScenesList.add(BodySceneData("", "", "", 0))
                deviceSceneAdapter.notifyItemInserted(createScenesList.size)
            }
        }

        binding.ivEditCreateScene.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it,
                    "Edit Scene name",
                    binding.edtSceneName.text.toString().trim(),
                    getString(R.string.text_save),
                    getString(
                        R.string.text_cancel
                    ),
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            if (string.isEmpty()) {
                                Toast.makeText(
                                    it,
                                    "Scene name must not be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                DialogUtil.hideDialog()
                                binding.edtSceneName.text = string.toEditable()
                            }
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
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
                            ).takeLast(2).toLowerCase(Locale.getDefault())
                        }</font>"
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            binding.tvTime.text = Html.fromHtml(time)
                        }
                    },
                    hour,
                    minute,
                    false
                )

                mTimePicker.show()
            }

        }

        binding.ibSave.isEnabled = false
        binding.ibSave.setOnClickListener {
            val sceneName = binding.edtSceneName.text.toString().trim()
            val sceneTime = binding.tvTime.text.toString()
            val sceneFrequency = binding.tvDaily.text.toString()
            when {
                sceneName.isEmpty() -> {
                    context?.let {
                        Toast.makeText(
                            it,
                            getString(R.string.error_text_scene_name_empty),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                sceneName.length < 3 -> {
                    context?.let {
                        Toast.makeText(
                            it,
                            getString(R.string.error_text_scene_name_length),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else -> {
                    if (isUpdatingScene) {
                        if (updateDeviceSceneAdapter.isDuplicateSwitchFound()) {
                            context?.let { mContext ->
                                Toast.makeText(
                                    mContext,
                                    getString(R.string.error_text_duplicate_scene),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            activity?.let {
                                DialogUtil.loadingAlert(it)
                            }
                            Log.e(
                                logTag,
                                " BodyScene ${
                                    BodyUpdateScene(
                                        args.sceneDetail!!.id,
                                        sceneName,
                                        sceneTime,
                                        sceneFrequency,
                                        updateDeviceSceneAdapter.getScenes()
                                    )
                                }"
                            )
                            viewModel.updateScene(
                                BodyUpdateScene(
                                    args.sceneDetail!!.id,
                                    sceneName,
                                    sceneTime,
                                    sceneFrequency,
                                    updateDeviceSceneAdapter.getScenes()
                                )
                            )
                        }
                    } else {
                        if (deviceSceneAdapter.isDuplicateSwitchFound()) {
                            context?.let { mContext ->
                                Toast.makeText(
                                    mContext,
                                    getString(R.string.error_text_duplicate_scene),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            activity?.let {
                                DialogUtil.loadingAlert(it)
                            }
                            Log.e(
                                logTag,
                                " BodyScene ${
                                    BodyAddScene(
                                        sceneName,
                                        sceneTime,
                                        sceneFrequency,
                                        deviceSceneAdapter.getScenes()
                                    )
                                }"
                            )
                            viewModel.addScene(
                                BodyAddScene(
                                    sceneName,
                                    sceneTime,
                                    sceneFrequency,
                                    deviceSceneAdapter.getScenes()
                                )
                            )
                        }

                    }
                }
            }
        }
    }

    private fun apiResponse() {
        viewModel.addSceneResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        findNavController().navigateUp()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " addSceneResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.updateSceneResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()

                    activity?.runOnUiThread {
                        try {
                            val jsonObject = JSONObject(response.values.string())

                            println(" $logTag jsonObject $jsonObject")
                            if (jsonObject.getBoolean("status") && jsonObject.getInt("code") == Constants.API_SUCCESS_CODE) {
                                context?.let {
                                    Toast.makeText(
                                        it,
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                findNavController().navigateUp()
                                println(" $logTag success")
                            } else if (!jsonObject.getBoolean("status") && jsonObject.getInt("code") == 400) {
                                println(" $logTag fail")
                                val msgArray = jsonObject.getJSONArray("message")
                                if (msgArray.length() > 0) {
                                    println(" $logTag length() > 0")
                                    context?.let {
                                        Toast.makeText(
                                            it,
                                            msgArray.getJSONObject(0).getString("message"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " updateSceneResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.deleteSceneDetailResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        itemPosition?.let {
                            updateDeviceSceneAdapter.deleteScene(it)
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " deleteSceneDetailResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    private fun setSceneData(sceneData: GetSceneData) {
        binding.edtSceneName.text = sceneData.sceneName.toEditable()

        val time = "<font color='#1A8EFF'>${
            sceneData.sceneTime.dropLast(3)
        }</font><font color='#011B25'> ${
            sceneData.sceneTime.takeLast(2).toLowerCase(Locale.getDefault())
        }</font>"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvTime.text = Html.fromHtml(time)
        }

        binding.tvDaily.text = sceneData.sceneInterval

        activity?.let { mActivity ->
            sceneData.scene?.let {
                updateDeviceSceneAdapter = UpdateDeviceSceneAdapter(
                    mActivity,
                    it,
                    "",
                    ""
                )
                binding.recyclerScenes.adapter = updateDeviceSceneAdapter
                updateDeviceSceneAdapter.updateRoomList(args.controlModeList.toList())
                updateDeviceSceneAdapter.notifyDataSetChanged()

                updateDeviceSceneAdapter.setOnDeleteClickListener(object :
                    UpdateDeviceSceneAdapter.DeleteSceneItemClickListener<Scene> {
                    override fun onItemClick(data: Scene, scenePosition: Int) {
                        DialogUtil.loadingAlert(mActivity)
                        viewModel.deleteSceneDetail(data.id)
                        itemPosition = scenePosition
                    }

                })

            }
        }
    }

}