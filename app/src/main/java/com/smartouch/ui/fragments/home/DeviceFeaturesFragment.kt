package com.smartouch.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartouch.databinding.FragmentDeviceFeaturesBinding

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class DeviceFeaturesFragment : Fragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentDeviceFeaturesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceFeaturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivCreateSceneSettings.setOnClickListener {
            findNavController().navigate(DeviceFeaturesFragmentDirections.actionDeviceFeaturesFragmentToCreateSceneFragment())
        }
    }
}