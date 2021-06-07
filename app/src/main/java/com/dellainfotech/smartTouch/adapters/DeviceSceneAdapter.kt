package com.dellainfotech.smartTouch.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
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
    private var bodyScenes: ArrayList<BodySceneData>,
    private val deviceId: String,
    private val roomId: String
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

        holder.ibDelete.setOnClickListener {
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
        val ibDelete = itemView.findViewById(R.id.ib_delete) as ImageButton

    }

    private fun setSpinners(holder: MyViewHolder) {
        holder.apply {
            val deviceList = arrayListOf<GetDeviceData>()
            val switchList = arrayListOf<DeviceSwitchData>()
            val roomAdapter = RoomAdapter(mActivity, roomList)
            spinnerRoom.adapter = roomAdapter
            if (bodyScenes[adapterPosition].deviceSwitchId == "") {
                spinnerRoom.setSelection(roomAdapter.getPositionById(roomId))
            } else {
                spinnerRoom.setSelection(roomAdapter.getPositionById(bodyScenes[adapterPosition].roomId))
            }
            spinnerRoom.isEnabled = false
            spinnerDevice.isEnabled = false

            spinnerRoom.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val room = parent?.selectedItem as GetRoomData
                        bodyScenes[adapterPosition].roomId = room.id
                        for (roomData in roomDataList) {
                            if (roomData.id == room.id) {
                                roomData.deviceData?.let { devices ->
                                    deviceList.clear()
                                    deviceList.addAll(devices)
                                    val deviceAdapter = DeviceAdapter(mActivity, deviceList)
                                    spinnerDevice.adapter = deviceAdapter

                                    if (bodyScenes[adapterPosition].deviceId == "") {
                                        spinnerDevice.setSelection(
                                            deviceAdapter.getPositionById(
                                                deviceId
                                            )
                                        )
                                    } else {
                                        spinnerRoom.setSelection(
                                            roomAdapter.getPositionById(
                                                bodyScenes[adapterPosition].deviceId
                                            )
                                        )
                                    }


                                }
                                break
                            }
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
                        for (deviceData in deviceList) {
                            if (deviceData.id == device.id) {
                                deviceData.switchData?.let { switches ->
                                    switchList.clear()
                                    for (switch in switches) {
                                        if (switch.typeOfSwitch == 0) {
                                            switchList.add(switch)
                                        }
                                    }
                                    val switchAdapter =
                                        SwitchAdapter(mActivity, switchList)
                                    spinnerSwitch.adapter = switchAdapter

                                    if (bodyScenes[adapterPosition].deviceSwitchId != "") {
                                        spinnerSwitch.setSelection(
                                            switchAdapter.getPositionById(
                                                bodyScenes[adapterPosition].deviceSwitchId
                                            )
                                        )
                                    }
                                }
                                break
                            }
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
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            switch.setOnCheckedChangeListener { buttonView, isChecked ->
                bodyScenes[adapterPosition].deviceSwitchSettingValue = isChecked.toInt()
            }
        }
    }

    fun updateRoomList(controlModeList: List<ControlModeRoomData>) {
        roomDataList.addAll(controlModeList)
        for (roomData in roomDataList) {
            roomList.add(GetRoomData(roomData.id, null, roomData.userId, roomData.roomName, 1))
        }
    }

    fun getScenes(): ArrayList<BodySceneData> = bodyScenes

}