package com.dellainfotech.smartTouch.adapters.controlmodeadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.google.android.material.switchmaterial.SwitchMaterial
import com.warkiz.widget.IndicatorSeekBar

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
class ControlModeDeviceAdapter(
    private val deviceList: List<GetDeviceData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val EIGHT_PANEL_VIEW = 1
    private val FOUR_PANEL_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EIGHT_PANEL_VIEW -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_eight_panel, parent, false)
                EightPanelViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_four_panel, parent, false)
                FourPanelViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = deviceList[position]

        when (holder.itemViewType) {
            EIGHT_PANEL_VIEW -> {
                val eightPanelViewHolder: EightPanelViewHolder = holder as EightPanelViewHolder
                setEightSwitchViewHolder(eightPanelViewHolder, data)
            }
            FOUR_PANEL_VIEW -> {
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
            EIGHT_PANEL_VIEW
        } else {
            FOUR_PANEL_VIEW
        }
    }

    inner class EightPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView
        val tvSwitchNameFive = itemView.findViewById(R.id.tv_switch_five_name) as TextView
        val tvSwitchNameSix = itemView.findViewById(R.id.tv_switch_six_name) as TextView
        val tvSwitchNameSeven = itemView.findViewById(R.id.tv_switch_seven_name) as TextView
        val tvSwitchNameEight = itemView.findViewById(R.id.tv_switch_eight_name) as TextView

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

        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView

        val tvSwitchNameOne = itemView.findViewById(R.id.tv_switch_one_name) as TextView
        val tvSwitchNameTwo = itemView.findViewById(R.id.tv_switch_two_name) as TextView
        val tvSwitchNameThree = itemView.findViewById(R.id.tv_switch_three_name) as TextView
        val tvSwitchNameFour = itemView.findViewById(R.id.tv_switch_four_name) as TextView

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
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.toBoolean()
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.toBoolean()
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.toBoolean()
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.toBoolean()
                        }
                        "5" -> {
                            tvSwitchNameFive.text = value.name
                            switchFive.isChecked = value.switchStatus.toBoolean()
                        }
                        "6" -> {
                            tvSwitchNameSix.text = value.name
                            switchSix.isChecked = value.switchStatus.toBoolean()
                        }
                        "7" -> {
                            tvSwitchNameSeven.text = value.name
                            switchSeven.isChecked = value.switchStatus.toBoolean()
                        }
                        "8" -> {
                            tvSwitchNameEight.text = value.name
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

        }
    }

    private fun setFourSwitchViewHolder(holder: FourPanelViewHolder, device: GetDeviceData) {
        holder.apply {
            tvPanelName.text = device.deviceName

            device.switchData?.let { switchData ->
                for (value in switchData) {
                    when (value.index) {
                        "1" -> {
                            tvSwitchNameOne.text = value.name
                            switchOne.isChecked = value.switchStatus.toBoolean()
                        }
                        "2" -> {
                            tvSwitchNameTwo.text = value.name
                            switchTwo.isChecked = value.switchStatus.toBoolean()
                        }
                        "3" -> {
                            tvSwitchNameThree.text = value.name
                            switchThree.isChecked = value.switchStatus.toBoolean()
                        }
                        "4" -> {
                            tvSwitchNameFour.text = value.name
                            switchFour.isChecked = value.switchStatus.toBoolean()
                        }
                        "5" -> {
                            seekBar.setProgress(value.switchStatus.toFloat())
                        }
                        "7" -> {
                            switchPortC.isChecked = value.switchStatus.toBoolean()
                        }
                    }
                }
            }

        }
    }

}