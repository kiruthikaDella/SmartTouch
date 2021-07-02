package com.dellainfotech.smartTouch.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.spinneradapter.DeviceAdapter
import com.dellainfotech.smartTouch.adapters.spinneradapter.RoomAdapter
import com.dellainfotech.smartTouch.adapters.spinneradapter.SwitchAdapter
import com.dellainfotech.smartTouch.api.body.BodySceneData
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.api.model.GetRoomData
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */
class DeviceSceneAdapter(
    private val mActivity: Activity,
    private var bodyScenes: ArrayList<BodySceneData>
) : RecyclerView.Adapter<DeviceSceneAdapter.MyViewHolder>() {

    private val roomDataList = arrayListOf<ControlModeRoomData>()
    private val logTag = this::class.java.simpleName

    private var roomList = arrayListOf<GetRoomData>()

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
                            bodyScenes.removeAt(position)
                            notifyDataSetChanged()
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
        }

        setSpinners(holder)
    }

    override fun getItemCount(): Int {
        return bodyScenes.size
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

            if (bodyScenes[adapterPosition].deviceSwitchId.isNotEmpty()) {
                spinnerRoom.setSelection(roomAdapter.getPositionById(bodyScenes[adapterPosition].roomId))
            } else {
                spinnerRoom.setSelection(0)
            }

            spinnerRoom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val room = parent?.selectedItem as GetRoomData
                    bodyScenes[adapterPosition].roomId = room.id

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
                                    mActivity.getString(R.string.text_select_device),
                                    0,
                                    0,
                                    0,
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
                                    mActivity.getString(R.string.text_no_device),
                                    0,
                                    0,
                                    0,
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
                                mActivity.getString(R.string.text_no_device),
                                0,
                                0,
                                0,
                                0,
                                null
                            )
                            deviceList.add(device)
                            spinnerDevice.isEnabled = false
                        }

                        val deviceAdapter = DeviceAdapter(mActivity, deviceList)
                        spinnerDevice.adapter = deviceAdapter

                        if (bodyScenes[adapterPosition].deviceId != "") {
                            spinnerDevice.setSelection(
                                deviceAdapter.getPositionById(
                                    bodyScenes[adapterPosition].deviceId
                                )
                            )
                        }

                    } else {

                        val device = GetDeviceData(
                            "",
                            "",
                            "",
                            "",
                            mActivity.getString(R.string.text_no_device),
                            0,
                            0,
                            0,
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
                        bodyScenes[adapterPosition].deviceId = device.id

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
                                    0,
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
                                    0,
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
                                0,
                                null
                            )
                            switchList.add(switch)
                            spinnerSwitch.isEnabled = false
                        }

                        val switchAdapter = SwitchAdapter(mActivity, switchList)
                        spinnerSwitch.adapter = switchAdapter
                        if (bodyScenes[adapterPosition].deviceSwitchId != "") {
                            spinnerSwitch.setSelection(
                                switchAdapter.getPositionById(
                                    bodyScenes[adapterPosition].deviceSwitchId
                                )
                            )
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
                        bodyScenes[adapterPosition].deviceSwitchId = switch.id
                        if (switch.name == mActivity.getString(R.string.text_no_switch) || switch.name == mActivity.getString(
                                R.string.text_select_switch
                            )
                        ) {
                            bodyScenes[adapterPosition].deviceSwitchId =
                                mActivity.getString(R.string.text_no_switch)
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            switch.setOnCheckedChangeListener { _, isChecked ->
                bodyScenes[adapterPosition].deviceSwitchSettingValue = isChecked.toInt()
            }
        }
    }

    fun addScene() {
        when {
            isDuplicateSwitchFound() -> {
                Toast.makeText(
                    mActivity,
                    mActivity.getString(R.string.error_text_duplicate_scene),
                    Toast.LENGTH_SHORT
                ).show()
            }
            isEmptySwitchInList() -> {
                Toast.makeText(
                    mActivity,
                    mActivity.getString(R.string.error_text_empty_switch),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                bodyScenes.add(BodySceneData("", "", "", 0))
                notifyItemChanged(bodyScenes.size)
            }
        }
    }

    fun updateRoomList(controlModeList: List<ControlModeRoomData>) {
        roomDataList.addAll(controlModeList)
        for (roomData in roomDataList) {
            roomList.add(GetRoomData(roomData.id, null, roomData.userId, roomData.roomName, 1))
        }
    }

    fun isDuplicateSwitchFound(): Boolean {
        val switchList = arrayListOf<String>()
        for (switch in bodyScenes) {
            switchList.add(switch.deviceSwitchId)
        }
        return switchList.size != switchList.distinct().count()
    }

    fun isEmptySwitchInList(): Boolean {
        return bodyScenes.find {
            it.deviceSwitchId.isEmpty() || it.deviceSwitchId == mActivity.getString(
                R.string.text_no_switch
            )
        } != null
    }

    fun getScenes(): ArrayList<BodySceneData> = bodyScenes

}