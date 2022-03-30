package com.voinismartiot.voni.adapters.spinneradapter

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.GetRoomData

class RoomAdapter(private val activity: Activity, private val items: List<GetRoomData>) :
    ArrayAdapter<GetRoomData?>(activity, R.layout.simple_spinner_dropdown, items) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.text = items[position].roomName

        if (position == 0) {
            textView.setTypeface(null, Typeface.ITALIC)
        }

        return textView
    }

    override fun getItem(position: Int): GetRoomData {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val inflater = activity.layoutInflater
            v = inflater.inflate(R.layout.scene_spinner_selected_text_layout, null)
        }
        val lbl = v!!.findViewById<TextView>(R.id.text1)
        lbl.text = items[position].roomName
        return v
    }

    fun getPositionById(roomId: String): Int {
        var position: Int = -1

        if (roomId.isEmpty())
            return position

        for ((index, value) in items.withIndex()) {
            if (value.id == roomId) {
                position = index
                break
            }
        }
        return position
    }
}