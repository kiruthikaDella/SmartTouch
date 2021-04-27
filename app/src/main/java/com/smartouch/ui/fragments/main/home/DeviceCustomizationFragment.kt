package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartouch.R
import com.smartouch.common.utils.dialog
import com.smartouch.databinding.FragmentDeviceCustomizationBinding
import com.smartouch.ui.fragments.BaseFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */

class DeviceCustomizationFragment : BaseFragment() {

    private lateinit var binding: FragmentDeviceCustomizationBinding

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
            binding.tvBottomViewTitle.text = getString(R.string.text_upload_image)
            binding.layoutTextStyle.linearTextStyle.isVisible = false
            binding.layoutUploadImage.linearUploadImage.isVisible = true

            Handler(Looper.getMainLooper()).postDelayed({
                binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }, 500)
        }

        binding.ivHideUploadImagePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

        binding.ivTextStyleSettings.setOnClickListener {
            binding.tvBottomViewTitle.text = getString(R.string.text_style)
            binding.layoutUploadImage.linearUploadImage.isVisible = false
            binding.layoutTextStyle.linearTextStyle.isVisible = true

            Handler(Looper.getMainLooper()).postDelayed({
                binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }, 500)

        }

        binding.ivSwitchIconsSettings.setOnClickListener {
            findNavController().navigate(DeviceCustomizationFragmentDirections.actionDeviceCustomizationFragmentToSwitchIconsFragment())
        }
    }

}