package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.smartouch.databinding.FragmentDeviceSettingsBinding
import com.smartouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class DeviceSettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentDeviceSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}