package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentScreenLayoutBinding
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment

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
        binding.relativeLock.isVisible = true
    }

    private fun unlockScreen() {
        binding.relativeLock.isVisible = false
    }

}