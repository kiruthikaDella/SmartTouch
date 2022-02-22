package com.voinismartiot.voni.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.DeviceSwitchData
import com.voinismartiot.voni.api.model.IconListData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener

class SwitchIconsDetailAdapter(
    private val iconList: ArrayList<IconListData>
) : RecyclerView.Adapter<SwitchIconsDetailAdapter.MyViewHolder>() {

    private var switchClickListener: AdapterItemClickListener<IconListData>? = null
    private var rowIndex = -1

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSwitchName: TextView = itemView.findViewById(R.id.tv_switch)
        val ivSwitch: ImageView = itemView.findViewById(R.id.iv_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_switch_icons_detail, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val data = iconList[adapterPosition]

            tvSwitchName.text = data.iconName

            Glide
                .with(this.itemView.context)
                .load(data.icon)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(ivSwitch)

            itemView.setOnClickListener {
                rowIndex = adapterPosition
                switchClickListener?.onItemClick(data)
                notifyDataSetChanged()
            }

            if (rowIndex == adapterPosition) {
                ivSwitch.setColorFilter(this.itemView.context.getColor(R.color.white))
                ivSwitch.backgroundTintList =
                    ContextCompat.getColorStateList(this.itemView.context, R.color.theme_color)
            } else {
                ivSwitch.setColorFilter(this.itemView.context.getColor(R.color.theme_color))
                ivSwitch.backgroundTintList =
                    ContextCompat.getColorStateList(this.itemView.context, R.color.white)
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