package com.voinismartiot.voni.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyFeedback
import com.voinismartiot.voni.api.repository.ContactUsRepository
import com.voinismartiot.voni.common.utils.Utils
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.common.utils.loadingDialog
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentContactUsBinding
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.ContactUsViewModel
import kotlinx.coroutines.flow.collectLatest

class ContactUsFragment :
    BaseFragment<ContactUsViewModel, FragmentContactUsBinding, ContactUsRepository>() {

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

                if (!Utils.isNetworkConnectivityAvailable()) {
                    context?.getString(R.string.text_no_internet_available)
                    return@setOnClickListener
                }

                activity?.let {
                    it.loadingDialog()
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
                        hideDialog()
                        context?.showToast(response.values.message)
                    }
                    is Resource.Failure -> {
                        hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, " addFeedbackResponse Failure ${response.errorBody}")
                    }
                    else -> Unit
                }

            }

        }

    }

}