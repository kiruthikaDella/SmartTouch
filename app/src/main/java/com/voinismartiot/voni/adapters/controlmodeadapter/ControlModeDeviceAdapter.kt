package com.voinismartiot.voni.adapters.controlmodeadapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.GetDeviceData
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
class ControlModeDeviceAdapter(
    private val mActivity: Activity,
    private val deviceList: ArrayList<GetDeviceData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val logTag = this::class.java.simpleName

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
                    .inflate(R.layout.item_control_mode_room_smart_ack, parent, false)
                SmartApPanelViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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
        return if (deviceList[position].productGroup == Constants.PRODUCT_SMART_TOUCH) {
            if (deviceList[position].deviceType == Constants.DEVICE_TYPE_EIGHT) {
                eightPanelView
            } else {
                fourPanelView
            }
        } else if (deviceList[position].productGroup == Constants.PRODUCT_SMART_ACK) {
            smartAckPanelView
        } else {
            smartApPanelView
        }
    }

    inner class EightPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val relativeMain = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView
        val tvSwitchNameFive = itemView.findViewById(R.id.tv_switch_five_name) as TextView
        val tvSwitchNameSix = itemView.findViewById(R.id.tv_switch_six_name) as TextView
        val tvSwitchNameSeven = itemView.findViewById(R.id.tv_switch_seven_name) as TextView
        val tvSwitchNameEight = itemView.findViewById(R.id.tv_switch_eight_name) as TextView

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
        val relativeMain = itemView.findViewById(R.id.relative_main) as RelativeLayout
        val relativeLayout = itemView.findViewById(R.id.relative_layout) as RelativeLayout

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

    private fun setEightSwitchViewHolder(holder: EightPanelViewHolder, device: GetDeviceData) {
        holder.apply {

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

            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "5" -> {
                            tvSwitchNameFive.text = value.name
                            switchFive.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "6" -> {
                            tvSwitchNameSix.text = value.name
                            switchSix.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameSixDesc.text = it
                            }
                        }
                        "7" -> {
                            tvSwitchNameSeven.text = value.name
                            switchSeven.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameSevenDesc.text = it
                            }
                        }
                        "8" -> {
                            tvSwitchNameEight.text = value.name
                            switchEight.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameEightDesc.text = it
                            }
                        }
                        "9" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "10" -> {
                            switchPortA.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                        "11" -> {
                            switchPortC.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                    }
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
                    seekParams?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

            }

        }
    }

    private fun setFourSwitchViewHolder(holder: FourPanelViewHolder, device: GetDeviceData) {
        holder.apply {

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

            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameOneDesc.text = it
                            }
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameTwoDesc.text = it
                            }
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameThreeDesc.text = it
                            }
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameFourDesc.text = it
                            }
                        }
                        "5" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "6" -> {
                            switchPortC.isChecked = value.switchStatus.toInt().toBoolean()
                        }
                    }
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
                    seekParams?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

            }

        }
    }

    private fun setSmartAckViewHolder(holder: SmartAckPanelViewHolder, device: GetDeviceData) {
        holder.apply {

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

            if (device.isDeviceAvailable == "0") {
                relativeLayout.visibility = View.VISIBLE
            } else {
                relativeLayout.visibility = View.GONE
            }

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameOneDesc.text = it
                            }
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameTwoDesc.text = it
                            }
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.toInt().toBoolean()
                            value.desc?.let {
                                tvSwitchNameThreeDesc.text = it
                            }
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
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
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                    }
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
                    switchFive.isChecked.toInt().toString()
                )
            }

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    seekParams?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

            }

        }
    }

    private fun setSmartApViewHolder(holder: SmartApPanelViewHolder, device: GetDeviceData) {
        holder.apply {

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
                            try {
                                seekBar.setProgress(value.switchStatus.toFloat())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
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
                    seekParams?.progress?.let {
                        publishDimmer(
                            device.deviceSerialNo,
                            it.toString()
                        )
                    }
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

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
                                        deviceData?.switchData?.get(4)?.switchStatus =
                                            switchStatus[4] //Fan speed

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
                                val firstIndex = deviceList.indexOfFirst { it.id == dData.id }
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

        Log.e(logTag, " publish payload $payload")

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