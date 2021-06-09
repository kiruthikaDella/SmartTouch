package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.api.repository.HomeRepository
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
            screenLayoutModel?.init()
        }

        when (args.deviceCustomizationDetail.screenLayoutType) {
            screenLayoutModel?.screenLayoutEight -> {
                binding.linearFourIconsView.isVisible = false
                binding.linearEightIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutSix -> {
                binding.linearFourIconsView.isVisible = false
                binding.linearSixIconsView.performClick()
            }
            screenLayoutModel?.screenLayoutFour -> {
                binding.linearEightIconsView.isVisible = false
                binding.linearSixIconsView.isVisible = false
                binding.linearFourIconsView.performClick()
            }
        }

        when (args.deviceCustomizationDetail.screenLayout) {
            screenLayoutModel?.LEFT_MOST -> {
                binding.ivLeftMost.performClick()
            }
            screenLayoutModel?.RIGHT_MOST -> {
                binding.ivRightMost.performClick()
            }
            screenLayoutModel?.LEFT_RIGHT -> {
                binding.ivLeftRight.performClick()
            }
            screenLayoutModel?.MIDDLE_CENTER -> {
                binding.ivMiddleCenter.performClick()
            }
            screenLayoutModel?.TOP_CENTER -> {
                binding.ivTopCenter.performClick()
            }
            screenLayoutModel?.BOTTOM_CENTER -> {
                binding.ivBottomCenter.performClick()
            }
        }


        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentScreenLayoutBinding = FragmentScreenLayoutBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}