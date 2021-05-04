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
import com.google.android.material.switchmaterial.SwitchMaterial
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.model.RoomPanelModel
import com.warkiz.widget.IndicatorSeekBar

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
class RoomPanelsAdapter(
    private val panelList: List<RoomPanelModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var customizationClickListener: AdapterItemClickListener<RoomPanelModel>? = null
    private var featuresClickListener: AdapterItemClickListener<RoomPanelModel>? = null
    private var settingsClickListener: AdapterItemClickListener<RoomPanelModel>? = null
    private var editClickListener: AdapterItemClickListener<RoomPanelModel>? = null

    private val EIGHT_PANEL_VIEW = 1
    private val FOUR_PANEL_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
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
        val data = panelList[position]

        when(holder.itemViewType){
            EIGHT_PANEL_VIEW -> {
                val eightPanelViewHolder: EightPanelViewHolder = holder as EightPanelViewHolder

                eightPanelViewHolder.apply {
                    tvPanelName.text = data.title

                    imgBtnPanelMenu.setOnClickListener {
                        if (linearPanelMenu.isVisible) {
                            linearPanelMenu.visibility = View.GONE
                        } else {
                            linearPanelMenu.visibility = View.VISIBLE
                        }
                    }

                    linearCustomization.setOnClickListener {
                        customizationClickListener?.onItemClick(data)
                    }

                    linearFeature.setOnClickListener {
                        featuresClickListener?.onItemClick(data)
                    }

                    linearDeviceSettings.setOnClickListener {
                        settingsClickListener?.onItemClick(data)
                    }

                    tvSwitchOneEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }

                    tvSwitchTwoEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }
                    tvSwitchThreeEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }
                    tvSwitchFourEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }
                    tvSwitchFiveEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }
                    tvSwitchSixEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }
                    tvSwitchSevenEdit.setOnClickListener {
                        editClickListener?.onItemClick(data)
                    }
                }

            }
            FOUR_PANEL_VIEW -> {
                val fourPanelViewHolder: FourPanelViewHolder = holder as FourPanelViewHolder
            }
        }

    }

    override fun getItemCount(): Int {
        return panelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (panelList[position].panelType == 1){
            EIGHT_PANEL_VIEW
        }else {
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

        val tvSwitchOne = itemView.findViewById(R.id.switch_one) as SwitchMaterial
        val tvSwitchTwo = itemView.findViewById(R.id.switch_two) as SwitchMaterial
        val tvSwitchThree = itemView.findViewById(R.id.switch_three) as SwitchMaterial
        val tvSwitchFour = itemView.findViewById(R.id.switch_four) as SwitchMaterial
        val tvSwitchFive = itemView.findViewById(R.id.switch_five) as SwitchMaterial
        val tvSwitchSix = itemView.findViewById(R.id.switch_six) as SwitchMaterial
        val tvSwitchSeven = itemView.findViewById(R.id.switch_seven) as SwitchMaterial
        val tvSwitchEight = itemView.findViewById(R.id.switch_eight) as SwitchMaterial

        val seekBar = itemView.findViewById(R.id.seek_bar) as IndicatorSeekBar

        val tvSwitchPortA = itemView.findViewById(R.id.switch_usb_port_a) as SwitchMaterial
        val tvSwitchPortC = itemView.findViewById(R.id.switch_usb_port_c) as SwitchMaterial

    }

    inner class FourPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun setOnCustomizationClickListener(listener: AdapterItemClickListener<RoomPanelModel>) {
        this.customizationClickListener = listener
    }

    fun setOnFeaturesClickListener(listener: AdapterItemClickListener<RoomPanelModel>) {
        this.featuresClickListener = listener
    }

    fun setOnSettingsClickListener(listener: AdapterItemClickListener<RoomPanelModel>) {
        this.settingsClickListener = listener
    }

    fun setOnEditClickListener(listener: AdapterItemClickListener<RoomPanelModel>) {
        this.editClickListener = listener
    }
}