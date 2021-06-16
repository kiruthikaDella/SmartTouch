package com.dellainfotech.smartTouch.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.spinneradapter.DeviceAdapter
import com.dellainfotech.smartTouch.adapters.spinneradapter.RoomAdapter
import com.dellainfotech.smartTouch.adapters.spinneradapter.SwitchAdapter
import com.dellainfotech.smartTouch.api.body.BodySceneData
import com.dellainfotech.smartTouch.api.body.BodyUpdateScene
import com.dellainfotech.smartTouch.api.body.BodyUpdateSceneData
import com.dellainfotech.smartTouch.api.model.*
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */
class UpdateDeviceSceneAdapter(
    private val mActivity: Activity,
    private val scenes: ArrayList<Scene>,
    private val roomId: String,
    private val deviceId: String,
) : RecyclerView.Adapter<UpdateDeviceSceneAdapter.MyViewHolder>() {

    private val roomDataList = arrayListOf<ControlModeRoomData>()
    private val logTag = this::class.java.simpleName

    private var deleteClickListener: DeleteSceneItemClickListener<Scene>? = null

    private var roomList = arrayListOf<GetRoomData>()
    var updateSceneList = arrayListOf<BodyUpdateSceneData>()

    init {
        for (scene in scenes){
            updateSceneList.add(BodyUpdateSceneData("", "", scene.id, scene.deviceSwitchId!!.id,scene.deviceSwitchSettingValue))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_scene, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        updateSceneList.add(BodyUpdateSceneData("", "", null, "",0))
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
    }

    private fun setSpinners(holder: MyViewHolder) {
        holder.apply {
            val deviceList = arrayListOf<GetDeviceData>()
            val switchList = arrayListOf<DeviceSwitchData>()
            val roomAdapter = RoomAdapter(mActivity, roomList)
            spinnerRoom.adapter = roomAdapter
            scenes[adapterPosition].roomId?.let { roomData ->
                spinnerRoom.setSelection(roomAdapter.getPositionById(roomData.id))
            }?: kotlin.run {
                spinnerRoom.setSelection(roomAdapter.getPositionById(roomId))
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
                        updateSceneList[adapterPosition].roomId = room.id

                        for (roomData in roomDataList) {
                            if (roomData.id == room.id) {
                                roomData.deviceData?.let { devices ->
                                    deviceList.clear()
                                    deviceList.addAll(devices)
                                    val deviceAdapter = DeviceAdapter(mActivity, deviceList)
                                    spinnerDevice.adapter = deviceAdapter
                                    scenes[adapterPosition].deviceId?.let { deviceData ->
                                        spinnerDevice.setSelection(
                                            deviceAdapter.getPositionById(
                                                deviceData.id
                                            )
                                        )
                                    }?: kotlin.run {
                                        spinnerDevice.setSelection(
                                            deviceAdapter.getPositionById(
                                                deviceId
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
                        updateSceneList[adapterPosition].deviceId = device.id

                        for (deviceData in deviceList) {
                            if (deviceData.id == device.id) {
                                deviceData.switchData?.let { switches ->
                                    switchList.clear()
                                    for (switch in switches) {
                                        if (switch.typeOfSwitch == 0) {
                                            switchList.add(switch)
                                        }
                                    }
                                    val switchAdapter = SwitchAdapter(mActivity, switchList)
                                    spinnerSwitch.adapter = switchAdapter
                                    scenes[adapterPosition].deviceSwitchId?.let { switchData ->
                                        spinnerSwitch.setSelection(
                                            switchAdapter.getPositionById(
                                                switchData.id
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
                        updateSceneList[adapterPosition].deviceSwitchId = switch.id
                        scenes[adapterPosition].deviceSwitchId = switch
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            switch.isChecked = scenes[adapterPosition].deviceSwitchSettingValue.toBoolean()

            switch.setOnCheckedChangeListener { _, isChecked ->
                updateSceneList[adapterPosition].deviceSwitchSettingValue = isChecked.toInt()
            }

            ibDelete.setOnClickListener {
                DialogUtil.askAlert(
                    mActivity, mActivity.getString(R.string.dialog_title_delete_scene),
                    mActivity.getString(R.string.text_yes),
                    mActivity.getString(R.string.text_no),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            if (scenes[adapterPosition].id == ""){
                       /*         scenes.removeAt(adapterPosition)
                                notifyDataSetChanged()*/
                                deleteScene(adapterPosition)
                            }else {
                                deleteClickListener?.onItemClick(scenes[adapterPosition],adapterPosition)
                            }
                        }

                        override fun onNoClicked() {
                        }

                    }
                )
            }
        }
    }

    fun deleteScene(position: Int){
       /* val id = scenes[position].id
        for ((index,value) in updateSceneList.withIndex()){
            if (value.sceneDetailId == id){
                updateSceneList.removeAt(index)
                scenes.removeAt(position)
                notifyDataSetChanged()
            }
        }*/
        updateSceneList.removeAt(position)
        scenes.removeAt(position)
        notifyDataSetChanged()
    }

    fun addScene(){
        scenes.add(Scene("","",null,null,null,0))
        notifyDataSetChanged()
    }

    fun updateRoomList(controlModeList: List<ControlModeRoomData>) {
        roomDataList.addAll(controlModeList)
        for (roomData in roomDataList) {
            roomList.add(GetRoomData(roomData.id, null, roomData.userId, roomData.roomName, 1))
        }
    }

    fun getScenes(): ArrayList<BodyUpdateSceneData> {
        val scenes = arrayListOf<BodyUpdateSceneData>()
        for (scene in updateSceneList) {
            if (scene.deviceSwitchId.isNotEmpty()) {
                scenes.add(scene)
            }
        }
        return scenes
    }

    fun isDuplicateSwitchFound(): Boolean{

        val scenes = getScenes()

        val switchList = arrayListOf<String>()
        for (switch in scenes){
            switchList.add(switch.deviceSwitchId)
        }
        switchList.removeAll(listOf(null,""))
        return switchList.size != switchList.distinct().count()
    }

    interface DeleteSceneItemClickListener<T> {
        fun onItemClick(data: T, scenePosition: Int)
    }

    fun setOnDeleteClickListener(listener: DeleteSceneItemClickListener<Scene>) {
        this.deleteClickListener = listener
    }
}