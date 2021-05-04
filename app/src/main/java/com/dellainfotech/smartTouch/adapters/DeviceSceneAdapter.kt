package com.dellainfotech.smartTouch.adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.model.HomeRoomModel

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */
class DeviceSceneAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<DeviceSceneAdapter.MyViewHolder>() {

    private var roomClickListener: AdapterItemClickListener<HomeRoomModel>? = null
    val roomNames = arrayOf("Living Room", "Bedroom", "Kitchen", "Master Bedroom")
    val deviceNames =
        arrayOf("SMT00838234", "SMT00838235", "SMT00838236", "SMT00838237", "SMT00838238")
    val switchNames = arrayOf("Switch-1", "Switch-2", "Switch-3", "Switch-4", "Switch-5")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_scene, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val roomAdapter = ArrayAdapter(mContext, R.layout.simple_spinner_dropdown, roomNames)
            roomAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
            spinnerRoom.adapter = roomAdapter

            val deviceAdapter =
                ArrayAdapter(mContext, R.layout.simple_spinner_dropdown, deviceNames)
            deviceAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
            spinnerDevice.adapter = deviceAdapter

            val switchAdapter =
                ArrayAdapter(mContext, R.layout.simple_spinner_dropdown, switchNames)
            switchAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
            spinnerSwitch.adapter = switchAdapter

            spinnerRoom.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        parent?.getChildAt(0)?.let { mView ->
                            val textView = mView as TextView
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mContext,
                                    R.color.theme_color
                                )
                            )
                            textView.gravity = Gravity.CENTER
                            textView.setBackgroundColor(Color.TRANSPARENT)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            spinnerDevice.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        parent?.getChildAt(0)?.let { mView ->
                            val textView = mView as TextView
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mContext,
                                    R.color.theme_color
                                )
                            )
                            textView.gravity = Gravity.CENTER
                            textView.setBackgroundColor(Color.TRANSPARENT)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            spinnerSwitch.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        parent?.getChildAt(0)?.let { mView ->
                            val textView = mView as TextView
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mContext,
                                    R.color.theme_color
                                )
                            )
                            textView.gravity = Gravity.CENTER
                            textView.setBackgroundColor(Color.TRANSPARENT)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
        }

    }

    override fun getItemCount(): Int {
        return 3
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val spinnerRoom = itemView.findViewById(R.id.spinner_room_name) as Spinner
        val spinnerDevice = itemView.findViewById(R.id.spinner_device_name) as Spinner
        val spinnerSwitch = itemView.findViewById(R.id.spinner_switch_name) as Spinner

    }

    fun setCallback(listener: AdapterItemClickListener<HomeRoomModel>) {
        this.roomClickListener = listener
    }
}