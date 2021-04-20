package com.smartouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.R
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.model.HomeRoomModel

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */
class ControlModeAdapter(
    private val roomList: List<HomeRoomModel>
) : RecyclerView.Adapter<ControlModeAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_control_mode, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = roomList[position]

        holder.apply {
            tvPanelName.text = data.title
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }
}