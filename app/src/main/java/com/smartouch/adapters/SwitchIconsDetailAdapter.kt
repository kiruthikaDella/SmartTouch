package com.smartouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.AppDelegate
import com.smartouch.R
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.model.SwitchIconsDetailModel
import com.smartouch.model.SwitchIconsModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

class SwitchIconsDetailAdapter(
    private val switchList: List<SwitchIconsDetailModel>
) : RecyclerView.Adapter<SwitchIconsDetailAdapter.MyViewHolder>() {

    private var switchClickListener: AdapterItemClickListener<SwitchIconsDetailModel>? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSwitchName: TextView = itemView.findViewById(R.id.tv_switch)
        val ivSwitch: ImageView = itemView.findViewById(R.id.iv_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_switch_icons_detail, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return switchList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = switchList[position]

        holder.apply {
            tvSwitchName.text = data.title
            ivSwitch.setImageDrawable(
                ContextCompat.getDrawable(
                    AppDelegate.instance,
                    data.image
                )
            )

            itemView.setOnClickListener {
                switchClickListener?.onItemClick(data)
            }
        }
    }

    fun setOnSwitchClickListener(listener: AdapterItemClickListener<SwitchIconsDetailModel>) {
        this.switchClickListener = listener
    }
}