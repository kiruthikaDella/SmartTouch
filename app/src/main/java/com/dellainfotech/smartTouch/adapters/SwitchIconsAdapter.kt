package com.dellainfotech.smartTouch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

class SwitchIconsAdapter(
    private val switchList: ArrayList<DeviceSwitchData>
) : RecyclerView.Adapter<SwitchIconsAdapter.MyViewHolder>() {

    private var switchClickListener: AdapterItemClickListener<DeviceSwitchData>? = null
    private var mContext: Context? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSwitchName = itemView.findViewById(R.id.tv_switch_name) as TextView
        val ivSwitch = itemView.findViewById(R.id.iv_switch) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_switch_icons, parent, false)
        mContext = parent.context
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return switchList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = switchList[position]

        holder.apply {
            tvSwitchName.text = data.name

            itemView.setOnClickListener {
                switchClickListener?.onItemClick(data)
            }

            mContext?.let {
                Glide
                    .with(it)
                    .load(data.icon)
                    .placeholder(R.drawable.ic_switch)
                    .centerCrop()
                    .into(ivSwitch)
            }
        }
    }

    fun setOnSwitchClickListener(listener: AdapterItemClickListener<DeviceSwitchData>) {
        this.switchClickListener = listener
    }
}