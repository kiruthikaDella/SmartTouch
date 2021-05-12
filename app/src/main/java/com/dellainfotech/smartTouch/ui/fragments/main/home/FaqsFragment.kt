package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.adapters.faqadapter.AnswerModel
import com.dellainfotech.smartTouch.adapters.faqadapter.FAQAdapter
import com.dellainfotech.smartTouch.adapters.faqadapter.QuestionModel
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentFaqsBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 26-04-2021.
 */

class FaqsFragment : ModelBaseFragment<HomeViewModel, FragmentFaqsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var faqAdapter: FAQAdapter
    private var faqList: List<QuestionModel> = ArrayList()
    private var answerList: List<AnswerModel> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val animator: RecyclerView.ItemAnimator? = binding.recyclerFaq.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        faqList.toMutableList().clear()

        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.getFAQ()

        viewModel.faqResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        response.values.data?.let { faqData ->
                            for ((index, value) in faqData.withIndex()) {
                                answerList.toMutableList().clear()
                                answerList.toMutableList()
                                    .add(0, AnswerModel(value.description, true))
                                faqList.toMutableList()
                                    .add(index, QuestionModel(value.title, answerList))
                            }

                            faqAdapter =
                                FAQAdapter(faqList)
                            binding.recyclerFaq.adapter = faqAdapter
                        }

                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "faqResponse error ${response.errorBody}")
                }
            }
        })
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFaqsBinding = FragmentFaqsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)
}