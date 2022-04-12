package com.voinismartiot.voni.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.GetRoomData
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import java.util.regex.Pattern

class RoomsAdapter(
    private val roomList: List<GetRoomData>
) : RecyclerView.Adapter<RoomsAdapter.MyViewHolder>() {

    private var roomClickListener: AdapterItemClickListener<GetRoomData>? = null
    private var roomDeleteClickListener: AdapterItemClickListener<GetRoomData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_room, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = roomList[holder.adapterPosition]

        holder.apply {

            data.roomName?.let {
                val roomName = it.split(Pattern.compile(" "), 2)

                if (roomName.size > 1) {
                    val firstWord = "<B>${roomName[0]}</B> " + roomName[1]
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        tvRoomTitle.text = Html.fromHtml(firstWord, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        tvRoomTitle.text = Html.fromHtml(firstWord)
                    }
                } else {
                    val firstWord = "<B>${roomName[0]}</B>"
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        tvRoomTitle.text = Html.fromHtml(firstWord, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        tvRoomTitle.text = Html.fromHtml(firstWord)
                    }
                }
            }

            Glide
                .with(this.itemView.context)
                .load(data.roomTypeId?.file)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .placeholder(R.drawable.ic_room_placeholder)
                .into(ivRoomImage)

            itemView.setOnClickListener {
                roomClickListener?.onItemClick(data)
            }

            tvDeleteRoom.setOnClickListener {
                roomDeleteClickListener?.onItemClick(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomTitle: TextView = itemView.findViewById(R.id.tv_room_title)
        val ivRoomImage: ImageView = itemView.findViewById(R.id.iv_room_image)
        val tvDeleteRoom: ImageView = itemView.findViewById(R.id.iv_delete_room)
    }

    fun setCallback(listener: AdapterItemClickListener<GetRoomData>) {
        this.roomClickListener = listener
    }

    fun setDeleteCallback(listener: AdapterItemClickListener<GetRoomData>) {
        this.roomDeleteClickListener = listener
    }
}