package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.smartouch.R
import com.smartouch.common.interfaces.DialogAskListener
import com.smartouch.common.utils.Constants
import com.smartouch.common.utils.DialogUtil
import com.smartouch.databinding.FragmentScreenLayoutBinding
import com.smartouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */
class ScreenLayoutFragment : BaseFragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentScreenLayoutBinding
    private var screenLayoutModel: ScreenLayoutModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreenLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            screenLayoutModel = ScreenLayoutModel(it, binding)
        }

        screenLayoutModel?.init()

        if (FastSave.getInstance().getBoolean(
                Constants.isScreenLayoutLocked,
                Constants.DEFAULT_SCREEN_LAYOUT_LOCK_STATUS
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
                            FastSave.getInstance().saveBoolean(Constants.isScreenLayoutLocked, true)
                            lockScreen()
                        }

                        override fun onNoClicked() {
                            FastSave.getInstance()
                                .saveBoolean(Constants.isScreenLayoutLocked, false)
                            unlockScreen()
                        }

                    }
                )
            }
        }

    }

    private fun lockScreen() {
        binding.linearEightIconsView.isEnabled = false
        binding.linearSixIconsView.isEnabled = false
        binding.linearFourIconsView.isEnabled = false
        binding.ivLeftMost.isEnabled = false
        binding.ivRightMost.isEnabled = false
        binding.ivLeftRight.isEnabled = false
        binding.ivMiddleCenter.isEnabled = false
        binding.ivTopCenter.isEnabled = false
        binding.ivBottomCenter.isEnabled = false
        binding.btnSynchronize.isEnabled = false
    }

    private fun unlockScreen() {
        binding.linearEightIconsView.isEnabled = true
        binding.linearSixIconsView.isEnabled = true
        binding.linearFourIconsView.isEnabled = true
        binding.ivLeftMost.isEnabled = true
        binding.ivRightMost.isEnabled = true
        binding.ivLeftRight.isEnabled = true
        binding.ivMiddleCenter.isEnabled = true
        binding.ivTopCenter.isEnabled = true
        binding.ivBottomCenter.isEnabled = true
        binding.btnSynchronize.isEnabled = true
    }

}