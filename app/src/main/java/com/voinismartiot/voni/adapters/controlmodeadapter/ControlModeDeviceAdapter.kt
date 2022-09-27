package com.voinismartiot.voni.adapters.controlmodeadapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.google.android.material.switchmaterial.SwitchMaterial
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.spinneradapter.AppliancesAdapter
import com.voinismartiot.voni.api.model.DeviceAppliances
import com.voinismartiot.voni.api.model.GetDeviceData
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.Utils.isSmartAck
import com.voinismartiot.voni.common.utils.Utils.isSmartAp
import com.voinismartiot.voni.common.utils.Utils.isSmartouch
import com.voinismartiot.voni.common.utils.Utils.stringToFloat
import com.voinismartiot.voni.common.utils.Utils.stringToInt
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt
import com.voinismartiot.voni.mqtt.AwsMqttSingleton
import com.voinismartiot.voni.mqtt.MQTTConstants
import com.voinismartiot.voni.ui.activities.MainActivity
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import org.json.JSONObject
import java.nio.charset.StandardCharsets

@SuppressLint("ClickableViewAccessibility")
class ControlModeDeviceAdapter(
    private val mActivity: Activity,
    private val deviceList: ArrayList<GetDeviceData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val logTag = this::class.java.simpleName

    private val applianceList = (mActivity as MainActivity).getAppliances()

    private val eightPanelView = 1
    private val fourPanelView = 2
    private val smartAckPanelView = 3
    private val smartApPanelView = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            eightPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_eight_panel, parent, false)
                EightPanelViewHolder(v)
            }
            fourPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_four_panel, parent, false)
                FourPanelViewHolder(v)
            }
            smartAckPanelView -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_room_smart_ack, parent, false)
                SmartAckPanelViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_room_smart_ap, parent, false)
                SmartApPanelViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = deviceList[holder.adapterPosition]

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
        return if (deviceList[position].productGroup.isSmartouch()) {
            if (deviceList[position].deviceType == Constants.DEVICE_TYPE_EIGHT) {
                eightPanelView
            } else {
                fourPanelView
            }
        } else if (deviceList[position].productGroup.isSmartAck()) {
            smartAckPanelView
        } else if (deviceList[position].productGroup.isSmartAp()) {
            when (deviceList[position].deviceType) {
                Constants.DEVICE_TYPE_EIGHT -> {
                    eightPanelView
                }
                Constants.DEVICE_TYPE_FOUR -> {
                    fourPanelView
                }
                else -> {
                    smartApPanelView
                }
            }
        } else {
            0
        }
    }

    inner class EightPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val relativeMain = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvOutdoorModeIndication =
            itemView.findViewById(R.id.tv_outdoor_mode_indication) as TextView

        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
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

    }

    inner class FourPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val relativeMain = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvOutdoorModeIndication =
            itemView.findViewById(R.id.tv_outdoor_mode_indication) as TextView

        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView

        val tvSwitchNameOneDesc = itemView.findViewById(R.id.tv_switch_one_type) as TextView
        val tvSwitchNameTwoDesc = itemView.findViewById(R.id.tv_switch_two_type) as TextView
        val tvSwitchNameThreeDesc = itemView.findViewById(R.id.tv_switch_three_type) as TextView
        val tvSwitchNameFourDesc = itemView.findViewById(R.id.tv_switch_four_type) as TextView

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val switchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val switchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val switchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial

        val seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

        val switchPortC = itemView.findViewById(R.id.switch_usb_port_c) as SwitchMaterial
    }

    inner class SmartAckPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val relativeMain = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvOutdoorModeIndication =
            itemView.findViewById(R.id.tv_outdoor_mode_indication) as TextView

        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
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

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val switchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val switchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val switchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial
        val switchFive = itemView.findViewById(R.id.switch_five) as SwitchMaterial

        val seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

    }

    inner class SmartApPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView

        val rlSelectAppliances = itemView.findViewById(R.id.rl_select_appliances) as RelativeLayout
        val spinnerAppliances = itemView.findViewById(R.id.spinner_appliances) as Spinner
        val ivAppliancesDown = itemView.findViewById(R.id.iv_appliances_down) as ImageView

        val tvOutdoorModeIndication =
            itemView.findViewById(R.id.tv_outdoor_mode_indication) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView

        val tvSwitchNameOneDesc = itemView.findViewById(R.id.tv_switch_one_type) as TextView

        val switchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial

    }

    private fun setEightSwitchViewHolder(holder: EightPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            changeEightSwitchStatus(this, true)

            try {
                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                relativeMain.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = relativeMain.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            relativeLayout.isVisible = !device.isDeviceAvailable.toBoolean()
            tvOutdoorModeIndication.isVisible = device.outdoorMode.toBoolean()

            if (device.outdoorMode.toBoolean()) {
                changeEightSwitchStatus(this, false)
            }

            /*if (device.productGroup.isSmartouch()) {
                seekBar.visibility = View.GONE
            }*/

            seekBar.isVisible = device.switchData?.find {
                it.desc?.lowercase() == mActivity.getString(R.string.text_switch_dimmer).lowercase()
            }?.switchStatus?.stringToInt()?.toBoolean() ?: false

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameOneDesc.text = value.desc ?: ""
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameTwoDesc.text = value.desc ?: ""
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameThreeDesc.text = value.desc ?: ""
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameFourDesc.text = value.desc ?: ""
                        }
                        "5" -> {
                            tvSwitchNameFive.text = value.name
                            switchFive.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameFiveDesc.text = value.desc ?: ""
                        }
                        "6" -> {
                            tvSwitchNameSix.text = value.name
                            switchSix.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameSixDesc.text = value.desc ?: ""
                        }
                        "7" -> {
                            tvSwitchNameSeven.text = value.name
                            switchSeven.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameSevenDesc.text = value.desc ?: ""
                        }
                        "8" -> {
                            tvSwitchNameEight.text = value.name
                            switchEight.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameEightDesc.text = value.desc ?: ""
                        }
                        "9" -> {
                            seekBar.setProgress(value.switchStatus.stringToFloat())
                        }
                        "10" -> {
                            switchPortA.isChecked = value.switchStatus.stringToInt().toBoolean()
                        }
                        "11" -> {
                            switchPortC.isChecked = value.switchStatus.stringToInt().toBoolean()
                        }
                    }
                }
            }

            switchOne.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString(),
                    tvSwitchNameOne.text.toString()
                )
            }

            switchTwo.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString(),
                    tvSwitchNameTwo.text.toString()
                )
            }

            switchThree.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString(),
                    tvSwitchNameThree.text.toString()
                )
            }

            switchFour.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString(),
                    tvSwitchNameFour.text.toString()
                )
            }

            switchFive.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_5,
                    switchFive.isChecked.toInt().toString(),
                    tvSwitchNameFive.text.toString()
                )
            }

            switchSix.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_6,
                    switchSix.isChecked.toInt().toString(),
                    tvSwitchNameSix.text.toString()
                )
            }

            switchSeven.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_7,
                    switchSeven.isChecked.toInt().toString(),
                    tvSwitchNameSeven.text.toString()
                )
            }

            switchEight.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_8,
                    switchEight.isChecked.toInt().toString(),
                    tvSwitchNameEight.text.toString()
                )
            }

            switchPortA.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_USB_PORT_A,
                    switchPortA.isChecked.toInt().toString()
                )
            }

            switchPortC.setOnClickListener {
                changeEightSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_USB_PORT_C,
                    switchPortC.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) = Unit

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) = Unit

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

    private fun changeEightSwitchStatus(holder: EightPanelViewHolder, isEnable: Boolean) {
        holder.apply {
            switchOne.isEnabled = isEnable
            switchTwo.isEnabled = isEnable
            switchThree.isEnabled = isEnable
            switchFour.isEnabled = isEnable
            switchFive.isEnabled = isEnable
            switchSix.isEnabled = isEnable
            switchSeven.isEnabled = isEnable
            switchEight.isEnabled = isEnable
            seekBar.isEnabled = isEnable
            switchPortA.isEnabled = isEnable
            switchPortC.isEnabled = isEnable
        }
    }

    private fun setFourSwitchViewHolder(holder: FourPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            changeFourSwitchStatus(this, true)
            try {
                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                relativeMain.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = relativeMain.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            relativeLayout.isVisible = !device.isDeviceAvailable.toBoolean()
            tvOutdoorModeIndication.isVisible = device.outdoorMode.toBoolean()

            if (device.outdoorMode.toBoolean()) {
                changeFourSwitchStatus(this, false)
            }

            /*if (device.productGroup.isSmartouch()) {
                seekBar.visibility = View.GONE
            }*/

            seekBar.isVisible = device.switchData?.find {
                it.desc?.lowercase() == mActivity.getString(R.string.text_switch_dimmer).lowercase()
            }?.switchStatus?.stringToInt()?.toBoolean() ?: false

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameOneDesc.text = value.desc ?: ""
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameTwoDesc.text = value.desc ?: ""
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameThreeDesc.text = value.desc ?: ""
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.stringToInt().toBoolean()
                            /*if (device.productGroup.isSmartAp()) {
                                seekBar.isVisible = switchFour.isChecked
                            }*/
                            tvSwitchNameFourDesc.text = value.desc ?: ""
                        }
                        "5" -> {
                            seekBar.setProgress(value.switchStatus.stringToFloat())
                        }
                        "6" -> {
                            switchPortC.isChecked = value.switchStatus.stringToInt().toBoolean()
                        }
                    }
                }
            }

            switchOne.setOnClickListener {
                changeFourSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString(),
                    tvSwitchNameOne.text.toString()
                )
            }

            switchTwo.setOnClickListener {
                changeFourSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString(),
                    tvSwitchNameTwo.text.toString()
                )
            }

            switchThree.setOnClickListener {
                changeFourSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString(),
                    tvSwitchNameThree.text.toString()
                )
            }

            switchFour.setOnClickListener {
                changeFourSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString(),
                    tvSwitchNameFour.text.toString()
                )
            }

            switchFour.setOnCheckedChangeListener { _, p1 ->
                changeFourSwitchStatus(this, false)
                if (device.productGroup.isSmartAp()) {
                    seekBar.isVisible = p1
                }
            }

            switchPortC.setOnClickListener {
                changeFourSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_USB_PORT_C,
                    switchPortC.isChecked.toInt().toString()
                )
            }
            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) = Unit

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) = Unit

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

    private fun changeFourSwitchStatus(holder: FourPanelViewHolder, isEnable: Boolean) {
        holder.apply {
            switchOne.isEnabled = isEnable
            switchTwo.isEnabled = isEnable
            switchThree.isEnabled = isEnable
            switchFour.isEnabled = isEnable
            seekBar.isEnabled = isEnable
            switchPortC.isEnabled = isEnable
        }
    }

    private fun setSmartAckViewHolder(holder: SmartAckPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            changeSmartAckSwitchStatus(this, true)
            try {
                val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                relativeMain.measure(wrapSpec, wrapSpec)

                val relativeParams = relativeLayout.layoutParams as RelativeLayout.LayoutParams

                relativeParams.height = relativeMain.measuredHeight
                relativeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                relativeLayout.layoutParams = relativeParams
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvPanelName.text = device.deviceName
            relativeLayout.isVisible = !device.isDeviceAvailable.toBoolean()
            tvOutdoorModeIndication.isVisible = device.outdoorMode.toBoolean()

            if (device.outdoorMode.toBoolean()) {
                changeSmartAckSwitchStatus(this, false)
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameOneDesc.text = value.desc ?: ""
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameTwoDesc.text = value.desc ?: ""
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameThreeDesc.text = value.desc ?: ""
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameFourDesc.text = value.desc ?: ""
                        }
                        "5" -> {
                            val switchName = value.name
                            tvSwitchNameFive.text = switchName
                            switchFive.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameFiveDesc.text = value.desc ?: ""

                            seekBar.isVisible = value.switchStatus.stringToInt().toBoolean()
                        }
                        "6" -> {
                            seekBar.setProgress(value.switchStatus.stringToFloat())
                        }
                    }
                }
            }

            switchOne.setOnClickListener {
                changeSmartAckSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString(),
                    tvSwitchNameOne.text.toString()
                )
            }

            switchTwo.setOnClickListener {
                changeSmartAckSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_2,
                    switchTwo.isChecked.toInt().toString(),
                    tvSwitchNameTwo.text.toString()
                )
            }

            switchThree.setOnClickListener {
                changeSmartAckSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_3,
                    switchThree.isChecked.toInt().toString(),
                    tvSwitchNameThree.text.toString()
                )
            }

            switchFour.setOnClickListener {
                changeSmartAckSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_4,
                    switchFour.isChecked.toInt().toString(),
                    tvSwitchNameFour.text.toString()
                )
            }

            switchFive.setOnClickListener {
                changeSmartAckSwitchStatus(this, false)
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_5,
                    switchFive.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) = Unit

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) = Unit

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

    private fun changeSmartAckSwitchStatus(holder: SmartAckPanelViewHolder, isEnable: Boolean) {
        holder.apply {
            switchOne.isEnabled = isEnable
            switchTwo.isEnabled = isEnable
            switchThree.isEnabled = isEnable
            switchFour.isEnabled = isEnable
            switchFive.isEnabled = isEnable
            seekBar.isEnabled = isEnable
        }
    }

    private fun setSmartApViewHolder(holder: SmartApPanelViewHolder, device: GetDeviceData) {
        holder.apply {

            switchOne.isEnabled = true

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
            relativeLayout.isVisible = !device.isDeviceAvailable.toBoolean()
            tvOutdoorModeIndication.isVisible = device.outdoorMode.toBoolean()

            if (device.outdoorMode.toBoolean()) {
                switchOne.isEnabled = false
            }

            var is15ADevice = false
            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            val switchName = value.name
                            tvSwitchNameOne.text = switchName
                            switchOne.isChecked = value.switchStatus.stringToInt().toBoolean()
                            tvSwitchNameOneDesc.text = value.desc ?: ""
                            value.desc?.let {

                                is15ADevice =
                                    it.lowercase() == mActivity.getString(R.string.text_15a)
                                        .lowercase()
                                rlSelectAppliances.isVisible = is15ADevice

                                if (!is15ADevice)
                                    return@let

                                val applianceAdapter = AppliancesAdapter(mActivity, applianceList)
                                spinnerAppliances.adapter = applianceAdapter
                                device.deviceAppliances?.let { dApp ->
                                    spinnerAppliances.setSelection(
                                        applianceAdapter.getPositionById(
                                            dApp
                                        )
                                    )
                                }

                                var check = 0

                                spinnerAppliances.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            p0: AdapterView<*>?,
                                            p1: View?,
                                            p2: Int,
                                            p3: Long
                                        ) {

                                            if (++check > 1) {
                                                val appliance = p0?.selectedItem as DeviceAppliances
                                                device.deviceAppliances = appliance.id
                                                publishAppliance(
                                                    device.deviceSerialNo,
                                                    appliance.title,
                                                    appliance.groupType
                                                )
                                            }
                                        }

                                        override fun onNothingSelected(p0: AdapterView<*>?) = Unit

                                    }
                                ivAppliancesDown.setOnClickListener {
                                    spinnerAppliances.performClick()
                                }
                            }
                        }
                    }
                }
            }

            switchOne.setOnClickListener {
                switchOne.isEnabled = false
                publish(
                    device.deviceSerialNo,
                    MQTTConstants.AWS_SWITCH_1,
                    switchOne.isChecked.toInt().toString()
                )

                if (!is15ADevice) {
                    return@setOnClickListener
                }

                val appliance = spinnerAppliances.selectedItem as DeviceAppliances

                publishAppliance(
                    device.deviceSerialNo,
                    appliance.title,
                    appliance.groupType
                )
            }
        }
    }

    //
    //region MQTT Methods
    //

    private fun subscribeToDevice(deviceId: String) {
        try {

            //Response of Get Switch status
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
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

                            if (jsonObject.has(MQTTConstants.AWS_DEVICE_TYPE)) {
                                if (jsonObject.getInt(MQTTConstants.AWS_DEVICE_TYPE) == Constants.DEVICE_TYPE_EIGHT || jsonObject.getInt(
                                        MQTTConstants.AWS_DEVICE_TYPE
                                    ) == Constants.DEVICE_TYPE_SIX
                                ) {
                                    deviceData?.switchData?.get(0)?.switchStatus = switchStatus[0]
                                    deviceData?.switchData?.get(1)?.switchStatus = switchStatus[1]
                                    deviceData?.switchData?.get(2)?.switchStatus = switchStatus[2]
                                    deviceData?.switchData?.get(3)?.switchStatus = switchStatus[3]
                                    deviceData?.switchData?.get(4)?.switchStatus = switchStatus[4]
                                    deviceData?.switchData?.get(5)?.switchStatus = switchStatus[5]
                                    deviceData?.switchData?.get(6)?.switchStatus = switchStatus[6]
                                    deviceData?.switchData?.get(7)?.switchStatus = switchStatus[7]
                                    deviceData?.switchData?.get(8)?.switchStatus =
                                        jsonObject.getString(MQTTConstants.AWS_DIMMER) //Dimmer
                                    deviceData?.switchData?.get(9)?.switchStatus =
                                        jsonObject.getString(MQTTConstants.AWS_USB_A) //USB A
                                    deviceData?.switchData?.get(10)?.switchStatus =
                                        jsonObject.getString(MQTTConstants.AWS_USB_C) //USB C
                                } else if (jsonObject.getInt(MQTTConstants.AWS_DEVICE_TYPE) == Constants.DEVICE_TYPE_FOUR) {
                                    deviceData?.switchData?.get(0)?.switchStatus = switchStatus[0]
                                    deviceData?.switchData?.get(1)?.switchStatus = switchStatus[1]
                                    deviceData?.switchData?.get(2)?.switchStatus = switchStatus[2]
                                    deviceData?.switchData?.get(3)?.switchStatus = switchStatus[3]
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
                                        deviceData?.switchData?.get(4)?.switchStatus =
                                            switchStatus[4] //Fan speed

                                        deviceData?.switchData?.get(5)?.switchStatus =
                                            jsonObject.getString(
                                                MQTTConstants.AWS_DIMMER
                                            ) //Dimmer
                                    }
                                } else if (jsonObject.getInt(MQTTConstants.AWS_DEVICE_TYPE) == Constants.DEVICE_TYPE_ONE) {
                                    deviceData?.switchData?.get(0)?.switchStatus = switchStatus[0]
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
                                val firstIndex = deviceList.indexOfFirst { it.id == dData.id }
                                deviceList[firstIndex] = dData
                            }
                        }

                        notifyDataSetChanged()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            //Device outdoor mode update - ON/OFF
            AwsMqttSingleton.mqttManager?.subscribeToTopic(
                MQTTConstants.OUTDOOR_MODE_ACK.replace(
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
                        // topic [3] = status

                        val deviceData = deviceList.find { it.deviceSerialNo == topic1[2] }

                        val jsonObject = JSONObject(message)

                        if (jsonObject.has(MQTTConstants.AWS_OUTDOOR_MODE)) {
                            deviceData?.outdoorMode =
                                jsonObject.getString(MQTTConstants.AWS_OUTDOOR_MODE)

                            deviceData?.let { dData ->
                                val firstIndex =
                                    deviceList.indexOfFirst { it.deviceSerialNo == dData.deviceSerialNo }
                                deviceList[firstIndex] = dData
                            }
                        }

                        notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    fun publishAppliance(
        deviceId: String, appliance: String, groupType: String
    ) {
        val payload = JSONObject()
        payload.put(MQTTConstants.AWS_APPLIANCES, appliance)
        payload.put(MQTTConstants.AWS_APPLIANCES_GROUP_TYPE, groupType)

        AwsMqttSingleton.publish(
            MQTTConstants.DEVICE_APPLIANCES.replace(
                MQTTConstants.AWS_DEVICE_ID,
                deviceId
            ), payload.toString()
        )
    }

    private fun publish(
        deviceId: String,
        switchIndex: String,
        switchValue: String,
        switchName: String = ""
    ) {
        val payload = JSONObject()
        payload.put(switchIndex, switchValue)
        if (switchName.isNotEmpty())
            payload.put(MQTTConstants.AWS_NAME, switchName)

        AwsMqttSingleton.publish(
            MQTTConstants.CONTROL_DEVICE_SWITCHES.replace(
                MQTTConstants.AWS_DEVICE_ID,
                deviceId
            ), payload.toString()
        )
    }

    private fun publishDimmer(deviceId: String, progress: String) {
        val payload = JSONObject()
        payload.put(MQTTConstants.AWS_DMR, progress)

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

}