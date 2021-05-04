package com.dellainfotech.smartTouch.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.AppDelegate
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.model.HomeRoomModel
import java.util.regex.Pattern

/**
 * Created by Jignesh Dangar on 14-04-2021.
 */
class HomeRoomsAdapter(
    private val roomList: List<HomeRoomModel>
) : RecyclerView.Adapter<HomeRoomsAdapter.MyViewHolder>() {

    private var roomClickListener: AdapterItemClickListener<HomeRoomModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_room, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = roomList[position]

        holder.apply {
            val roomName =
                data.title.split(Pattern.compile(" "), 2)

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
            tvRoomImage.setImageDrawable(
                ContextCompat.getDrawable(
                    AppDelegate.instance,
                    data.image
                )
            )

            tvRoomSettings.setOnClickListener {
                roomClickListener?.onItemClick(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomTitle: TextView = itemView.findViewById(R.id.tv_room_title)
        val tvRoomImage: ImageView = itemView.findViewById(R.id.iv_room_image)
        val tvRoomSettings: ImageView = itemView.findViewById(R.id.iv_room_settings)
    }

    fun setCallback(listener: AdapterItemClickListener<HomeRoomModel>) {
        this.roomClickListener = listener
    }
}