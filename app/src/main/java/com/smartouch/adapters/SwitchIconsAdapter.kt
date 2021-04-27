package com.smartouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.R
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.model.SwitchIconsModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

class SwitchIconsAdapter(
    private val switchList: List<SwitchIconsModel>
) : RecyclerView.Adapter<SwitchIconsAdapter.MyViewHolder>() {

    private var switchClickListener: AdapterItemClickListener<SwitchIconsModel>? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSwitchName: TextView = itemView.findViewById(R.id.tv_switch_name)
        val tvSwitchNumber: TextView = itemView.findViewById(R.id.tv_switch_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_switch_icons, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return switchList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = switchList[position]

        holder.apply {
            tvSwitchName.text = data.switchName
            tvSwitchNumber.text = data.switchNumber

            itemView.setOnClickListener {
                switchClickListener?.onItemClick(data)
            }
        }
    }

    fun setOnSwitchClickListener(listener: AdapterItemClickListener<SwitchIconsModel>) {
        this.switchClickListener = listener
    }
}