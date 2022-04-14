package com.voinismartiot.voni.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.spinneradapter.DeviceAdapter
import com.voinismartiot.voni.adapters.spinneradapter.RoomAdapter
import com.voinismartiot.voni.adapters.spinneradapter.SwitchAdapter
import com.voinismartiot.voni.api.body.BodySceneData
import com.voinismartiot.voni.api.model.*
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.common.utils.askAlert
import com.voinismartiot.voni.customviews.CustomSpinner


class DeviceSceneAdapter(
    private val mActivity: Activity,
    private var bodyScenes: ArrayList<BodySceneData>
) : RecyclerView.Adapter<DeviceSceneAdapter.MyViewHolder>() {

    private val roomDataList = arrayListOf<ControlModeRoomData>()
    private var switchType = ""

    private var roomList = arrayListOf<GetRoomData>()
    private var errorList: MutableMap<Int, String> = mutableMapOf()

    private val deviceList = arrayListOf<GetDeviceData>()
    private val switchList = arrayListOf<DeviceSwitchData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_scene, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            ibDelete.setOnClickListener {
                mActivity.askAlert(
                    mActivity.getString(R.string.dialog_title_delete_scene),
                    mActivity.getString(R.string.text_yes),
                    mActivity.getString(R.string.text_no),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            bodyScenes.removeAt(adapterPosition)
                            notifyDataSetChanged()
                        }

                        override fun onNoClicked() = Unit

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

            tvError.isVisible = false
            if (errorList.isNotEmpty() && errorList.containsKey(adapterPosition)) {
                tvError.text = errorList[adapterPosition]
                tvError.isVisible = true
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
        val spinnerSwitch = itemView.findViewById(R.id.spinner_switch_name) as CustomSpinner
        val switchStatus = itemView.findViewById(R.id.switch_status) as SwitchMaterial

        val ibDelete = itemView.findViewById(R.id.ib_delete) as ImageView
        val ivRoomName = itemView.findViewById(R.id.iv_room_name_down) as ImageView
        val ivDeviceName = itemView.findViewById(R.id.iv_device_name_down) as ImageView
        val ivSwitchName = itemView.findViewById(R.id.iv_switch_name_down) as ImageView

        val tvError = itemView.findViewById(R.id.tv_text_error) as TextView

    }

    private fun setSpinners(holder: MyViewHolder) {
        holder.apply {

            if (roomList.isEmpty()) {
                roomList.add(
                    GetRoomData(
                        "",
                        null,
                        "",
                        mActivity.getString(R.string.text_no_room),
                        0
                    )
                )
            } else if (roomList[0].roomName != mActivity.getString(R.string.text_select_room)) {
                roomList.add(
                    0,
                    GetRoomData("", null, "", mActivity.getString(R.string.text_select_room), 0)
                )
            }

            val roomAdapter = RoomAdapter(mActivity, roomList)
            spinnerRoom.adapter = roomAdapter

            if (bodyScenes[adapterPosition].deviceSwitchId.isNotEmpty()) {
                spinnerRoom.setSelection(roomAdapter.getPositionById(bodyScenes[adapterPosition].roomId))
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
                    spinnerDevice.isEnabled = roomData?.deviceData?.isNotEmpty() ?: false

                    deviceList.add(
                        GetDeviceData(
                            "",
                            "",
                            "",
                            "",
                            "0",
                            ArrayList(),
                            if (roomData?.deviceData?.isNotEmpty() == true) mActivity.getString(R.string.text_select_device) else mActivity.getString(
                                R.string.text_no_device
                            ),
                            0,
                            0,
                            "0",
                            0,
                            null
                        )
                    )
                    deviceList.addAll(if (roomData?.deviceData?.isNotEmpty() == true) roomData.deviceData!! else emptyList())

                    val deviceAdapter = DeviceAdapter(mActivity, deviceList)
                    spinnerDevice.adapter = deviceAdapter

                    roomData?.let {
                        spinnerDevice.setSelection(
                            deviceAdapter.getPositionById(
                                bodyScenes[adapterPosition].deviceId
                            )
                        )
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

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

                        spinnerSwitch.isEnabled = deviceData?.switchData?.isNotEmpty() ?: false

                        switchList.add(
                            DeviceSwitchData(
                                "",
                                0,
                                "",
                                if (deviceData?.switchData?.isNotEmpty() == true) mActivity.getString(
                                    R.string.text_select_switch
                                ) else mActivity.getString(
                                    R.string.text_no_switch
                                ),
                                "",
                                "0",
                                null
                            )
                        )

                        deviceData?.switchData?.let { switchData ->
                            switchList.addAll(switchData.filter {
                                it.typeOfSwitch == 0
                            })

                        }

                        val switchAdapter = SwitchAdapter(mActivity, switchList)
                        spinnerSwitch.adapter = switchAdapter
                        spinnerSwitch.setSelection(
                            switchAdapter.getPositionById(
                                bodyScenes[adapterPosition].deviceSwitchId
                            )
                        )

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                }

            spinnerSwitch.setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                override fun onSpinnerOpened(spinner: AppCompatSpinner?) {

                    val filteredSwitchList = arrayListOf<DeviceSwitchData>()
                    val size = spinner?.adapter?.count ?: 0

                    for (i in 0 until size) {
                        filteredSwitchList.add(spinner!!.adapter!!.getItem(i) as DeviceSwitchData)
                    }

                    val abc = arrayListOf<DeviceSwitchData>()
                    abc.addAll(filteredSwitchList.filter { !bodyScenes.any { obj -> obj.deviceSwitchId == it.id } })
                    if ((spinnerSwitch.selectedItem as DeviceSwitchData).name != mActivity.getString(
                            R.string.text_select_switch
                        )
                    ) {
                        abc.add(spinnerSwitch.selectedItem as DeviceSwitchData)
                    }

                    val switchAdapter = SwitchAdapter(mActivity, abc)
                    switchAdapter.notifyDataSetChanged()
                    spinnerSwitch.adapter = switchAdapter


                }

                override fun onSpinnerClosed(spinner: AppCompatSpinner?) = Unit
            })


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
                        switchType = switch.desc?.lowercase() ?: ""
                        if (switch.name == mActivity.getString(R.string.text_no_switch) || switch.name == mActivity.getString(
                                R.string.text_select_switch
                            )
                        ) {
                            bodyScenes[adapterPosition].deviceSwitchId =
                                mActivity.getString(R.string.text_no_switch)
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                }

            switchStatus.setOnCheckedChangeListener { _, isChecked ->
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

    fun setError(errorData: List<ErrorSceneData>) {
        errorList.clear()
        for (error in errorData) {
            for ((index, scene) in getScenes().withIndex()) {
                if (scene.deviceSwitchId == error.deviceSwitchId) {
                    errorList[index] = error.message
                }
            }
        }

        notifyDataSetChanged()
    }

}