package com.voinismartiot.voni.ui.fragments.main.home

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
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.faqadapter.AnswerModel
import com.voinismartiot.voni.adapters.faqadapter.FAQAdapter
import com.voinismartiot.voni.adapters.faqadapter.QuestionModel
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.common.utils.loadingDialog
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentFaqsBinding
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

class FaqsFragment : BaseFragment<HomeViewModel, FragmentFaqsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var faqAdapter: FAQAdapter

    private var faqList = arrayListOf<QuestionModel>()
    private var filteredItems = arrayListOf<QuestionModel>()
    private val answerList = arrayListOf<AnswerModel>()

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                char?.let {
                    filter(it.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit

        })

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                faqList.clear()
                activity?.loadingDialog()
                viewModel.getFAQ()
            } else {
                Log.e(logTag, " internet is not available")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.faqResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                            response.values.data?.let { faqData ->
                                for (value in faqData) {
                                    try {
                                        answerList.clear()
                                        answerList.add(AnswerModel(value.description, false))
                                        faqList.add(
                                            QuestionModel(
                                                value.title,
                                                answerList.clone() as List<AnswerModel?>
                                            )
                                        )
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
                        hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, "faqResponse error ${response.errorBody}")
                    }
                    else -> Unit
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