package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.faqadapter.AnswerModel
import com.dellainfotech.smartTouch.adapters.faqadapter.FAQAdapter
import com.dellainfotech.smartTouch.adapters.faqadapter.QuestionModel
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentFaqsBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Jignesh Dangar on 26-04-2021.
 */

class FaqsFragment : ModelBaseFragment<HomeViewModel, FragmentFaqsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var faqAdapter: FAQAdapter

    private var faqList = arrayListOf<QuestionModel>()
    private var filteredItems = arrayListOf<QuestionModel>()
    private var answerList = arrayListOf<AnswerModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val animator: RecyclerView.ItemAnimator? = binding.recyclerFaq.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        faqAdapter = FAQAdapter(faqList)

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                char?.let {
                    filter(it.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                faqList.clear()
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getFAQ()
            } else {
                Log.e(logTag, " internet is not available")
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.faqResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                            response.values.data?.let { faqData ->
                                for ((index, value) in faqData.withIndex()) {
                                    try {
                                        answerList.clear()
                                        answerList.add(0, AnswerModel(value.description, true))
                                        faqList.add(index, QuestionModel(value.title, answerList))
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                filteredItems.addAll(faqList)
                                faqAdapter = FAQAdapter(faqList)
                                binding.recyclerFaq.adapter = faqAdapter
                            }

                        } else {
                            context?.showToast(response.values.message)
                        }
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, "faqResponse error ${response.errorBody}")
                    }
                    else -> {
                        // We will do nothing here
                    }
                }
            }

        }

    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFaqsBinding = FragmentFaqsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun filter(string: String) {
        var charText = string
        charText = charText.lowercase()
        faqList.clear()
        if (charText.isEmpty()) {
            faqList.addAll(filteredItems)
        } else {
            for (wp in filteredItems) {
                if (wp.title.lowercase().contains(charText)) {
                    faqList.add(wp)
                }
            }
        }
        faqAdapter.notifyDataSetChanged()
    }

}