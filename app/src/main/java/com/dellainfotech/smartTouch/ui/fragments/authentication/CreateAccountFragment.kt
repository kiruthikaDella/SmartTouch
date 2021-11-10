package com.dellainfotech.smartTouch.ui.fragments.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodySignUp
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentCreateAccountBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class CreateAccountFragment :
    ModelBaseFragment<AuthViewModel, FragmentCreateAccountBinding, AuthRepository>() {

    private val logTag = this::class.java.simpleName
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSignUp.setOnClickListener {
            if (isInternetConnected()) {
                validateUserInformation()
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

        binding.ivHidePassword.setOnClickListener {
            if (isPasswordVisible) {
                isPasswordVisible = false
                context?.let {
                    binding.ivHidePassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_password_hidden
                        )
                    )
                    binding.edtPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
            } else {
                isPasswordVisible = true
                context?.let {
                    binding.ivHidePassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_password_visible
                        )
                    )
                    binding.edtPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
            }
        }

        binding.ivHideConfirmPassword.setOnClickListener {

            if (isConfirmPasswordVisible) {
                isConfirmPasswordVisible = false
                context?.let {
                    binding.ivHideConfirmPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_password_hidden
                        )
                    )
                    binding.edtConfirmPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
            } else {
                isConfirmPasswordVisible = true
                context?.let {
                    binding.ivHideConfirmPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_password_visible
                        )
                    )
                    binding.edtConfirmPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.signUpResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            context?.showToast(response.values.message)
                            findNavController().navigateUp()
                        } else {
                            context?.showToast(response.values.message)
                        }

                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))

                        Log.e(logTag, "registration error ${response.errorBody?.string()}")
                    }
                    else -> {
                        //We will do nothing here
                    }
                }
            }

        }

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
            binding.edtFullName.requestFocus()
        } else if (fullName.length < 3) {
            binding.edtFullName.error = getString(R.string.error_text_full_name_length)
            binding.edtFullName.requestFocus()
        } else if (userName.isEmpty()) {
            binding.edtUserName.error = getString(R.string.error_text_user_name)
            binding.edtUserName.requestFocus()
        } else if (userName.length < 3) {
            binding.edtUserName.error = getString(R.string.error_text_user_name_length)
            binding.edtUserName.requestFocus()
        } else if (phoneNumber.isEmpty()) {
            binding.edtPhoneNumber.error = getString(R.string.error_text_phone_number)
            binding.edtPhoneNumber.requestFocus()
        } else if (email.isEmpty()) {
            binding.edtEmail.error = getString(R.string.error_text_email)
            binding.edtEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = getString(R.string.error_text_valid_email)
            binding.edtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edtPassword.error = getString(R.string.error_text_password)
            binding.edtPassword.requestFocus()
        } else if (password.length < Constants.PASSWORD_LENGTH) {
            binding.edtPassword.error = getString(R.string.error_text_password_length)
            binding.edtPassword.requestFocus()
        } else if (password != confirmPassword) {
            binding.edtConfirmPassword.error = getString(R.string.error_text_confirm_password)
            binding.edtPassword.requestFocus()
        } else {
            activity?.let {
                DialogUtil.loadingAlert(it)
            }
            viewModel.signUp(
                BodySignUp(
                    fullName,
                    userName,
                    email,
                    password,
                    confirmPassword,
                    Constants.LOGIN_TYPE_NORMAL,
                    phoneNumber
                )
            )
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