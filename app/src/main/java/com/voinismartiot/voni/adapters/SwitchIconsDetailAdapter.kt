package com.voinismartiot.voni.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.api.model.IconListData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

class SwitchIconsDetailAdapter(
    private val iconList: ArrayList<IconListData>
) : RecyclerView.Adapter<SwitchIconsDetailAdapter.MyViewHolder>() {

    private var mContext: Context? = null
    private var switchClickListener: AdapterItemClickListener<IconListData>? = null
    private var rowIndex = -1

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSwitchName: TextView = itemView.findViewById(R.id.tv_switch)
        val ivSwitch: ImageView = itemView.findViewById(R.id.iv_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_switch_icons_detail, parent, false)
        mContext = parent.context
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = iconList[position]

        holder.apply {
            tvSwitchName.text = data.iconName

            mContext?.let {
                Glide
                    .with(it)
                    .load(data.icon)
                    .centerCrop()
                    .into(ivSwitch)
            }

            itemView.setOnClickListener {
                rowIndex = position
                switchClickListener?.onItemClick(data)
                notifyDataSetChanged()
            }

            if (rowIndex == position) {
                mContext?.let { ctx ->
                    ivSwitch.setColorFilter(ctx.getColor(R.color.white))
                    ivSwitch.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.theme_color)
                }
            } else {
                mContext?.let { ctx ->
                    ivSwitch.setColorFilter(ctx.getColor(R.color.theme_color))
                    ivSwitch.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.white)
                }
            }
        }
    }

    fun selectIcon(switchData: DeviceSwitchData): IconListData? {
        var iconData: IconListData? = null
        for ((index, value) in iconList.withIndex()) {
            if (value.icon == switchData.icon) {
                rowIndex = index
                iconData = value
                break
            }
        }
        notifyDataSetChanged()
        return iconData
    }

    fun setOnSwitchClickListener(listener: AdapterItemClickListener<IconListData>) {
        this.switchClickListener = listener
    }
}