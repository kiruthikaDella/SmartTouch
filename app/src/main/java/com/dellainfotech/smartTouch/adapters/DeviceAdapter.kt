package com.dellainfotech.smartTouch.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.warkiz.widget.IndicatorSeekBar
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
class DeviceAdapter(
    private val mActivity: Activity,
    private val deviceList: ArrayList<GetDeviceData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val logTag = this::class.java.simpleName

    private var customizationClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var featuresClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var settingsClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var editSwitchNameClickListener: SwitchItemClickListener<GetDeviceData>? = null
    private var updateDeviceNameClickListener: DeviceItemClickListener<GetDeviceData>? = null

    private val eightPanelView = 1
    private val fourPanelView = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            eightPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_room_eight_panel, parent, false)
                EightPanelViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_room_four_panel, parent, false)
                FourPanelViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val data = deviceList[position]

        subscribeToDevice(data.deviceSerialNo)

        when (holder.itemViewType) {
            eightPanelView -> {
                val eightPanelViewHolder: EightPanelViewHolder = holder as EightPanelViewHolder
                setEightSwitchViewHolder(eightPanelViewHolder, data)
            }
            fourPanelView -> {
                val fourPanelViewHolder: FourPanelViewHolder = holder as FourPanelViewHolder
                setFourSwitchViewHolder(fourPanelViewHolder, data)
            }
        }

    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (deviceList[position].deviceType == Constants.DEVICE_TYPE_EIGHT) {
            eightPanelView
        } else {
            fourPanelView
        }
    }

    inner class EightPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageButton
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageButton
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout

        val linearCustomization = itemView.findViewById(R.id.linear_customization) as LinearLayout
        val linearFeature = itemView.findViewById(R.id.linear_features) as LinearLayout
        val linearDeviceSettings =
            itemView.findViewById(R.id.linear_device_settings) as LinearLayout

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView
        val tvSwitchNameFive = itemView.findViewById(R.id.tv_switch_five_name) as TextView
        val tvSwitchNameSix = itemView.findViewById(R.id.tv_switch_six_name) as TextView
        val tvSwitchNameSeven = itemView.findViewById(R.id.tv_switch_seven_name) as TextView
        val tvSwitchNameEight = itemView.findViewById(R.id.tv_switch_eight_name) as TextView

        val tvSwitchOneEdit = itemView.findViewById(R.id.tv_switch_one_edit) as TextView
        val tvSwitchTwoEdit = itemView.findViewById(R.id.tv_switch_two_edit) as TextView
        val tvSwitchThreeEdit = itemView.findViewById(R.id.tv_switch_three_edit) as TextView
        val tvSwitchFourEdit = itemView.findViewById(R.id.tv_switch_four_edit) as TextView
        val tvSwitchFiveEdit = itemView.findViewById(R.id.tv_switch_five_edit) as TextView
        val tvSwitchSixEdit = itemView.findViewById(R.id.tv_switch_six_edit) as TextView
        val tvSwitchSevenEdit = itemView.findViewById(R.id.tv_switch_seven_edit) as TextView
        val tvSwitchEightEdit = itemView.findViewById(R.id.tv_switch_eight_edit) as TextView

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val switchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val switchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val switchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial
        val switchFive = itemView.findViewById(R.id.switch_five) as SwitchMaterial
        val switchSix = itemView.findViewById(R.id.switch_six) as SwitchMaterial
        val switchSeven = itemView.findViewById(R.id.switch_seven) as SwitchMaterial
        val switchEight = itemView.findViewById(R.id.switch_eight) as SwitchMaterial

        var seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

        val tvSwitchPortA = itemView.findViewById(R.id.switch_usb_port_a) as SwitchMaterial
        val tvSwitchPortC = itemView.findViewById(R.id.switch_usb_port_c) as SwitchMaterial

    }

    inner class FourPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageButton
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageButton
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout

        val linearCustomization = itemView.findViewById(R.id.linear_customization) as LinearLayout
        val linearFeature = itemView.findViewById(R.id.linear_features) as LinearLayout
        val linearDeviceSettings =
            itemView.findViewById(R.id.linear_device_settings) as LinearLayout

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView

        val tvSwitchOneEdit = itemView.findViewById(R.id.tv_switch_one_edit) as TextView
        val tvSwitchTwoEdit = itemView.findViewById(R.id.tv_switch_two_edit) as TextView
        val tvSwitchThreeEdit = itemView.findViewById(R.id.tv_switch_three_edit) as TextView
        val tvSwitchFourEdit = itemView.findViewById(R.id.tv_switch_four_edit) as TextView

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val switchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val switchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val switchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial

        val seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

        val switchPortC = itemView.findViewById(R.id.switch_usb_port_c) as SwitchMaterial
    }

    private fun setEightSwitchViewHolder(holder: EightPanelViewHolder, device: GetDeviceData) {
        holder.apply {
            tvPanelName.text = device.deviceName

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.toBoolean()
                        }
                        "2" -> {
                            val switchName = value.name
                            tvSwitchNameTwo.text = switchName
                            switchTwo.isChecked = value.switchStatus.toBoolean()
                        }
                        "3" -> {
                            val switchName = value.name
                            tvSwitchNameThree.text = switchName
                            switchThree.isChecked = value.switchStatus.toBoolean()
                        }
                        "4" -> {
                            val switchName = value.name
                            tvSwitchNameFour.text = switchName
                            switchFour.isChecked = value.switchStatus.toBoolean()
                        }
                        "5" -> {
                            val switchName = value.name
                            tvSwitchNameFive.text = switchName
                            switchFive.isChecked = value.switchStatus.toBoolean()
                        }
                        "6" -> {
                            val switchName = value.name
                            tvSwitchNameSix.text = switchName
                            switchSix.isChecked = value.switchStatus.toBoolean()
                        }
                        "7" -> {
                            val switchName = value.name
                            tvSwitchNameSeven.text = switchName
                            switchSeven.isChecked = value.switchStatus.toBoolean()
                        }
                        "8" -> {
                            val switchName = value.name
                            tvSwitchNameEight.text = switchName
                            switchEight.isChecked = value.switchStatus.toBoolean()
                        }
                        "9" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "10" -> {
                            tvSwitchPortA.isChecked = value.switchStatus.toBoolean()
                        }
                        "11" -> {
                            tvSwitchPortC.isChecked = value.switchStatus.toBoolean()
                        }
                    }
                }
            }

            imgBtnPanelMenu.setOnClickListener {
                if (linearPanelMenu.isVisible) {
                    linearPanelMenu.visibility = View.GONE
                } else {
                    linearPanelMenu.visibility = View.VISIBLE
                }
            }

            imgBtnPanelEdit.setOnClickListener {
                updateDeviceNameClickListener?.onItemClick(device, adapterPosition)
            }

            linearCustomization.setOnClickListener {
                customizationClickListener?.onItemClick(device)
            }

            linearFeature.setOnClickListener {
                featuresClickListener?.onItemClick(device)
            }

            linearDeviceSettings.setOnClickListener {
                settingsClickListener?.onItemClick(device)
            }

            tvSwitchOneEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[0])
                }
            }
            tvSwitchTwoEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[1])
                }
            }
            tvSwitchThreeEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[2])
                }
            }
            tvSwitchFourEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[3])
                }
            }
            tvSwitchFiveEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[4])
                }
            }
            tvSwitchSixEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[5])
                }
            }
            tvSwitchSevenEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[6])
                }
            }
            tvSwitchEightEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[7])
                }
            }
        }
    }

    private fun setFourSwitchViewHolder(holder: FourPanelViewHolder, device: GetDeviceData) {
        holder.apply {
            tvPanelName.text = device.deviceName

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.toBoolean()
                        }
                        "2" -> {
                            val switchName = value.name
                            tvSwitchNameTwo.text = switchName
                            switchTwo.isChecked = value.switchStatus.toBoolean()
                        }
                        "3" -> {
                            val switchName = value.name
                            tvSwitchNameThree.text = switchName
                            switchThree.isChecked = value.switchStatus.toBoolean()
                        }
                        "4" -> {
                            val switchName = value.name
                            tvSwitchNameFour.text = switchName
                            switchFour.isChecked = value.switchStatus.toBoolean()
                        }
                        "5" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "6" -> {
                            switchPortC.isChecked = value.switchStatus.toBoolean()
                        }
                    }
                }
            }

            imgBtnPanelMenu.setOnClickListener {
                if (linearPanelMenu.isVisible) {
                    linearPanelMenu.visibility = View.GONE
                } else {
                    linearPanelMenu.visibility = View.VISIBLE
                }
            }

            imgBtnPanelEdit.setOnClickListener {
                updateDeviceNameClickListener?.onItemClick(device, adapterPosition)
            }

            linearCustomization.setOnClickListener {
                customizationClickListener?.onItemClick(device)
            }

            linearFeature.setOnClickListener {
                featuresClickListener?.onItemClick(device)
            }

            linearDeviceSettings.setOnClickListener {
                settingsClickListener?.onItemClick(device)
            }

            tvSwitchOneEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[0])
                }

            }

            tvSwitchTwoEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[1])
                }
            }
            tvSwitchThreeEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[2])
                }
            }
            tvSwitchFourEdit.setOnClickListener {
                device.switchData?.let { switchData ->
                    editSwitchNameClickListener?.onItemClick(device, adapterPosition, switchData[3])
                }
            }

            switchOne.setOnCheckedChangeListener { _, isChecked ->
                publish(device.deviceSerialNo, Constants.AWS_SWITCH_1, isChecked.toInt().toString())
            }
            switchTwo.setOnCheckedChangeListener { _, isChecked ->
                publish(device.deviceSerialNo, Constants.AWS_SWITCH_2, isChecked.toInt().toString())
            }
            switchThree.setOnCheckedChangeListener { _, isChecked ->
                publish(device.deviceSerialNo, Constants.AWS_SWITCH_3, isChecked.toInt().toString())
            }
            switchFour.setOnCheckedChangeListener { _, isChecked ->
                publish(device.deviceSerialNo, Constants.AWS_SWITCH_4, isChecked.toInt().toString())
            }
            switchPortC.setOnCheckedChangeListener { _, isChecked ->
                publish(device.deviceSerialNo, Constants.AWS_USB_PORT_C, isChecked.toInt().toString())
            }
        }
    }

    //
    //region MQTT Methods
    //

    private fun subscribeToDevice(deviceId: String) {
        try {
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
                Constants.Control_Device_Switches.replace(Constants.AWS_DEVICE_ID, deviceId),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                mActivity.runOnUiThread {

                    val message = String(data, StandardCharsets.UTF_8)
                    Log.d("$logTag ReceivedData", "$topic    $message")

                    if (topic.contains("/control/")) {
                        val topic1 = topic.split("/")
                        // topic [0] = ''
                        // topic [1] = smarttouch
                        // topic [2] = deviceId
                        // topic [3] = control

                        val deviceData = deviceList.find { it.id == topic1[2] }

                        val jsonObject = JSONObject(message)
                        if (jsonObject.has(Constants.AWS_SWITCH_1)) {
                            deviceData?.switchData?.get(0)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_1).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_2)) {
                            deviceData?.switchData?.get(1)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_2).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_3)) {
                            deviceData?.switchData?.get(2)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_3).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_4)) {
                            deviceData?.switchData?.get(3)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_4).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_5)) {
                            deviceData?.switchData?.get(4)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_5).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_6)) {
                            deviceData?.switchData?.get(5)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_6).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_7)) {
                            deviceData?.switchData?.get(6)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_7).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_SWITCH_8)) {
                            deviceData?.switchData?.get(7)?.switchStatus =
                                jsonObject.getString(Constants.AWS_SWITCH_8).toInt()
                        }
                        if (jsonObject.has(Constants.AWS_USB_PORT_A)) {
                            if (deviceData?.deviceType == Constants.DEVICE_TYPE_EIGHT) {
                                deviceData.switchData?.get(9)?.switchStatus =
                                    jsonObject.getString(Constants.AWS_USB_PORT_A).toInt()
                            }

                        }
                        if (jsonObject.has(Constants.AWS_USB_PORT_C)) {
                            if (deviceData?.deviceType == Constants.DEVICE_TYPE_EIGHT) {
                                deviceData.switchData?.get(10)?.switchStatus =
                                    jsonObject.getString(Constants.AWS_USB_PORT_C).toInt()
                            } else {
                                deviceData?.switchData?.get(5)?.switchStatus =
                                    jsonObject.getString(Constants.AWS_USB_PORT_C).toInt()
                            }

                        }

                        for ((index, value) in deviceList.withIndex()) {
                            if (value.id == deviceData?.id) {
                                deviceList[index] = deviceData
                                break
                            }
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(AwsMqttSingleton.logTag, "Subscription error.", e)
        }
    }

    private fun publish(deviceId: String, switchIndex: String, switchValue: String) {
        val payload = JSONObject()
        payload.put(switchIndex, switchValue)
        AwsMqttSingleton.publish(
            Constants.Control_Device_Switches.replace(
                Constants.AWS_DEVICE_ID,
                deviceId
            ), payload.toString()
        )
    }

    //
    //endregion
    //

    //
    //region interface
    //

    interface DeviceItemClickListener<T> {
        fun onItemClick(data: T, devicePosition: Int)
    }

    interface SwitchItemClickListener<T> {
        fun onItemClick(data: T, devicePosition: Int, switchData: DeviceSwitchData)
    }

    //
    //endregion
    //

    //
    //region clicklistener
    //

    fun setOnCustomizationClickListener(listener: AdapterItemClickListener<GetDeviceData>) {
        this.customizationClickListener = listener
    }

    fun setOnFeaturesClickListener(listener: AdapterItemClickListener<GetDeviceData>) {
        this.featuresClickListener = listener
    }

    fun setOnSettingsClickListener(listener: AdapterItemClickListener<GetDeviceData>) {
        this.settingsClickListener = listener
    }

    fun setOnEditSwitchNameClickListener(listener: SwitchItemClickListener<GetDeviceData>) {
        this.editSwitchNameClickListener = listener
    }

    fun setOnUpdateDeviceNameClickListener(listener: DeviceItemClickListener<GetDeviceData>) {
        this.updateDeviceNameClickListener = listener
    }

    //
    //endregion
    //
}