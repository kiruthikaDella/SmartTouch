package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyChangePassword
import com.dellainfotech.smartTouch.api.body.BodyOwnership
import com.dellainfotech.smartTouch.api.body.BodyUpdateUserProfile
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
import com.dellainfotech.smartTouch.databinding.FragmentAccountSettingsBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.android.material.button.MaterialButton

/**
 * Created by Jignesh Dangar on 26-04-2021.
 */

class AccountSettingsFragment :
    ModelBaseFragment<HomeViewModel, FragmentAccountSettingsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private var dialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivEditName.setOnClickListener {
            binding.edtName.isEnabled = true
            binding.edtName.requestFocus()
        }

        binding.ivEditPhoneNumber.setOnClickListener {
            binding.edtPhoneNumber.isEnabled = true
            binding.edtPhoneNumber.requestFocus()
        }

        binding.ivEditPassword.setOnClickListener {
            dialogUpdatePassword()
        }

        binding.btnUpdateProfile.setOnClickListener {
            val fullName = binding.edtName.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()

            if (fullName.isEmpty()) {
                binding.edtName.error = getString(R.string.error_text_full_name)
            } else if (fullName.length < 3) {
                binding.edtName.error = getString(R.string.error_text_full_name_length)
            } else if (phoneNumber.isEmpty()) {
                binding.edtPhoneNumber.error = getString(R.string.error_text_phone_number)
            } else {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                    viewModel.updateUserProfile(
                        BodyUpdateUserProfile(
                            fullName,
                            phoneNumber
                        )
                    )
                }
            }
        }

        binding.ivMasterEditName.setOnClickListener {
            binding.edtMasterName.isEnabled = true
            binding.edtMasterName.requestFocus()
        }

        binding.ivMasterEditEmail.setOnClickListener {
            binding.edtMasterEmail.isEnabled = true
            binding.edtMasterEmail.requestFocus()
        }

        binding.btnUpdate.setOnClickListener {
            val name = binding.edtMasterName.text.toString().trim()
            val email = binding.edtMasterEmail.text.toString().trim()

            if (name.isEmpty()) {
                binding.edtMasterName.error = getString(R.string.error_text_name)
            } else if (name.length < 3) {
                binding.edtMasterName.error = getString(R.string.error_text_full_name_length)
            } else if (email.isEmpty()) {
                binding.edtMasterEmail.error = getString(R.string.error_text_email)
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtMasterEmail.error = getString(R.string.error_text_valid_email)
            }else {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.transferOwnership(BodyOwnership(email,name))
            }
        }

        apiCall()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSettingsBinding =
        FragmentAccountSettingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }

    private fun apiCall() {
        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.getUserProfile()
        viewModel.getOwnership()

        viewModel.getUserProfileResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        response.values.data?.let { userData ->
                            userData.vEmail?.let {
                                binding.edtEmail.text = it.toEditable()
                            }
                            userData.vFullName?.let {
                                binding.edtName.text = it.toEditable()
                            }
                            userData.bPhoneNumber?.let {
                                binding.edtPhoneNumber.text = it.toEditable()
                            }
                        }

                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "getUserProfileResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.updateUserProfileResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " ${response.values.message} ")
                    activity?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        response.values.data?.let { userData ->
                            userData.vEmail?.let {
                                binding.edtEmail.text = it.toEditable()
                            }
                            userData.vFullName?.let {
                                binding.edtName.text = it.toEditable()
                            }
                            userData.bPhoneNumber?.let {
                                binding.edtPhoneNumber.text = it.toEditable()
                            }
                        }

                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "getUserProfileResponse Failure ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.changePasswordResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " changePasswordResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.getOwnershipResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            binding.edtMasterName.text = it.name.toEditable()
                            binding.edtMasterEmail.text = it.email.toEditable()
                            binding.btnUpdate.isEnabled = false
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " getOwnershipResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.transferOwnershipResponse.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it,response.values.message,Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            binding.edtMasterName.text = it.name.toEditable()
                            binding.edtMasterEmail.text = it.email.toEditable()
                            binding.btnUpdate.isEnabled = false
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " transferOwnershipResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    private fun dialogUpdatePassword() {
        activity?.let { myActivity ->
            dialog = Dialog(myActivity)
            dialog?.setContentView(R.layout.dialog_password)
            dialog?.setCancelable(true)

            val edtCurrentPassword = dialog?.findViewById(R.id.edt_current_password) as EditText
            val edtPassword = dialog?.findViewById(R.id.edt_password) as EditText
            val edtConfirmPassword = dialog?.findViewById(R.id.edt_confirm_password) as EditText
            val btnSave = dialog?.findViewById(R.id.btn_save) as MaterialButton
            val btnCancel = dialog?.findViewById(R.id.btn_cancel) as MaterialButton

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }

            btnSave.setOnClickListener {
                val currentPassword = edtCurrentPassword.text.toString().trim()
                val newPassword = edtPassword.text.toString().trim()
                val confirmPassword = edtConfirmPassword.text.toString().trim()

                if (currentPassword.isEmpty()) {
                    edtCurrentPassword.error = getString(R.string.error_text_current_password)
                } else if (newPassword.isEmpty()) {
                    edtPassword.error = getString(R.string.error_text_password)
                } else if (newPassword.length < 3) {
                    edtPassword.error = getString(R.string.error_text_password_length)
                } else if (newPassword != confirmPassword) {
                    edtConfirmPassword.error = getString(R.string.error_text_confirm_password)
                } else {
                    dialog?.dismiss()
                    DialogUtil.loadingAlert(myActivity)
                    viewModel.changePassword(BodyChangePassword(currentPassword, newPassword))
                }
            }

            val displayMetrics = DisplayMetrics()
            myActivity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val width = (displayMetrics.widthPixels * 0.85.toFloat())
            val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(width.toInt(), height.toInt())
            dialog?.show()
        }
    }
}