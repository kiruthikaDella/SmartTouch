package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.repository.ContactUsRepository
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentContactUsBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.ContactUsViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

class ContactUsFragment :
    ModelBaseFragment<ContactUsViewModel, FragmentContactUsBinding, ContactUsRepository>() {

    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            val feedback = binding.edtFeedback.text.toString().trim()
            if (feedback.isEmpty()) {
                context?.showToast("Please write something in feedback")
            } else {

                if (!isInternetConnected()){
                    context?.getString(R.string.text_no_internet_available)
                    return@setOnClickListener
                }

                activity?.let {
                    DialogUtil.loadingAlert(it)
                    viewModel.addFeedback(BodyFeedback(feedback))
                }
            }
        }

        apiCall()
    }

    override fun getViewModel(): Class<ContactUsViewModel> = ContactUsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactUsBinding = FragmentContactUsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): ContactUsRepository = ContactUsRepository(networkModel)

    private fun apiCall() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.addFeedbackResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        context?.showToast(response.values.message)
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, " addFeedbackResponse Failure ${response.errorBody}")
                    }
                    else -> {
                        //we will do nothing here
                    }
                }

            }

        }

    }

}