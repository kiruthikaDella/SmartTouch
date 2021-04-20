package com.smartouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.smartouch.R
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.model.RoomPanelModel
import com.warkiz.widget.IndicatorSeekBar

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
class RoomPanelsAdapter(
    private val panelList: List<RoomPanelModel>
) : RecyclerView.Adapter<RoomPanelsAdapter.MyViewHolder>() {

    private var customizationClickListener: AdapterItemClickListener<RoomPanelModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_eight_panel, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = panelList[position]

        holder.apply {
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
        }
    }

    override fun getItemCount(): Int {
        return panelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgBtnPanelMenu = itemView.findViewById(R.id.img_panel_menu) as ImageButton
        val imgBtnPanelEdit = itemView.findViewById(R.id.img_panel_edit) as ImageButton
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
        val linearPanelMenu = itemView.findViewById(R.id.linear_panel_menu) as LinearLayout

        val linearCustomization = itemView.findViewById(R.id.linear_customization) as LinearLayout

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

    fun setOnCustomizationClickListener(listener: AdapterItemClickListener<RoomPanelModel>) {
        this.customizationClickListener = listener
    }
}