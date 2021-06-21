package com.dellainfotech.smartTouch.adapters.spinneradapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.dellainfotech.smartTouch.R

class SpinnerAdapter(private val activity: Activity, private val items: List<String>) :
    ArrayAdapter<String?>(activity, R.layout.simple_spinner_dropdown, items) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.text = items[position]
        return textView
    }

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val inflater = activity.layoutInflater
            v = inflater.inflate(R.layout.scene_spinner_selected_text_layout, null)
        }
        val lbl = v!!.findViewById<TextView>(R.id.text1)
        lbl.text = items[position]
        return v
    }
}