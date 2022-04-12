package com.voinismartiot.voni.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener

class SwitchIconsAdapter(
    private val switchList: ArrayList<DeviceSwitchData>
) : RecyclerView.Adapter<SwitchIconsAdapter.MyViewHolder>() {

    private var switchClickListener: AdapterItemClickListener<DeviceSwitchData>? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSwitchName = itemView.findViewById(R.id.tv_switch_name) as TextView
        val ivSwitch = itemView.findViewById(R.id.iv_switch) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_switch_icons, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return switchList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val data = switchList[adapterPosition]
            tvSwitchName.text = data.name

            itemView.setOnClickListener {
                switchClickListener?.onItemClick(data)
            }

            Glide
                .with(this.itemView.context)
                .load(data.icon)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_switch)
                .centerCrop()
                .into(ivSwitch)
        }
    }

    fun setOnSwitchClickListener(listener: AdapterItemClickListener<DeviceSwitchData>) {
        this.switchClickListener = listener
    }
}