package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentDeviceCustomizationBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */

class DeviceCustomizationFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceCustomizationBinding, HomeRepository>() {

    private val args: DeviceCustomizationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sizeList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val fontNames =
            arrayOf("Times New Roman", "Roboto", "Montserrat", "Lato", "Krona One", "Arial")

        if (FastSave.getInstance().getBoolean(
                Constants.isDeviceCustomizationLocked,
                Constants.DEFAULT_DEVICE_CUSTOMIZATION_LOCK_STATUS
            )
        ) {
            lockScreen()
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ibLock.setOnClickListener {
            activity?.let {
                DialogUtil.askAlert(
                    it,
                    getString(R.string.dialog_title_text_lock),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            FastSave.getInstance()
                                .saveBoolean(Constants.isDeviceCustomizationLocked, true)
                            lockScreen()
                        }

                        override fun onNoClicked() {
                            FastSave.getInstance()
                                .saveBoolean(Constants.isDeviceCustomizationLocked, false)
                            unLockScreen()
                        }

                    }
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
            }, 600)
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
            }, 600)

        }

        binding.ivSwitchIconsSettings.setOnClickListener {
            findNavController().navigate(DeviceCustomizationFragmentDirections.actionDeviceCustomizationFragmentToSwitchIconsFragment())
        }

        context?.let { mContext ->

            val sizeAdapter = ArrayAdapter(mContext, R.layout.simple_spinner_dropdown, sizeList)
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
                        parent?.getChildAt(0)?.let { mView ->
                            val textView = mView as TextView
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mContext,
                                    R.color.theme_color
                                )
                            )
                            textView.gravity = Gravity.CENTER
                        }
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
                        parent?.getChildAt(0)?.let { mView ->
                            val textView = mView as TextView
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mContext,
                                    R.color.theme_color
                                )
                            )
                            textView.gravity = Gravity.CENTER
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }

            val roomAdapter = ArrayAdapter(mContext, R.layout.simple_spinner_dropdown, fontNames)
            roomAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
            binding.layoutTextStyle.spinnerFonts.adapter = roomAdapter

            binding.layoutTextStyle.spinnerFonts.onItemSelectedListener =
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

    override fun onResume() {
        super.onResume()
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    private fun lockScreen() {
        binding.relativeLock.isVisible = true
    }

    private fun unLockScreen() {
        binding.relativeLock.isVisible = false
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeviceCustomizationBinding =
        FragmentDeviceCustomizationBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}