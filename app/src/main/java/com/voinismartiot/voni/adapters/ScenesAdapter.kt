package com.voinismartiot.voni.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.GetSceneData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toInt

class ScenesAdapter(
    private val sceneList: MutableList<GetSceneData>
) : RecyclerView.Adapter<ScenesAdapter.MyViewHolder>() {

    private var sceneClickListener: AdapterItemClickListener<GetSceneData>? = null
    private var deleteClickListener: AdapterItemClickListener<GetSceneData>? = null
    private var switchClickListener: SwitchItemClickListener<GetSceneData>? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSceneName = itemView.findViewById(R.id.tv_scene_name) as TextView
        val ibDeleteScene = itemView.findViewById(R.id.ib_delete) as ImageView
        val relativeLayout = itemView.findViewById(R.id.main_view) as RelativeLayout
        val switchSceneStatus = itemView.findViewById(R.id.switch_status) as SwitchMaterial
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scene2, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sceneList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val data = sceneList[adapterPosition]
            tvSceneName.text = data.sceneName
            switchSceneStatus.isChecked = data.isDeviceDisable.toBoolean()

            relativeLayout.setOnClickListener {
                if (data.isDeviceDisable != 0) {
                    sceneClickListener?.onItemClick(data)
                }
            }

            ibDeleteScene.setOnClickListener {
                deleteClickListener?.onItemClick(data)
            }

            switchSceneStatus.setOnClickListener {
                switchClickListener?.onItemClick(data, switchSceneStatus.isChecked.toInt())
            }
        }
    }

    interface SwitchItemClickListener<GetSceneData> {
        fun onItemClick(data: GetSceneData, sceneStatus: Int)
    }

    fun setOnClickListener(listener: AdapterItemClickListener<GetSceneData>) {
        this.sceneClickListener = listener
    }

    fun setOnDeleteClickListener(listener: AdapterItemClickListener<GetSceneData>) {
        this.deleteClickListener = listener
    }

    fun setOnSwitchClickListener(listener: SwitchItemClickListener<GetSceneData>) {
        this.switchClickListener = listener
    }
}