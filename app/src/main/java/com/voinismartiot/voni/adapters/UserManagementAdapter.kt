package com.voinismartiot.voni.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.SubordinateUserData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class UserManagementAdapter(
    private val userList: List<SubordinateUserData>
) : RecyclerView.Adapter<UserManagementAdapter.MyViewHolder>() {

    private var removeUserClickListener: AdapterItemClickListener<SubordinateUserData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_management, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = userList[position]
        holder.apply {
            tvUserName.text = data.fullName
            ivRemoveUser.setOnClickListener {
                removeUserClickListener?.onItemClick(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        val ivRemoveUser: ImageView = itemView.findViewById(R.id.iv_remove_user)
    }

    fun setOnRemoveClickListener(listener: AdapterItemClickListener<SubordinateUserData>) {
        this.removeUserClickListener = listener
    }

}