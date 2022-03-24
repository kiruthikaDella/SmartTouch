package com.voinismartiot.voni.ui.fragments.authentication

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
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodySignUp
import com.voinismartiot.voni.api.repository.AuthRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.databinding.FragmentCreateAccountBinding
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

class CreateAccountFragment :
    BaseFragment<AuthViewModel, FragmentCreateAccountBinding, AuthRepository>() {

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

            if (Utils.isNetworkConnectivityAvailable()) {
                validateUserInformation()
                return@setOnClickListener
            }

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
                return@setOnClickListener
            }

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
                return@setOnClickListener
            }

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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.signUpResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            context?.showToast(response.values.message)
                            findNavController().navigateUp()
                        } else {
                            context?.showToast(response.values.message)
                        }

                    }
                    is Resource.Failure -> {
                        hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))

                        Log.e(logTag, "registration error ${response.errorBody?.string()}")
                    }
                    else -> Unit
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
        } else if (phoneNumber.length < 6) {
            binding.edtPhoneNumber.error = getString(R.string.error_text_phone_number_min_length)
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
            activity?.loadingDialog()
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