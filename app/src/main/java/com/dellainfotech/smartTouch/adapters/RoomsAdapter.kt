package com.dellainfotech.smartTouch.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.model.GetRoomData
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import java.util.regex.Pattern

/**
 * Created by Jignesh Dangar on 14-04-2021.
 */
class RoomsAdapter(
    private val roomList: List<GetRoomData>
) : RecyclerView.Adapter<RoomsAdapter.MyViewHolder>() {

    private var roomClickListener: AdapterItemClickListener<GetRoomData>? = null
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_room, parent, false)
        mContext = parent.context
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = roomList[position]

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

            mContext?.let {
                Glide
                    .with(it)
                    .load(data.roomTypeId?.file)
                    .centerCrop()
                    .into(ivRoomImage)
            }

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
        val ivRoomImage: ImageView = itemView.findViewById(R.id.iv_room_image)
        val tvRoomSettings: ImageView = itemView.findViewById(R.id.iv_room_settings)
    }

    fun setCallback(listener: AdapterItemClickListener<GetRoomData>) {
        this.roomClickListener = listener
    }
}