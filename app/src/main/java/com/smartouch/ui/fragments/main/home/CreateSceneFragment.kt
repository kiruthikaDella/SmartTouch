package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.smartouch.R
import com.smartouch.adapters.DeviceSceneAdapter
import com.smartouch.common.utils.DialogUtil
import com.smartouch.databinding.FragmentCreateSceneBinding
import com.smartouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 23-04-2021.
 */

class CreateSceneFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateSceneBinding
    private lateinit var deviceSceneAdapter: DeviceSceneAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateSceneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        context?.let {
            deviceSceneAdapter = DeviceSceneAdapter(it)
            binding.recyclerScenes.adapter = deviceSceneAdapter
        }

        binding.ivEditCreateScene.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(it,getString(R.string.text_scene_name),"Living Room",getString(R.string.text_save),getString(
                    R.string.text_cancel))
            }
        }
    }
}