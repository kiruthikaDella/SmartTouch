package com.voinismartiot.voni.adapters

import android.app.Activity
import android.util.Log
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
import com.voinismartiot.voni.api.body.BodyUpdateSceneData
import com.voinismartiot.voni.api.model.*
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.common.utils.askAlert
import com.voinismartiot.voni.customviews.CustomSpinner

class UpdateDeviceSceneAdapter(
    private val mActivity: Activity,
    private val scenes: ArrayList<Scene>
) : RecyclerView.Adapter<UpdateDeviceSceneAdapter.MyViewHolder>() {

    private val roomDataList = arrayListOf<ControlModeRoomData>()
    private var roomList = arrayListOf<GetRoomData>()
    private var errorList: MutableMap<Int, String> = mutableMapOf()
    private var switchType = ""

    private val logTag = this::class.java.simpleName

    private var deleteClickListener: DeleteSceneItemClickListener<Scene>? = null

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
                            if (scenes[adapterPosition].id == "") {
                                deleteScene(adapterPosition)
                                return
                            }

                            deleteClickListener?.onItemClick(
                                scenes[adapterPosition],
                                adapterPosition
                            )
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
        return scenes.size
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
            val deviceList = arrayListOf<GetDeviceData>()
            val switchList = arrayListOf<DeviceSwitchData>()

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
            } else if (roomList[0].id.isNotEmpty() && roomList[0].roomName != mActivity.getString(R.string.text_select_room)) {
                roomList.add(
                    0,
                    GetRoomData("", null, "", mActivity.getString(R.string.text_select_room), 0)
                )
            }

            val roomAdapter = RoomAdapter(mActivity, roomList)
            spinnerRoom.adapter = roomAdapter

            scenes[adapterPosition].roomId?.let { roomData ->
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
                        scenes[adapterPosition].roomId = RoomId(room.id, room.roomName)

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
                                if (roomData?.deviceData?.isNotEmpty() == true) mActivity.getString(
                                    R.string.text_select_device
                                ) else mActivity.getString(
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

                        scenes[adapterPosition].deviceId?.let {
                            val devicePosition = deviceAdapter.getPositionById(it.id)
                            spinnerDevice.setSelection(if (devicePosition >= 0) devicePosition else 0)
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
                        scenes[adapterPosition].deviceId = DeviceId(device.id, device.deviceName)

                        val deviceData = deviceList.find { it.id == device.id }
                        switchList.clear()

                        spinnerSwitch.isEnabled = deviceData?.switchData?.isNotEmpty() ?: false

                        val switch = DeviceSwitchData(
                            "",
                            0,
                            "",
                            if (deviceData?.switchData?.isNotEmpty() == true) mActivity.getString(R.string.text_select_switch) else mActivity.getString(
                                R.string.text_no_switch
                            ),
                            "",
                            "0",
                            null
                        )
                        switchList.add(switch)
                        deviceData?.switchData?.let { switchData ->
                            switchList.addAll(switchData.filter {
                                it.typeOfSwitch == 0
                            })
                        }


                        val switchAdapter = SwitchAdapter(mActivity, switchList)
                        spinnerSwitch.adapter = switchAdapter

                        scenes[adapterPosition].deviceSwitchId?.id?.let {
                            spinnerSwitch.setSelection(switchAdapter.getPositionById(it))
                        }


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
                    abc.addAll(filteredSwitchList.filter { !scenes.any { obj -> obj.deviceSwitchId?.id == it.id } })

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
                        scenes[adapterPosition].deviceSwitchId =
                            DeviceSwitchId(switch.id, switch.name)

                        switchType = switch.desc?.lowercase() ?: ""

                        if (switch.name == mActivity.getString(R.string.text_no_switch) || switch.name == mActivity.getString(
                                R.string.text_select_switch
                            )
                        ) {
                            scenes[adapterPosition].deviceSwitchId?.id =
                                mActivity.getString(R.string.text_no_switch)
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                }

            switchStatus.isChecked = scenes[adapterPosition].deviceSwitchSettingValue.toBoolean()

            switchStatus.setOnCheckedChangeListener { _, isChecked ->
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
            if (scene.roomId != null && scene.deviceId != null && scene.deviceSwitchId != null) {
                array.add(
                    BodyUpdateSceneData(
                        scene.roomId!!.id,
                        scene.deviceId!!.id,
                        scene.id,
                        scene.deviceSwitchId!!.id,
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