package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.adapters.faqadapter.FAQAdapter
import com.dellainfotech.smartTouch.common.utils.Test
import com.dellainfotech.smartTouch.databinding.FragmentFaqsBinding
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 26-04-2021.
 */

class FaqsFragment : BaseFragment() {

    private lateinit var binding: FragmentFaqsBinding
    private lateinit var faqAdapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFaqsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }


        val animator: RecyclerView.ItemAnimator? = binding.recyclerFaq.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        faqAdapter =
            FAQAdapter(Test.makeQuestion())
        binding.recyclerFaq.adapter = faqAdapter
    }
}