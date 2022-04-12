package com.voinismartiot.voni.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.mqtt.MQTTConstants

class SwitchesAdapter(
    private var switchList: List<DeviceSwitchData>
) : RecyclerView.Adapter<SwitchesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_weekly_days, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val dayData = switchList[adapterPosition]
            tvDay.text = dayData.name
            cbDay.isChecked = dayData.isChecked

            rlDay.setOnClickListener {
                dayData.isChecked = !dayData.isChecked
                cbDay.isChecked = dayData.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return switchList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rlDay = itemView.findViewById(R.id.rl_day) as RelativeLayout
        val tvDay = itemView.findViewById(R.id.tv_day) as TextView
        val cbDay = itemView.findViewById(R.id.cb_day) as CheckBox
    }

    fun getSelectedSwitchIds(): ArrayList<String> {
        val arrayDays: ArrayList<String> = ArrayList()
        for (switch in switchList) {
            if (switch.isChecked)
                arrayDays.add(switch.id)
        }
        return arrayDays
    }

    fun getSelectedSwitchNames(): ArrayList<String> {
        val arrayDays: ArrayList<String> = ArrayList()
        for (switch in switchList) {
            if (switch.typeOfSwitch == 0 && switch.isChecked) {
                arrayDays.add("SW0${switch.index}")
            } else if (switch.typeOfSwitch == 2 && switch.isChecked) {
                if (switch.name == MQTTConstants.USB_A) {
                    arrayDays.add(MQTTConstants.AWS_USB_PORT_A)
                } else if (switch.name == MQTTConstants.USB_C) {
                    arrayDays.add(MQTTConstants.AWS_USB_PORT_C)
                }
            }
        }
        return arrayDays
    }
}