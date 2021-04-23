package com.smartouch.ui.fragments.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartouch.R
import com.smartouch.common.utils.Constants
import com.smartouch.common.utils.dialog
import com.smartouch.databinding.FragmentDeviceCustomizationBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class DeviceCustomizationFragment : Fragment() {

    private lateinit var binding: FragmentDeviceCustomizationBinding
    private var textStyleDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceCustomizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sizeList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        context?.let {
            val sizeAdapter = ArrayAdapter(it, R.layout.simple_spinner_dropdown, sizeList)
            sizeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
            binding.spinnerIconSize.adapter = sizeAdapter
            binding.spinnerTextSize.adapter = sizeAdapter

            binding.spinnerIconSize.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
/*                    (parent?.getChildAt(0) as TextView)?.setTextColor(ContextCompat.getColor(it,R.color.theme_color))
                    (parent.getChildAt(0) as TextView)?.gravity = Gravity.CENTER*/
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            binding.spinnerTextSize.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
/*                    (parent?.getChildAt(0) as TextView)?.setTextColor(ContextCompat.getColor(it,R.color.theme_color))
                    (parent.getChildAt(0) as TextView)?.gravity = Gravity.CENTER*/
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
        }

        binding.ibLock.setOnClickListener {
            activity?.let {
                dialog.askAlert(
                    it,
                    getString(R.string.dialog_title_text_lock),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    null
                )
            }
        }

        binding.ivScreenLayoutSettings.setOnClickListener {
            findNavController().navigate(DeviceCustomizationFragmentDirections.actionDeviceCustomizationFragmentToScreenLayoutFragment())
        }

        binding.ivUploadImageSettings.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.ivHideUploadImagePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

        binding.ivTextStyleSettings.setOnClickListener {
            openTextStyleDialog()
        }
    }

    private fun openTextStyleDialog() {
        context?.let {
            textStyleDialog = Dialog(it,R.style.DialogSlideAnim)
            textStyleDialog?.setContentView(R.layout.layout_device_customization_text_style)
            textStyleDialog?.setCancelable(false)

            val ibHide = textStyleDialog?.findViewById(R.id.iv_hide_panel) as ImageButton

            ibHide.setOnClickListener {
                textStyleDialog?.dismiss()
            }

            textStyleDialog?.window?.setGravity(Gravity.BOTTOM)
            textStyleDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            textStyleDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            textStyleDialog?.show()
        }
    }
}