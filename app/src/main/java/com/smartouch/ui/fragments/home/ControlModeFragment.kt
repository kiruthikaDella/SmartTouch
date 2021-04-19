package com.smartouch.ui.fragments.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.smartouch.R
import com.smartouch.adapters.ControlModeAdapter
import com.smartouch.common.utils.Constants
import com.smartouch.databinding.FragmentControlModeBinding

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */

class ControlModeFragment : Fragment() {

    private lateinit var binding: FragmentControlModeBinding
    private lateinit var controlModeAdapter: ControlModeAdapter
    private var dialog: Dialog? = null

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

        controlModeAdapter = ControlModeAdapter()
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