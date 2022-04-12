package com.voinismartiot.voni.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.model.WeeklyDaysModel

class WeeklyDaysAdapter(
    private var daysList: List<WeeklyDaysModel>
) : RecyclerView.Adapter<WeeklyDaysAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_weekly_days, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val dayData = daysList[adapterPosition]
            tvDay.text = dayData.day
            cbDay.isChecked = dayData.isChecked

            rlDay.setOnClickListener {
                dayData.isChecked = !dayData.isChecked
                cbDay.isChecked = dayData.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return daysList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rlDay = itemView.findViewById(R.id.rl_day) as RelativeLayout
        val tvDay = itemView.findViewById(R.id.tv_day) as TextView
        val cbDay = itemView.findViewById(R.id.cb_day) as CheckBox
    }

    fun getDayList(): ArrayList<String> {
        val arrayDays: ArrayList<String> = ArrayList()
        for (day in daysList) {
            if (day.isChecked)
                arrayDays.add(day.day.take(3))
        }

        return arrayDays
    }
}