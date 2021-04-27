package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.smartouch.adapters.faqadapter.FAQAdapter
import com.smartouch.common.utils.Test
import com.smartouch.databinding.FragmentFaqsBinding
import com.smartouch.ui.fragments.BaseFragment

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