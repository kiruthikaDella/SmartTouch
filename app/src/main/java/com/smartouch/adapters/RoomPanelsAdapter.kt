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

    private var roomClickListener: AdapterItemClickListener<RoomPanelModel>? = null

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
                if (linearPanelMenu.isVisible){
                    linearPanelMenu.visibility = View.GONE
                }else{
                    linearPanelMenu.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return panelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgBtnPanelMenu = itemView.findViewById<ImageButton>(R.id.img_panel_menu)
        val imgBtnPanelEdit = itemView.findViewById<ImageButton>(R.id.img_panel_edit)
        val tvPanelName = itemView.findViewById<TextView>(R.id.tv_panel_name)
        val linearPanelMenu = itemView.findViewById<LinearLayout>(R.id.linear_panel_menu)

        val tvSwitchNameOne = itemView.findViewById<TextView>(R.id.tv_switch_one_name)
        val tvSwitchNameTwo = itemView.findViewById<TextView>(R.id.tv_switch_two_name)
        val tvSwitchNameThree = itemView.findViewById<TextView>(R.id.tv_switch_three_name)
        val tvSwitchNameFour = itemView.findViewById<TextView>(R.id.tv_switch_four_name)
        val tvSwitchNameFive = itemView.findViewById<TextView>(R.id.tv_switch_five_name)
        val tvSwitchNameSix = itemView.findViewById<TextView>(R.id.tv_switch_six_name)
        val tvSwitchNameSeven = itemView.findViewById<TextView>(R.id.tv_switch_seven_name)
        val tvSwitchNameEight = itemView.findViewById<TextView>(R.id.tv_switch_eight_name)

        val tvSwitchOneEdit = itemView.findViewById<TextView>(R.id.tv_switch_one_edit)
        val tvSwitchTwoEdit = itemView.findViewById<TextView>(R.id.tv_switch_two_edit)
        val tvSwitchThreeEdit = itemView.findViewById<TextView>(R.id.tv_switch_three_edit)
        val tvSwitchFourEdit = itemView.findViewById<TextView>(R.id.tv_switch_four_edit)
        val tvSwitchFiveEdit = itemView.findViewById<TextView>(R.id.tv_switch_five_edit)
        val tvSwitchSixEdit = itemView.findViewById<TextView>(R.id.tv_switch_six_edit)
        val tvSwitchsevenEdit = itemView.findViewById<TextView>(R.id.tv_switch_seven_edit)
        val tvSwitchEightEdit = itemView.findViewById<TextView>(R.id.tv_switch_eight_edit)

        val tvSwitchOne = itemView.findViewById<SwitchMaterial>(R.id.switch_one)
        val tvSwitchTwo = itemView.findViewById<SwitchMaterial>(R.id.switch_two)
        val tvSwitchThree = itemView.findViewById<SwitchMaterial>(R.id.switch_three)
        val tvSwitchFour = itemView.findViewById<SwitchMaterial>(R.id.switch_four)
        val tvSwitchFive = itemView.findViewById<SwitchMaterial>(R.id.switch_five)
        val tvSwitchSix = itemView.findViewById<SwitchMaterial>(R.id.switch_six)
        val tvSwitchSeven = itemView.findViewById<SwitchMaterial>(R.id.switch_seven)
        val tvSwitchEight = itemView.findViewById<SwitchMaterial>(R.id.switch_eight)

        val seekBar = itemView.findViewById<IndicatorSeekBar>(R.id.seek_bar)

        val tvSwitchPortA = itemView.findViewById<SwitchMaterial>(R.id.switch_usb_port_a)
        val tvSwitchPortC = itemView.findViewById<SwitchMaterial>(R.id.switch_usb_port_c)

    }
}