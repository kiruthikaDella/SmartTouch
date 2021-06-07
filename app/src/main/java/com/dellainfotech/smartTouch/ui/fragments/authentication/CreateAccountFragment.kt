package com.dellainfotech.smartTouch.ui.fragments.authentication

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodySignUp
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentCreateAccountBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.AuthViewModel

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class CreateAccountFragment :
    ModelBaseFragment<AuthViewModel, FragmentCreateAccountBinding, AuthRepository>() {

    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSignUp.setOnClickListener {
            validateUserInformation()
        }

        viewModel.signUpResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                        findNavController().navigateUp()
                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "registration error ${response.errorBody}")
                }
            }
        })
    }

    private fun validateUserInformation() {

        val fullName = binding.edtFullName.text.toString()
        val userName = binding.edtUserName.text.toString()
        val phoneNumber = binding.edtPhoneNumber.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        if (fullName.isEmpty()) {
            binding.edtFullName.error = getString(R.string.error_text_full_name)
        } else if (fullName.length < 3) {
            binding.edtFullName.error = getString(R.string.error_text_full_name_length)
        } else if (userName.isEmpty()) {
            binding.edtUserName.error = getString(R.string.error_text_user_name)
        } else if (userName.length < 3) {
            binding.edtUserName.error = getString(R.string.error_text_user_name_length)
        } else if (phoneNumber.isEmpty()) {
            binding.edtPhoneNumber.error = getString(R.string.error_text_phone_number)
        } else if (email.isEmpty()) {
            binding.edtEmail.error = getString(R.string.error_text_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = getString(R.string.error_text_valid_email)
        } else if (password.isEmpty()) {
            binding.edtPassword.error = getString(R.string.error_text_password)
        } else if (password.length < 6) {
            binding.edtPassword.error = getString(R.string.error_text_password_length)
        } else if (password != confirmPassword) {
            binding.edtConfirmPassword.error = getString(R.string.error_text_confirm_password)
        } else {
            activity?.let {
                DialogUtil.loadingAlert(it)
            }
            viewModel.signUp(BodySignUp(fullName,userName,email,password,confirmPassword,phoneNumber))
        }
    }

    override fun getViewModel(): Class<AuthViewModel> = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountBinding =
        FragmentCreateAccountBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository = AuthRepository(networkModel)

}