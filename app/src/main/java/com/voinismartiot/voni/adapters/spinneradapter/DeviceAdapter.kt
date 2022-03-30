package com.voinismartiot.voni.adapters.spinneradapter

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.GetDeviceData

class DeviceAdapter(private val activity: Activity, private val items: List<GetDeviceData>) :
    ArrayAdapter<GetDeviceData?>(activity, R.layout.simple_spinner_dropdown, items) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.text = items[position].deviceName

        if (position == 0) {
            textView.setTypeface(null, Typeface.ITALIC)
        }

        return textView
    }

    override fun getItem(position: Int): GetDeviceData {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val inflater = activity.layoutInflater
            v = inflater.inflate(R.layout.scene_spinner_selected_text_layout, null)
        }
        val lbl = v!!.findViewById<TextView>(R.id.text1)
        lbl.text = items[position].deviceName
        return v
    }

    fun getPositionById(deviceId: String): Int {
        var position: Int = -1

        if (deviceId.isEmpty())
            return position

        for ((index, value) in items.withIndex()) {
            if (value.id == deviceId) {
                position = index
                break
            }
        }
        return position
    }
}