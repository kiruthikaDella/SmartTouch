package com.smartouch.ui.fragments.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.smartouch.R
import com.smartouch.adapters.ControlModeAdapter
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.common.utils.Constants
import com.smartouch.databinding.FragmentControlModeBinding
import com.smartouch.model.HomeRoomModel

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */

class ControlModeFragment : Fragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentControlModeBinding
    private lateinit var controlModeAdapter: ControlModeAdapter
    private var dialog: Dialog? = null
    private var roomList = arrayListOf<HomeRoomModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentControlModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ibPin.setOnClickListener {
            askPinDialog()
        }
        roomList.add(
            HomeRoomModel(
                R.drawable.img_living_room,
                getString(R.string.text_living_room)
            )
        )
        roomList.add(HomeRoomModel(R.drawable.img_bedroom, getString(R.string.text_bedroom)))
        roomList.add(HomeRoomModel(R.drawable.img_kitchen, getString(R.string.text_kitchen)))
        roomList.add(
            HomeRoomModel(
                R.drawable.img_master_bedroom,
                getString(R.string.text_master_bedroom)
            )
        )
        controlModeAdapter = ControlModeAdapter(roomList)
        binding.recyclerControlModes.adapter = controlModeAdapter
    }

    private fun askPinDialog() {
        dialog?.dismiss()
        context?.let {
            dialog = Dialog(it)
            dialog?.setContentView(R.layout.dialog_pin_control_mode)
            dialog?.setCancelable(true)


            val btnCancel = dialog?.findViewById(R.id.tv_cancel) as TextView
            val btnOk = dialog?.findViewById(R.id.tv_ok) as TextView

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }

            btnOk.setOnClickListener {
                dialog?.dismiss()
            }

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
            val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(width.toInt(), height.toInt())
            dialog?.show()
        }

    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}