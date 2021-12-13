package com.voinismartiot.voni.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.api.model.GetDeviceData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.google.android.material.switchmaterial.SwitchMaterial
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
@SuppressLint("ClickableViewAccessibility")
class DeviceAdapter(
    private val mActivity: Activity,
    private val deviceList: ArrayList<GetDeviceData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //
    //region Adapter
    //

    private val logTag = this::class.java.simpleName

    private var customizationClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var featuresClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var settingsClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var editSwitchNameClickListener: SwitchItemClickListener<GetDeviceData>? = null
    private var updateDeviceNameClickListener: DeviceItemClickListener<GetDeviceData>? = null

    private val eightPanelView = 1
    private val fourPanelView = 2
    private val smartAckPanelView = 3
    private val smartApPanelView = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            eightPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_room_eight_panel, parent, false)
                EightPanelViewHolder(v)
            }
            fourPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_room_four_panel, parent, false)
                FourPanelViewHolder(v)
            }
            smartAckPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_room_smart_ack, parent, false)
                SmartAckPanelViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_room_smart_ack, parent, false)
                SmartApPanelViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val data = deviceList[position]

        if (AwsMqttSingleton.isConnected()) {
            subscribeToDevice(data.deviceSerialNo)
        }

        when (holder.itemViewType) {
            eightPanelView -> {
                val eightPanelViewHolder: EightPanelViewHolder = holder as EightPanelViewHolder
                setEightSwitchViewHolder(eightPanelViewHolder, data)
            }
            fourPanelView -> {
                val fourPanelViewHolder: FourPanelViewHolder = holder as FourPanelViewHolder
                setFourSwitchViewHolder(fourPanelViewHolder, data)
            }
            smartAckPanelView -> {
                val smartAckPanelViewHolder: SmartAckPanelViewHolder =
                    holder as SmartAckPanelViewHolder
                setSmartAckViewHolder(smartAckPanelViewHolder, data)
            }
            smartApPanelView -> {
                val smartApPanelViewHolder: SmartApPanelViewHolder =
                    holder as SmartApPanelViewHolder
                setSmartApViewHolder(smartApPanelViewHolder, data)
            }
        }

    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (deviceList[position].productGroup.lowercase() == Constants.PRODUCT_SMART_TOUCH) {
            if (deviceList[position].deviceType == Constants.DEVICE_TYPE_EIGHT) {
                eightPanelView
            } else {
                fourPanelView
            }
        } else if (deviceList[position].productGroup.lowercase() == Constants.PRODUCT_SMART_ACK) {
            smartAckPanelView
        } else {
            smartApPanelView
        }
    }

    inner class EightPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageView
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageView
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout

        val constraintLayout = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvCustomization = itemView.findViewById(R.id.tv_customization) as TextView
        val tvFeature = itemView.findViewById(R.id.tv_features) as TextView
        val tvDeviceSettings = itemView.findViewById(R.id.tv_device_settings) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView
        val tvSwitchNameFive = itemView.findViewById(R.id.tv_switch_five_name) as TextView
        val tvSwitchNameSix = itemView.findViewById(R.id.tv_switch_six_name) as TextView
        val tvSwitchNameSeven = itemView.findViewById(R.id.tv_switch_seven_name) as TextView
        val tvSwitchNameEight = itemView.findViewById(R.id.tv_switch_eight_name) as TextView

        val tvSwitchNameOneDesc = itemView.findViewById(R.id.tv_switch_one_type) as TextView
        val tvSwitchNameTwoDesc = itemView.findViewById(R.id.tv_switch_two_type) as TextView
        val tvSwitchNameThreeDesc = itemView.findViewById(R.id.tv_switch_three_type) as TextView
        val tvSwitchNameFourDesc = itemView.findViewById(R.id.tv_switch_four_type) as TextView
        val tvSwitchNameFiveDesc = itemView.findViewById(R.id.tv_switch_five_type) as TextView
        val tvSwitchNameSixDesc = itemView.findViewById(R.id.tv_switch_six_type) as TextView
        val tvSwitchNameSevenDesc = itemView.findViewById(R.id.tv_switch_seven_type) as TextView
        val tvSwitchNameEightDesc = itemView.findViewById(R.id.tv_switch_eight_type) as TextView

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

        val switchPortA = itemView.findViewById(R.id.switch_usb_port_a) as SwitchMaterial
        val switchPortC = itemView.findViewById(R.id.switch_usb_port_c) as SwitchMaterial

        val tvSwitchPortA = itemView.findViewById(R.id.tv_usb_port_a) as TextView
        val tvSwitchPortC = itemView.findViewById(R.id.tv_usb_port_c) as TextView

    }

    inner class FourPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageView
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageView
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout
        val constraintLayout = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvCustomization = itemView.findViewById(R.id.tv_customization) as TextView
        val tvFeature = itemView.findViewById(R.id.tv_features) as TextView
        val tvDeviceSettings = itemView.findViewById(R.id.tv_device_settings) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView

        val tvSwitchNameOneDesc = itemView.findViewById(R.id.tv_switch_one_type) as TextView
        val tvSwitchNameTwoDesc = itemView.findViewById(R.id.tv_switch_two_type) as TextView
        val tvSwitchNameThreeDesc = itemView.findViewById(R.id.tv_switch_three_type) as TextView
        val tvSwitchNameFourDesc = itemView.findViewById(R.id.tv_switch_four_type) as TextView

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
        val tvSwitchPortC = itemView.findViewById(R.id.tv_usb_port_c) as TextView
    }

    inner class SmartAckPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageView
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageView
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout
        val constraintLayout = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvDeviceSettings = itemView.findViewById(R.id.tv_device_settings) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView
        val tvSwitchNameFive = itemView.findViewById(R.id.tv_switch_five_name) as TextView

        val tvSwitchNameOneDesc = itemView.findViewById(R.id.tv_switch_one_type) as TextView
        val tvSwitchNameTwoDesc = itemView.findViewById(R.id.tv_switch_two_type) as TextView
        val tvSwitchNameThreeDesc = itemView.findViewById(R.id.tv_switch_three_type) as TextView
        val tvSwitchNameFourDesc = itemView.findViewById(R.id.tv_switch_four_type) as TextView
        val tvSwitchNameFiveDesc = itemView.findViewById(R.id.tv_switch_five_type) as TextView

        val tvSwitchOneEdit = itemView.findViewById(R.id.tv_switch_one_edit) as TextView
        val tvSwitchTwoEdit = itemView.findViewById(R.id.tv_switch_two_edit) as TextView
        val tvSwitchThreeEdit = itemView.findViewById(R.id.tv_switch_three_edit) as TextView
        val tvSwitchFourEdit = itemView.findViewById(R.id.tv_switch_four_edit) as TextView

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val switchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val switchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val switchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial
        val switchFive = itemView.findViewById(R.id.switch_five) as SwitchMaterial

        val seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

    }

    inner class SmartApPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageView
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageView
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout
        val constraintLayout = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvDeviceSettings = itemView.findViewById(R.id.tv_device_settings) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView
        val tvSwitchNameFive = itemView.findViewById(R.id.tv_switch_five_name) as TextView

        val tvSwitchNameOneDesc = itemView.findViewById(R.id.tv_switch_one_type) as TextView
        val tvSwitchNameTwoDesc = itemView.findViewById(R.id.tv_switch_two_type) as TextView
        val tvSwitchNameThreeDesc = itemView.findViewById(R.id.tv_switch_three_type) as TextView
        val tvSwitchNameFourDesc = itemView.findViewById(R.id.tv_switch_four_type) as TextView
        val tvSwitchNameFiveDesc = itemView.findViewById(R.id.tv_switch_five_type) as TextView

        val tvSwitchOneEdit = itemView.findViewById(R.id.tv_switch_one_edit) as TextView
        val tvSwitchTwoEdit = itemView.findViewById(R.id.tv_switch_two_edit) as TextView
        val tvSwitchThreeEdit = itemView.findViewById(R.id.tv_switch_three_edit) as TextView
        val tvSwitchFourEdit = itemView.findViewById(R.id.tv_switch_four_edit) as TextView

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val switchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val switchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val switchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial
        val switchFive = itemView.findViewById(R.id.switch_five) as SwitchMaterial

        val seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

    }

    private fun setEightSwitchViewHolder(holder: EightPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            try {
                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                constraintLayout.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = constraintLayout.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameOneDesc.text = it
                            }
                        }
                        "2" -> {
                            val switchName = value.name
                            tvSwitchNameTwo.text = switchName
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameTwoDesc.text = it
                            }
                        }
                        "3" -> {
                            val switchName = value.name
                            tvSwitchNameThree.text = switchName
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameThreeDesc.text = it
                            }
                        }
                        "4" -> {
                            val switchName = value.name
                            tvSwitchNameFour.text = switchName
                            switchFour.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFourDesc.text = it
                            }
                        }
                        "5" -> {
                            val switchName = value.name
                            tvSwitchNameFive.text = switchName
                            switchFive.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFiveDesc.text = it
                            }
                        }
                        "6" -> {
                            val switchName = value.name
                            tvSwitchNameSix.text = switchName
                            switchSix.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameSixDesc.text = it
                            }
                        }
                        "7" -> {
                            val switchName = value.name
                            tvSwitchNameSeven.text = switchName
                            switchSeven.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameSevenDesc.text = it
                            }
                        }
                        "8" -> {
                            val switchName = value.name
                            tvSwitchNameEight.text = switchName
                            switchEight.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameEightDesc.text = it
                            }
                        }
                        "9" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "10" -> {
                            val switchName = value.name
                            tvSwitchPortA.text = switchName
                            switchPortA.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "11" -> {
                            val switchName = value.name
                            tvSwitchPortC.text = switchName
                            switchPortC.isChecked = value.switchStatus.toInt().toBoolean()
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

            tvCustomization.setOnClickListener {
                customizationClickListener?.onItemClick(device)
            }

            tvFeature.setOnClickListener {
                featuresClickListener?.onItemClick(device)
            }

            tvDeviceSettings.setOnClickListener {
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

            switchOne.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString(),
                    tvSwitchNameOne.text.toString()
                )
            }

            switchTwo.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString(),
                    tvSwitchNameTwo.text.toString()
                )
            }

            switchThree.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString(),
                    tvSwitchNameThree.text.toString()
                )
            }

            switchFour.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString(),
                    tvSwitchNameFour.text.toString()
                )
            }

            switchFive.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_5,
                    switchFive.isChecked.toInt().toString(),
                    tvSwitchNameFive.text.toString()
                )
            }

            switchSix.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_6,
                    switchSix.isChecked.toInt().toString(),
                    tvSwitchNameSix.text.toString()
                )
            }

            switchSeven.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_7,
                    switchSeven.isChecked.toInt().toString(),
                    tvSwitchNameSeven.text.toString()
                )
            }

            switchEight.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_8,
                    switchEight.isChecked.toInt().toString(),
                    tvSwitchNameEight.text.toString()
                )
            }

            switchPortA.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_USB_PORT_A,
                    switchPortA.isChecked.toInt().toString()
                )
            }

            switchPortC.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_USB_PORT_C,
                    switchPortC.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {

                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    seekBar?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

            }
        }
    }

    private fun setFourSwitchViewHolder(holder: FourPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            try {

                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                constraintLayout.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = constraintLayout.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameOneDesc.text = it
                            }
                        }
                        "2" -> {
                            val switchName = value.name
                            tvSwitchNameTwo.text = switchName
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameTwoDesc.text = it
                            }
                        }
                        "3" -> {
                            val switchName = value.name
                            tvSwitchNameThree.text = switchName
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameThreeDesc.text = it
                            }
                        }
                        "4" -> {
                            val switchName = value.name
                            tvSwitchNameFour.text = switchName
                            switchFour.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFourDesc.text = it
                            }
                        }
                        "5" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "6" -> {
                            val switchName = value.name
                            tvSwitchPortC.text = switchName
                            switchPortC.isChecked = value.switchStatus.toInt().toBoolean()
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

            tvCustomization.setOnClickListener {
                customizationClickListener?.onItemClick(device)
            }

            tvFeature.setOnClickListener {
                featuresClickListener?.onItemClick(device)
            }

            tvDeviceSettings.setOnClickListener {
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

            switchOne.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString(),
                    tvSwitchNameOne.text.toString()
                )
            }

            switchTwo.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString(),
                    tvSwitchNameTwo.text.toString()
                )
            }

            switchThree.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString(),
                    tvSwitchNameThree.text.toString()
                )
            }

            switchFour.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString(),
                    tvSwitchNameFour.text.toString()
                )
            }

            switchPortC.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_USB_PORT_C,
                    switchPortC.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    seekBar?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

            }
        }
    }

    private fun setSmartAckViewHolder(holder: SmartAckPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            try {

                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                constraintLayout.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = constraintLayout.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameOneDesc.text = it
                            }
                        }
                        "2" -> {
                            val switchName = value.name
                            tvSwitchNameTwo.text = switchName
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameTwoDesc.text = it
                            }
                        }
                        "3" -> {
                            val switchName = value.name
                            tvSwitchNameThree.text = switchName
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameThreeDesc.text = it
                            }
                        }
                        "4" -> {
                            val switchName = value.name
                            tvSwitchNameFour.text = switchName
                            switchFour.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFourDesc.text = it
                            }
                        }
                        "5" -> {
                            val switchName = value.name
                            tvSwitchNameFive.text = switchName
                            switchFive.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFiveDesc.text = it
                            }

                            seekBar.isVisible = value.switchStatus.toInt().toBoolean()
                        }
                        "6" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
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

            tvDeviceSettings.setOnClickListener {
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

            switchOne.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString()
                )
            }

            switchTwo.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString()
                )
            }

            switchThree.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString()
                )
            }

            switchFour.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString()
                )
            }

            switchFive.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_5,
                    switchFive.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {

                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    seekBar?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

            }
        }
    }

    private fun setSmartApViewHolder(holder: SmartApPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            try {

                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                constraintLayout.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = constraintLayout.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameOneDesc.text = it
                            }
                        }
                        "2" -> {
                            val switchName = value.name
                            tvSwitchNameTwo.text = switchName
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameTwoDesc.text = it
                            }
                        }
                        "3" -> {
                            val switchName = value.name
                            tvSwitchNameThree.text = switchName
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameThreeDesc.text = it
                            }
                        }
                        "4" -> {
                            val switchName = value.name
                            tvSwitchNameFour.text = switchName
                            switchFour.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFourDesc.text = it
                            }
                        }
                        "5" -> {
                            val switchName = value.name
                            tvSwitchNameFive.text = switchName
                            switchFive.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFiveDesc.text = it
                            }
                            seekBar.isVisible = value.switchStatus.toInt().toBoolean()
                        }
                        "6" -> {
                            try {
                                seekBar.setProgress(value.switchStatus.toFloat())
                            }catch (e: Exception){
                                e.printStackTrace()
                            }

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

            tvDeviceSettings.setOnClickListener {
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

            switchOne.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString()
                )
            }

            switchTwo.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString()
                )
            }

            switchThree.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString()
                )
            }

            switchFour.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString()
                )
            }

            switchFive.setOnClickListener {
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_5,
                    switchFive.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    seekBar?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

            }
        }
    }


    //
    //endregion
    //

    //
    //region MQTT Methods
    //

    private fun subscribeToDevice(deviceId: String) {
        try {

            //Response of Get Switch status
            AwsMqttSingleton.mqttManager?.subscribeToTopic(
                MQTTConstants.GET_SWITCH_STATUS.replace(
                    MQTTConstants.AWS_DEVICE_ID,
                    deviceId
                ),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                mActivity.runOnUiThread {

                    val message = String(data, StandardCharsets.UTF_8)
                    Log.d("$logTag ReceivedData", "$topic $message")

                    try {
                        val topic1 = topic.split("/")
                        // topic [0] = ''
                        // topic [1] = smarttouch
                        // topic [2] = deviceId
                        // topic [3] = swstatus

                        val deviceData = deviceList.find { it.deviceSerialNo == topic1[2] }

                        val jsonObject = JSONObject(message)

                        if (jsonObject.has(MQTTConstants.AWS_SWITCH)) {
                            val switchStatus =
                                jsonObject.getString(MQTTConstants.AWS_SWITCH).split(",")

                            // topic [0] = ''
                            // topic [1] = Switch 1 Status
                            // topic [2] = Switch 2 Status
                            // topic [3] = Switch 3 Status
                            // topic [4] = Switch 4 Status
                            // topic [5] = Switch 5 Status (if DT = 8)
                            // topic [6] = Switch 6 Status (if DT = 8)
                            // topic [7] = Switch 7 Status (if DT = 8)
                            // topic [8] = Switch 8 Status (if DT = 8)

                            deviceData?.switchData?.get(0)?.switchStatus = switchStatus[0]
                            deviceData?.switchData?.get(1)?.switchStatus = switchStatus[1]
                            deviceData?.switchData?.get(2)?.switchStatus = switchStatus[2]
                            deviceData?.switchData?.get(3)?.switchStatus = switchStatus[3]

                            if (jsonObject.has(MQTTConstants.AWS_DEVICE_TYPE)) {
                                if (jsonObject.getString(MQTTConstants.AWS_DEVICE_TYPE) == "8") {
                                    deviceData?.switchData?.get(4)?.switchStatus =
                                        switchStatus[4]
                                    deviceData?.switchData?.get(5)?.switchStatus =
                                        switchStatus[5]
                                    deviceData?.switchData?.get(6)?.switchStatus =
                                        switchStatus[6]
                                    deviceData?.switchData?.get(7)?.switchStatus =
                                        switchStatus[7]
                                    deviceData?.switchData?.get(8)?.switchStatus =
                                        jsonObject.getString(
                                            MQTTConstants.AWS_DIMMER
                                        ) //Dimmer
                                    deviceData?.switchData?.get(9)?.switchStatus =
                                        jsonObject.getString(
                                            MQTTConstants.AWS_USB_A
                                        ) //USB A
                                    deviceData?.switchData?.get(10)?.switchStatus =
                                        jsonObject.getString(
                                            MQTTConstants.AWS_USB_C
                                        ) //USB C
                                } else {
                                    if (jsonObject.has(MQTTConstants.AWS_USB_C)) {
                                        deviceData?.switchData?.get(4)?.switchStatus =
                                            jsonObject.getString(
                                                MQTTConstants.AWS_DIMMER
                                            ) //Dimmer
                                        deviceData?.switchData?.get(5)?.switchStatus =
                                            jsonObject.getString(
                                                MQTTConstants.AWS_USB_C
                                            ) //USB C
                                    } else {
                                        deviceData?.switchData?.get(4)?.switchStatus = switchStatus[4] //Fan speed

                                        deviceData?.switchData?.get(5)?.switchStatus =
                                            jsonObject.getString(
                                                MQTTConstants.AWS_DIMMER
                                            ) //Dimmer
                                    }
                                }
                            }
                        }

                        deviceData?.let { dData ->
                            val firstIndex = deviceList.indexOfFirst { it.id == dData.id }
                            deviceList[firstIndex] = dData
                        }

                        notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            //Current Device Status Update - Online/Offline
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
                MQTTConstants.DEVICE_STATUS.replace(MQTTConstants.AWS_DEVICE_ID, deviceId),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                mActivity.runOnUiThread {

                    val message = String(data, StandardCharsets.UTF_8)
                    Log.d("$logTag ReceivedData", "$topic    $message")

                    try {
                        val topic1 = topic.split("/")
                        // topic [0] = ''
                        // topic [1] = smarttouch
                        // topic [2] = deviceId
                        // topic [3] = status

                        val deviceData = deviceList.find { it.deviceSerialNo == topic1[2] }

                        val jsonObject = JSONObject(message)

                        if (jsonObject.has(MQTTConstants.AWS_STATUS)) {
                            deviceData?.isDeviceAvailable =
                                jsonObject.getString(MQTTConstants.AWS_STATUS)

                            deviceData?.let { dData ->
                                val firstIndex =
                                    deviceList.indexOfFirst { it.deviceSerialNo == dData.deviceSerialNo }
                                deviceList[firstIndex] = dData
                            }

                            notifyDataSetChanged()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    fun publish(
        deviceId: String,
        switchIndex: String,
        switchValue: String,
        switchName: String = ""
    ) {
        val payload = JSONObject()
        payload.put(switchIndex, switchValue)
        if (switchName.isNotEmpty())
            payload.put(MQTTConstants.AWS_NAME, switchName)

        val topic = MQTTConstants.CONTROL_DEVICE_SWITCHES.replace(
            MQTTConstants.AWS_DEVICE_ID,
            deviceId
        )
        Log.e(logTag, " publishSwitch topic $topic payload $payload  ")
        AwsMqttSingleton.publish(
            topic, payload.toString()
        )
    }

    private fun publishDimmer(deviceId: String, progress: String) {
        val payload = JSONObject()
        payload.put(MQTTConstants.AWS_DMR, progress)

        Log.e(logTag, " publishDimmer payload $payload  ")

        AwsMqttSingleton.publish(
            MQTTConstants.CONTROL_DEVICE_SWITCHES.replace(
                MQTTConstants.AWS_DEVICE_ID,
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
    //region clickListener
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