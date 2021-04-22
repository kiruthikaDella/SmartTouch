package com.smartouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.R
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.model.HomeRoomModel

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */
class DeviceSceneAdapter() : RecyclerView.Adapter<DeviceSceneAdapter.MyViewHolder>() {

    private var roomClickListener: AdapterItemClickListener<HomeRoomModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_scene, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 3
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun setCallback(listener: AdapterItemClickListener<HomeRoomModel>) {
        this.roomClickListener = listener
    }
}