package com.smartouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.R
import com.smartouch.model.RoomPanelModel

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */
class ControlModeAdapter(
    private val panelList: List<RoomPanelModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val EIGHT_PANEL_VIEW = 1
    private val FOUR_PANEL_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EIGHT_PANEL_VIEW -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_eight_panel, parent, false)
                EightPanelViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_control_mode_four_panel, parent, false)
                FourPanelViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = panelList[position]

        when (holder.itemViewType) {
            EIGHT_PANEL_VIEW -> {
                val eightPanelViewHolder: EightPanelViewHolder = holder as EightPanelViewHolder

                eightPanelViewHolder.apply {
                    tvPanelName.text = data.title
                }

            }
            FOUR_PANEL_VIEW -> {
                val fourPanelViewHolder: FourPanelViewHolder = holder as FourPanelViewHolder
                fourPanelViewHolder.apply {
                    tvPanelName.text = data.title
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return panelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (panelList[position].panelType == 1) {
            EIGHT_PANEL_VIEW
        } else {
            FOUR_PANEL_VIEW
        }
    }

    inner class EightPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
    }

    inner class FourPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPanelName = itemView.findViewById(R.id.tv_panel_name) as TextView
    }
}