package com.dellainfotech.smartTouch.ui.fragments.main.contact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.repository.ContactUsRepository
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentContactUsBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.ContactUsViewModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

class ContactUsFragment :
    ModelBaseFragment<ContactUsViewModel, FragmentContactUsBinding, ContactUsRepository>() {

    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            val feedback = binding.edtFeedback.text.toString().trim()
            if (feedback.isEmpty()) {
                context?.let {
                    Toast.makeText(it, "Please write something in feedback", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                    viewModel.addFeedback(BodyFeedback(feedback))
                }
            }
        }

        viewModel.addFeedbackResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " addFeedbackResponse Failure ${response.errorBody}")
                }
                else -> {
                    //we will do nothing here
                }
            }

        })

    }

    override fun getViewModel(): Class<ContactUsViewModel> = ContactUsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactUsBinding = FragmentContactUsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): ContactUsRepository = ContactUsRepository(networkModel)

}