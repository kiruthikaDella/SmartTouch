package com.dellainfotech.smartTouch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.GetSceneData
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener

/**
 * Created by Jignesh Dangar on 03-06-2021.
 */

class ScenesAdapter(
    private val sceneList: ArrayList<GetSceneData>
) : RecyclerView.Adapter<ScenesAdapter.MyViewHolder>() {

    private var sceneClickListener: AdapterItemClickListener<GetSceneData>? = null
    private var deleteClickListener: AdapterItemClickListener<GetSceneData>? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSceneName = itemView.findViewById(R.id.tv_scene_name) as TextView
        val ibDeleteScene = itemView.findViewById(R.id.ib_delete) as ImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scene, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sceneList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = sceneList[position]

        holder.apply {
            tvSceneName.text = data.sceneName

            itemView.setOnClickListener {
                sceneClickListener?.onItemClick(data)
            }

            ibDeleteScene.setOnClickListener {
                deleteClickListener?.onItemClick(data)
            }
        }
    }

    fun setOnClickListener(listener: AdapterItemClickListener<GetSceneData>) {
        this.sceneClickListener = listener
    }

    fun setOnDeleteClickListener(listener: AdapterItemClickListener<GetSceneData>) {
        this.deleteClickListener = listener
    }
}