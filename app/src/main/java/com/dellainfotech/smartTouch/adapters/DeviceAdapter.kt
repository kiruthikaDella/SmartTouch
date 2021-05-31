package com.dellainfotech.smartTouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.model.GetDeviceData
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.google.android.material.switchmaterial.SwitchMaterial
import com.warkiz.widget.IndicatorSeekBar

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
class DeviceAdapter(
    private val deviceList: List<GetDeviceData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var customizationClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var featuresClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var settingsClickListener: AdapterItemClickListener<GetDeviceData>? = null
    private var editSwitchNameClickListener: SwitchItemClickListener<GetDeviceData>? = null
    private var updateDeviceNameClickListener: DeviceItemClickListener<GetDeviceData>? = null

    private val EIGHT_PANEL_VIEW = 1
    private val FOUR_PANEL_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EIGHT_PANEL_VIEW -> {
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

        }
    }

    interface DeviceItemClickListener<T> {
        fun onItemClick(data: T, devicePosition: Int)
    }

    interface SwitchItemClickListener<T> {
        fun onItemClick(data: T, devicePosition: Int, switchData: DeviceSwitchData)
    }

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
}