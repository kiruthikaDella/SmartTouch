package com.voinismartiot.voni.ui.fragments.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyForgotPassword
import com.voinismartiot.voni.api.repository.AuthRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.databinding.FragmentForgotPasswordBinding
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

class ForgotPasswordFragment :
    BaseFragment<AuthViewModel, FragmentForgotPasswordBinding, AuthRepository>() {

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

            if (Utils.isNetworkConnectivityAvailable()) {
                val email = binding.edtEmail.text.toString().trim()
                if (email.isBlank()) {
                    binding.edtEmail.error = getString(R.string.error_text_email)
                } else {
                    activity?.loadingDialog()
                    viewModel.forgotPassword(BodyForgotPassword(email))
                }
            } else {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            hideDialog()
                            findNavController().navigateUp()
                        }

                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            findNavController().navigateUp()
                            context?.showToast(response.values.message)
                        } else {
                            context?.showToast(response.values.message)
                        }
                    }
                    is Resource.Failure -> {
                        hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, " Failure ${response.errorBody?.string()}")
                    }
                    else -> Unit
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