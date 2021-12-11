package com.voinismartiot.voni.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.spinneradapter.DeviceAdapter
import com.voinismartiot.voni.adapters.spinneradapter.RoomAdapter
import com.voinismartiot.voni.adapters.spinneradapter.SwitchAdapter
import com.voinismartiot.voni.api.body.BodyUpdateSceneData
import com.voinismartiot.voni.api.model.*
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */
class UpdateDeviceSceneAdapter(
    private val mActivity: Activity,
    private val scenes: ArrayList<Scene>
) : RecyclerView.Adapter<UpdateDeviceSceneAdapter.MyViewHolder>() {

    private val logTag = this::class.java.simpleName

    private val roomDataList = arrayListOf<ControlModeRoomData>()
    private var roomList = arrayListOf<GetRoomData>()
    private var errorList : MutableMap<Int,String> = mutableMapOf()

    private var deleteClickListener: DeleteSceneItemClickListener<Scene>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_scene, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {

            ibDelete.setOnClickListener {
                DialogUtil.askAlert(
                    mActivity, mActivity.getString(R.string.dialog_title_delete_scene),
                    mActivity.getString(R.string.text_yes),
                    mActivity.getString(R.string.text_no),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            if (scenes[position].id == "") {
                                deleteScene(position)
                            } else {
                                deleteClickListener?.onItemClick(
                                    scenes[position],
                                    position
                                )
                            }
                        }

                        override fun onNoClicked() {
                        }

                    }
                )
            }

            ivRoomName.setOnClickListener {
                if (spinnerRoom.isEnabled)
                    spinnerRoom.performClick()
            }

            ivDeviceName.setOnClickListener {
                if (spinnerDevice.isEnabled)
                    spinnerDevice.performClick()
            }

            ivSwitchName.setOnClickListener {
                if (spinnerSwitch.isEnabled)
                    spinnerSwitch.performClick()
            }

            if (errorList.isNotEmpty()){
                if (errorList.containsKey(position)){
                    tvError.text = errorList[position]
                    tvError.isVisible = true
                }else {
                    tvError.isVisible = false
                }
            }else {
                tvError.isVisible = false
            }
        }

        setSpinners(holder)
    }

    override fun getItemCount(): Int {
        return scenes.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spinnerRoom = itemView.findViewById(R.id.spinner_room_name) as Spinner
        val spinnerDevice = itemView.findViewById(R.id.spinner_device_name) as Spinner
        val spinnerSwitch = itemView.findViewById(R.id.spinner_switch_name) as Spinner
        val switch = itemView.findViewById(R.id.switch_status) as SwitchMaterial

        val ibDelete = itemView.findViewById(R.id.ib_delete) as ImageView
        val ivRoomName = itemView.findViewById(R.id.iv_room_name_down) as ImageView
        val ivDeviceName = itemView.findViewById(R.id.iv_device_name_down) as ImageView
        val ivSwitchName = itemView.findViewById(R.id.iv_switch_name_down) as ImageView

        val tvError = itemView.findViewById(R.id.tv_text_error) as TextView
    }

    private fun setSpinners(holder: MyViewHolder) {
        holder.apply {
            val deviceList = arrayListOf<GetDeviceData>()
            val switchList = arrayListOf<DeviceSwitchData>()

            if (roomList.isEmpty()) {
                val roomData =
                    GetRoomData("", null, "", mActivity.getString(R.string.text_no_room), 0)
                roomList.add(roomData)
            } else if (roomList[0].id.isNotEmpty() && roomList[0].roomName != mActivity.getString(R.string.text_select_room)) {
                val roomData =
                    GetRoomData("", null, "", mActivity.getString(R.string.text_select_room), 0)
                roomList.add(0, roomData)
            }

            val roomAdapter = RoomAdapter(mActivity, roomList)
            spinnerRoom.adapter = roomAdapter

            scenes[adapterPosition].roomData?.let { roomData ->
                spinnerRoom.setSelection(roomAdapter.getPositionById(roomData.id))
            }

            spinnerRoom.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val room = parent?.selectedItem as GetRoomData
                        scenes[adapterPosition].roomData = room

                        val roomData = roomDataList.find { it.id == room.id }
                        deviceList.clear()

                        if (roomData != null) {
                            roomData.deviceData?.let { devices ->
                                if (devices.isNotEmpty()) {
                                    spinnerDevice.isEnabled = true
                                    val device = GetDeviceData(
                                        "",
                                        "",
                                        "",
                                        "",
                                        "0",
                                        mActivity.getString(R.string.text_select_device),
                                        0,
                                        0,
                                        "0",
                                        0,
                                        null
                                    )
                                    deviceList.add(device)
                                    deviceList.addAll(devices)
                                } else {
                                    val device = GetDeviceData(
                                        "",
                                        "",
                                        "",
                                        "",
                                        "0",
                                        mActivity.getString(R.string.text_no_device),
                                        0,
                                        0,
                                        "0",
                                        0,
                                        null
                                    )
                                    deviceList.add(device)
                                    spinnerDevice.isEnabled = false
                                }
                            } ?: kotlin.run {
                                val device = GetDeviceData(
                                    "",
                                    "",
                                    "",
                                    "",
                                    "0",
                                    mActivity.getString(R.string.text_no_device),
                                    0,
                                    0,
                                    "0",
                                    0,
                                    null
                                )
                                deviceList.add(device)
                                spinnerDevice.isEnabled = false
                            }

                            val deviceAdapter = DeviceAdapter(mActivity, deviceList)
                            spinnerDevice.adapter = deviceAdapter

                            scenes[adapterPosition].deviceData?.let {
                                spinnerDevice.setSelection(
                                    deviceAdapter.getPositionById(
                                        it.id
                                    )
                                )
                            }

                        } else {

                            val device = GetDeviceData(
                                "",
                                "",
                                "",
                                "",
                                "0",
                                mActivity.getString(R.string.text_no_device),
                                0,
                                0,
                                "0",
                                0,
                                null
                            )
                            deviceList.add(device)
                            spinnerDevice.isEnabled = false

                            val deviceAdapter = DeviceAdapter(mActivity, deviceList)
                            spinnerDevice.adapter = deviceAdapter

                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            spinnerDevice.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val device = parent?.selectedItem as GetDeviceData
                        scenes[adapterPosition].deviceData = device

                        val deviceData = deviceList.find { it.id == device.id }
                        switchList.clear()

                        if (deviceData != null) {
                            deviceData.switchData?.let { switchData ->
                                val switch = DeviceSwitchData(
                                    "",
                                    0,
                                    "",
                                    mActivity.getString(R.string.text_select_switch),
                                    "",
                                    "0",
                                    null
                                )
                                switchList.add(switch)
                                switchList.addAll(switchData.filter { it.typeOfSwitch == 0 })
                                spinnerSwitch.isEnabled = true

                            } ?: kotlin.run {
                                switchList.clear()
                                val switch = DeviceSwitchData(
                                    "",
                                    0,
                                    "",
                                    mActivity.getString(R.string.text_no_switch),
                                    "",
                                    "0",
                                    null
                                )
                                switchList.add(switch)
                                spinnerSwitch.isEnabled = false
                            }

                        } else {
                            val switch = DeviceSwitchData(
                                "",
                                0,
                                "",
                                mActivity.getString(R.string.text_no_switch),
                                "",
                                "0",
                                null
                            )
                            switchList.add(switch)
                            spinnerSwitch.isEnabled = false
                        }

                        val switchAdapter = SwitchAdapter(mActivity, switchList)
                        spinnerSwitch.adapter = switchAdapter

                        scenes[adapterPosition].switchData?.id?.let {
                            spinnerSwitch.setSelection(switchAdapter.getPositionById(it))
                        }


                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            spinnerSwitch.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val switch = parent?.selectedItem as DeviceSwitchData
                        scenes[adapterPosition].switchData = switch
                        if (switch.name == mActivity.getString(R.string.text_no_switch) || switch.name == mActivity.getString(
                                R.string.text_select_switch
                            )
                        ) {
                            scenes[adapterPosition].switchData?.id =
                                mActivity.getString(R.string.text_no_switch)
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            switch.isChecked = scenes[adapterPosition].deviceSwitchSettingValue.toBoolean()

            switch.setOnCheckedChangeListener { _, isChecked ->
                scenes[adapterPosition].deviceSwitchSettingValue = isChecked.toInt()
            }


        }
    }

    fun deleteScene(position: Int) {
        scenes.removeAt(position)
        notifyDataSetChanged()
    }

    fun addScene() {

        when {
            isDuplicateSwitchFound() -> {
                Toast.makeText(
                    mActivity, mActivity.getString(R.string.error_text_duplicate_scene),
                    Toast.LENGTH_SHORT
                ).show()
            }
            isEmptySwitchInList() -> {
                Toast.makeText(
                    mActivity, mActivity.getString(R.string.error_text_empty_switch),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                scenes.add(Scene("", null, null, null, 0))
                notifyItemChanged(scenes.size)
            }
        }
    }

    fun updateRoomList(controlModeList: List<ControlModeRoomData>) {
        roomDataList.addAll(controlModeList)
        for (roomData in roomDataList) {
            roomList.add(GetRoomData(roomData.id, null, roomData.userId, roomData.roomName, 1))
        }
    }

    fun getScenes(): ArrayList<BodyUpdateSceneData> {
        val array = arrayListOf<BodyUpdateSceneData>()
        for (scene in scenes) {
            if (scene.roomData != null && scene.deviceData != null && scene.switchData != null) {
                array.add(
                    BodyUpdateSceneData(
                        scene.roomData!!.id,
                        scene.deviceData!!.id,
                        scene.id,
                        scene.switchData!!.id,
                        scene.deviceSwitchSettingValue
                    )
                )
            }
        }
        return array
    }

    fun isDuplicateSwitchFound(): Boolean {

        val scenes = getScenes()

        val switchList = arrayListOf<String>()
        for (switch in scenes) {
            switchList.add(switch.deviceSwitchId)
        }

        return switchList.size != switchList.distinct().count()
    }

    fun isEmptySwitchInList(): Boolean {
        return getScenes().find {
            it.deviceSwitchId.isEmpty() || it.deviceSwitchId == mActivity.getString(
                R.string.text_no_switch
            )
        } != null
    }

    interface DeleteSceneItemClickListener<T> {
        fun onItemClick(data: T, scenePosition: Int)
    }

    fun setOnDeleteClickListener(listener: DeleteSceneItemClickListener<Scene>) {
        this.deleteClickListener = listener
    }

    fun setError(errorData: List<ErrorSceneData>){
        errorList.clear()
        for (error in errorData){
            for ((index, scene) in getScenes().withIndex()){
                if (scene.deviceSwitchId == error.deviceSwitchId){
                    errorList[index] = error.message
                }
            }
        }

        Log.e(logTag, " errorList $errorList ")
        notifyDataSetChanged()
    }
}