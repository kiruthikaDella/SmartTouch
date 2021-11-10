package com.dellainfotech.smartTouch.ui.fragments.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyForgotPassword
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentForgotPasswordBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class ForgotPasswordFragment :
    ModelBaseFragment<AuthViewModel, FragmentForgotPasswordBinding, AuthRepository>() {

    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {

            if (isInternetConnected()) {
                val email = binding.edtEmail.text.toString().trim()
                if (email.isBlank()) {
                    binding.edtEmail.error = getString(R.string.error_text_email)
                } else {
                    activity?.let {
                        DialogUtil.loadingAlert(it)
                    }
                    viewModel.forgotPassword(BodyForgotPassword(email))
                }
            } else {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigateUp()
                            }

                        }
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            findNavController().navigateUp()
                            context?.showToast(response.values.message)
                        } else {
                            context?.showToast(response.values.message)
                        }
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, " Failure ${response.errorBody?.string()}")
                    }
                    else -> {
                        //We will do nothing here
                    }
                }
            }

        }

    }

    override fun getViewModel(): Class<AuthViewModel> = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotPasswordBinding =
        FragmentForgotPasswordBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository = AuthRepository(networkModel)

}