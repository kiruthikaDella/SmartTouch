package com.smartouch.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.model.RoomPanelModel

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */
class RoomPanelsAdapter(
    private val roomList: List<RoomPanelModel>
) : RecyclerView.Adapter<RoomPanelsAdapter.MyViewHolder>() {

    private var roomClickListener: AdapterItemClickListener<RoomPanelModel>? = null

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 0
    }
}