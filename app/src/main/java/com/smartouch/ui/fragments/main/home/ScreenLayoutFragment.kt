package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartouch.R
import com.smartouch.common.utils.dialog
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

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
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

    }

}