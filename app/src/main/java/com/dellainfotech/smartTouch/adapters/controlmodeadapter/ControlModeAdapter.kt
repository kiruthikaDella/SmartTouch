package com.dellainfotech.smartTouch.adapters.controlmodeadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */
class ControlModeAdapter(
    private val panelList: List<ControlModeRoomData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_control_mode_section, parent, false)
        return SectionViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = panelList[position]

        val sectionViewHolder: SectionViewHolder = holder as SectionViewHolder
        sectionViewHolder.apply {
            tvRoomName.text = data.roomName
            data.deviceData?.let { deviceData ->
                recyclerDevices.adapter = ControlModeDeviceAdapter(deviceData)
            }
        }

    }

    override fun getItemCount(): Int {
        return panelList.size
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomName = itemView.findViewById(R.id.tv_room_name) as TextView
        val recyclerDevices =
            itemView.findViewById(R.id.recycler_control_modes_device) as RecyclerView
    }
}