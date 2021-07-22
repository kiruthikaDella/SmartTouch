package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.databinding.FragmentConfigWifiBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 21-07-2021.
 */

class ConfigWifiFragment :
    ModelBaseFragment<HomeViewModel, FragmentConfigWifiBinding, HomeRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        showConfigView()

        binding.layoutConfigWifiPanel.btnSubmit.setOnClickListener {
            showConfigProcessView()
        }

    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConfigWifiBinding = FragmentConfigWifiBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun showConfigView() {
        binding.layoutConfigWifiPanel.linearConfigWifiPanel.isVisible = true
        binding.layoutConfigWifiProcess.linearConfigWifiProcess.isVisible = false
        binding.layoutConfigWifiProcess.pulsator.stopRippleAnimation()
    }

    private fun showConfigProcessView() {
        binding.layoutConfigWifiPanel.linearConfigWifiPanel.isVisible = false
        binding.layoutConfigWifiProcess.linearConfigWifiProcess.isVisible = true
        binding.layoutConfigWifiProcess.pulsator.startRippleAnimation()
    }
}