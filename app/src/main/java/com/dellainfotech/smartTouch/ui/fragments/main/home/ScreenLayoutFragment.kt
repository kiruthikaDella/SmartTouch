package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentScreenLayoutBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */
class ScreenLayoutFragment :
    ModelBaseFragment<HomeViewModel, FragmentScreenLayoutBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private var screenLayoutModel: ScreenLayoutModel? = null
    private val args: ScreenLayoutFragmentArgs by navArgs()

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

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentScreenLayoutBinding = FragmentScreenLayoutBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}