package com.dellainfotech.smartTouch.ui.fragments.authentication

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.databinding.FragmentCreateAccountBinding
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class CreateAccountFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateAccountBinding
    private val logTag = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

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
    }

    private fun validateUserInformation() {
        if (binding.edtFullName.text.toString().isEmpty()) {
            binding.edtFullName.error = getString(R.string.error_text_full_name)
        } else if (binding.edtFullName.text.toString().length < 3) {
            binding.edtFullName.error = getString(R.string.error_text_full_name_length)
        } else if (binding.edtUserName.text.toString().isEmpty()) {
            binding.edtUserName.error = getString(R.string.error_text_user_name)
        }else if (binding.edtUserName.text.toString().length < 3) {
            binding.edtUserName.error = getString(R.string.error_text_user_name_length)
        } else if (binding.edtPhoneNumber.text.toString().isEmpty()) {
            binding.edtPhoneNumber.error = getString(R.string.error_text_phone_number)
        } else if (binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.error = getString(R.string.error_text_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()) {
            binding.edtEmail.error = getString(R.string.error_text_valid_email)
        } else if (binding.edtPassword.text.toString().isEmpty()) {
            binding.edtPassword.error = getString(R.string.error_text_password)
        }else if (binding.edtPassword.text.toString().length < 3) {
            binding.edtPassword.error = getString(R.string.error_text_password_length)
        } else if (binding.edtPassword.text.toString() != binding.edtConfirmPassword.text.toString()) {
            binding.edtConfirmPassword.error = getString(R.string.error_text_confirm_password)
        } else {
            Log.e(logTag, "Valid")
        }
    }

}