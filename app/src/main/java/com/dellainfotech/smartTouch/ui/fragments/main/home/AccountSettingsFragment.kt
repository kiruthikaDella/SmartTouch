package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyChangePassword
import com.dellainfotech.smartTouch.api.body.BodyOwnership
import com.dellainfotech.smartTouch.api.body.BodyUpdateUserProfile
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.interfaces.DialogEditListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils
import com.dellainfotech.smartTouch.databinding.FragmentAccountSettingsBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.android.material.button.MaterialButton

/**
 * Created by Jignesh Dangar on 26-04-2021.
 */

@Suppress("DEPRECATION")
class AccountSettingsFragment :
    ModelBaseFragment<HomeViewModel, FragmentAccountSettingsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private var dialog: Dialog? = null
    private var cancelOwnership: Boolean = false
    private var ownershipId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivEditName.setOnClickListener {

            activity?.let {
                DialogUtil.editDialog(
                    it,
                    "Edit name",
                    binding.tvName.text.toString().trim(),
                    getString(R.string.text_save),
                    getString(R.string.text_cancel),
                    isLimitedText = false,
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            when {
                                string.isEmpty() -> {
                                    Toast.makeText(
                                        it,
                                        getString(R.string.error_text_full_name),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                string.length < 3 -> {
                                    Toast.makeText(
                                        it,
                                        getString(R.string.error_text_full_name_length),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    DialogUtil.hideDialog()
                                    binding.tvName.text = string
                                }
                            }
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }

        }

        binding.ivEditPhoneNumber.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it,
                    "Edit name",
                    binding.tvPhoneNumber.text.toString().trim(),
                    getString(R.string.text_save),
                    getString(R.string.text_cancel),
                    getString(R.string.dialog_input_type_phone),
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            when {
                                string.isEmpty() -> {
                                    Toast.makeText(
                                        it,
                                        getString(R.string.error_text_full_name),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                string.length < 3 -> {
                                    Toast.makeText(
                                        it,
                                        getString(R.string.error_text_full_name_length),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    DialogUtil.hideDialog()
                                    binding.tvPhoneNumber.text = string
                                }
                            }
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
        }

        binding.ivEditPassword.setOnClickListener {
            dialogUpdatePassword()
        }

        binding.btnUpdateProfile.setOnClickListener {
            val fullName = binding.tvName.text.toString()
            val phoneNumber = binding.tvPhoneNumber.text.toString()

            when {
                fullName.isEmpty() -> {
                    binding.tvName.error = getString(R.string.error_text_full_name)
                }
                fullName.length < 3 -> {
                    binding.tvName.error = getString(R.string.error_text_full_name_length)
                }
                phoneNumber.isEmpty() -> {
                    binding.tvPhoneNumber.error = getString(R.string.error_text_phone_number)
                }
                else -> {
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
        }

        binding.ivMasterEditName.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it,
                    "Edit name",
                    binding.tvMasterName.text.toString().trim(),
                    getString(R.string.text_save),
                    getString(R.string.text_cancel),
                    isLimitedText = false,
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            when {
                                string.isEmpty() -> {
                                    Toast.makeText(
                                        it,
                                        getString(R.string.error_text_name),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                string.length < 3 -> {
                                    Toast.makeText(
                                        it,
                                        getString(R.string.error_text_name_length),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    DialogUtil.hideDialog()
                                    binding.tvMasterName.text = string
                                }
                            }
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
        }

        binding.ivMasterEditEmail.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it,
                    "Edit email",
                    binding.tvMasterEmail.text.toString().trim(),
                    getString(R.string.text_save),
                    getString(R.string.text_cancel),
                    isLimitedText = false,
                    onClick = object : DialogEditListener {
                        override fun onYesClicked(string: String) {
                            if (string.isEmpty()) {
                                Toast.makeText(
                                    it,
                                    getString(R.string.error_text_email),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
                                Toast.makeText(
                                    it,
                                    getString(R.string.error_text_valid_email),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                DialogUtil.hideDialog()
                                binding.tvMasterEmail.text = string
                            }

                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
        }

        binding.btnTransferOwnership.setOnClickListener {
            val name = binding.tvMasterName.text.toString().trim()
            val email = binding.tvMasterEmail.text.toString().trim()

            activity?.let { mActivity ->

                if (name.isEmpty()) {
                    Toast.makeText(
                        mActivity,
                        getString(R.string.error_text_name),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (name.length < 3) {
                    Toast.makeText(
                        mActivity,
                        getString(R.string.error_text_full_name_length),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (email.isEmpty()) {
                    Toast.makeText(
                        mActivity,
                        getString(R.string.error_text_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(
                        mActivity,
                        getString(R.string.error_text_valid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (cancelOwnership) {
                        DialogUtil.askAlert(
                            mActivity,
                            getString(R.string.dialog_title_cancel_ownership),
                            getString(R.string.text_ok),
                            getString(R.string.text_cancel),
                            object : DialogAskListener {
                                override fun onYesClicked() {
                                    DialogUtil.hideDialog()
                                    DialogUtil.loadingAlert(mActivity)
                                    ownershipId?.let {
                                        viewModel.cancelOwnership(it)
                                    }
                                }

                                override fun onNoClicked() {
                                    DialogUtil.hideDialog()
                                }
                            })
                    } else {
                        DialogUtil.askAlert(
                            mActivity,
                            getString(R.string.dialog_title_transfer_ownership),
                            getString(R.string.text_ok),
                            getString(R.string.text_cancel),
                            object : DialogAskListener {
                                override fun onYesClicked() {
                                    DialogUtil.hideDialog()
                                    DialogUtil.loadingAlert(mActivity)
                                    viewModel.transferOwnership(BodyOwnership(email, name))
                                }

                                override fun onNoClicked() {
                                    DialogUtil.hideDialog()
                                }
                            })

                    }
                }

            }
        }

        if (FastSave.getInstance().getString(Constants.SOCIAL_ID, null) != "0") {
            binding.ivPassword.visibility = View.INVISIBLE
            binding.tvTitlePassword.visibility = View.INVISIBLE
            binding.tvPassword.visibility = View.INVISIBLE
            binding.ivEditPassword.visibility = View.INVISIBLE
        }

        if (!Utils.isMasterUser()) {
            binding.tvOwnershipTransfer.visibility = View.GONE
            binding.tvForMasterUsers.visibility = View.GONE
            binding.ivInfo.visibility = View.GONE
            binding.ivMasterName.visibility = View.GONE
            binding.tvMasterTitleName.visibility = View.GONE
            binding.tvMasterName.visibility = View.GONE
            binding.ivMasterEditName.visibility = View.GONE
            binding.ivMasterEmail.visibility = View.GONE
            binding.tvMasterTitleEmail.visibility = View.GONE
            binding.tvMasterEmail.visibility = View.GONE
            binding.ivMasterEditEmail.visibility = View.GONE
            binding.btnTransferOwnership.visibility = View.GONE
        }

        binding.ivInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    "",
                    getString(R.string.text_info_ownership_transfer)
                )
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateUserProfileResponse.postValue(null)
        viewModel.changePasswordResponse.postValue(null)
        viewModel.transferOwnershipResponse.postValue(null)
        viewModel.cancelOwnershipResponse.postValue(null)
    }

    private fun apiCall() {

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getUserProfile()
                viewModel.getOwnership()
            } else {
                Log.e(logTag, " internet is not available")
            }
        })

        viewModel.getUserProfileResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        response.values.data?.let { userData ->
                            userData.vEmail?.let {
                                binding.tvEmail.text = it
                            }
                            userData.vFullName?.let {
                                binding.tvName.text = it
                            }
                            userData.bPhoneNumber?.let {
                                binding.tvPhoneNumber.text = it
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
                                binding.tvEmail.text = it
                            }
                            userData.vFullName?.let {
                                binding.tvName.text = it
                            }
                            userData.bPhoneNumber?.let {
                                binding.tvPhoneNumber.text = it
                            }

                            FastSave.getInstance().saveString(Constants.USER_ID, userData.iUserId)
                            FastSave.getInstance()
                                .saveString(Constants.USER_FULL_NAME, userData.vFullName)
                            FastSave.getInstance()
                                .saveString(Constants.USERNAME, userData.vUserName)
                            FastSave.getInstance().saveString(Constants.USER_EMAIL, userData.vEmail)
                            FastSave.getInstance()
                                .saveString(Constants.USER_PHONE_NUMBER, userData.bPhoneNumber)

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
                            binding.tvMasterName.text = it.name
                            binding.tvMasterEmail.text = it.email
//                            binding.btnUpdate.isEnabled = false
                            if (it.isEmailVerified == 0) {
                                cancelOwnership = true
                                ownershipId = it.id
                                binding.btnTransferOwnership.text = getString(R.string.text_cancel)
                                binding.tvMasterName.clearFocus()
                                binding.tvMasterEmail.clearFocus()
                                binding.ivMasterEditName.isEnabled = false
                                binding.ivMasterEditEmail.isEnabled = false
                            }
                        }
                    } else {
                        binding.tvMasterName.text = ""
                        binding.tvMasterEmail.text = ""
                        cancelOwnership = false
                        ownershipId = null
                        binding.btnTransferOwnership.text =
                            getString(R.string.text_transfer_ownership)
                        binding.ivMasterEditName.isEnabled = true
                        binding.ivMasterEditEmail.isEnabled = true
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
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        getOwnership()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " transferOwnershipResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.cancelOwnershipResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        getOwnership()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " cancelOwnershipResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    private fun getOwnership() {
        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.getOwnership()
    }

    private fun dialogUpdatePassword() {
        activity?.let { myActivity ->
            dialog = Dialog(myActivity)
            dialog?.setContentView(R.layout.dialog_password)
            dialog?.setCancelable(true)

            var isCurrentPasswordVisible = false
            var isNewPasswordVisible = false
            var isConfirmPasswordVisible = false

            val edtCurrentPassword = dialog?.findViewById(R.id.edt_current_password) as EditText
            val edtNewPassword = dialog?.findViewById(R.id.edt_new_password) as EditText
            val edtConfirmPassword = dialog?.findViewById(R.id.edt_confirm_password) as EditText
            val ivHideCurrentPassword =
                dialog?.findViewById(R.id.iv_hide_current_password) as ImageView
            val ivHideNewPassword = dialog?.findViewById(R.id.iv_hide_new_password) as ImageView
            val ivHideConfirmPassword =
                dialog?.findViewById(R.id.iv_hide_confirm_password) as ImageView
            val btnSave = dialog?.findViewById(R.id.btn_save) as MaterialButton
            val btnCancel = dialog?.findViewById(R.id.btn_cancel) as MaterialButton

            ivHideCurrentPassword.setOnClickListener {
                if (isCurrentPasswordVisible) {
                    isCurrentPasswordVisible = false
                    ivHideCurrentPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity,
                            R.drawable.ic_password_hidden
                        )
                    )
                    edtCurrentPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()

                } else {
                    isCurrentPasswordVisible = true
                    ivHideCurrentPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity,
                            R.drawable.ic_password_visible
                        )
                    )
                    edtCurrentPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
            }

            ivHideNewPassword.setOnClickListener {
                if (isNewPasswordVisible) {
                    isNewPasswordVisible = false
                    ivHideNewPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity,
                            R.drawable.ic_password_hidden
                        )
                    )
                    edtNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()

                } else {
                    isNewPasswordVisible = true
                    ivHideNewPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity,
                            R.drawable.ic_password_visible
                        )
                    )
                    edtNewPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
            }

            ivHideConfirmPassword.setOnClickListener {
                if (isConfirmPasswordVisible) {
                    isConfirmPasswordVisible = false
                    ivHideConfirmPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity,
                            R.drawable.ic_password_hidden
                        )
                    )
                    edtConfirmPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()

                } else {
                    isConfirmPasswordVisible = true
                    ivHideConfirmPassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity,
                            R.drawable.ic_password_visible
                        )
                    )
                    edtConfirmPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
            }

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }

            btnSave.setOnClickListener {
                val currentPassword = edtCurrentPassword.text.toString().trim()
                val newPassword = edtNewPassword.text.toString().trim()
                val confirmPassword = edtConfirmPassword.text.toString().trim()

                when {
                    currentPassword.isEmpty() -> {
                        edtCurrentPassword.error = getString(R.string.error_text_current_password)
                    }
                    newPassword.isEmpty() -> {
                        edtNewPassword.error = getString(R.string.error_text_password)
                    }
                    newPassword.length < Constants.PASSWORD_LENGTH -> {
                        edtNewPassword.error = getString(R.string.error_text_password_length)
                    }
                    newPassword != confirmPassword -> {
                        edtConfirmPassword.error = getString(R.string.error_text_confirm_password)
                    }
                    else -> {
                        dialog?.dismiss()
                        DialogUtil.loadingAlert(myActivity)
                        viewModel.changePassword(BodyChangePassword(currentPassword, newPassword))
                    }
                }
            }

            val dpHeight: Float
            val dpWidth: Float

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display: Display? = myActivity.display
                val displayMetrics = DisplayMetrics()
                display?.getRealMetrics(displayMetrics)
                dpHeight = displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT
                dpWidth = displayMetrics.widthPixels * 0.85.toFloat()
            } else {
                val display: Display = myActivity.windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                dpHeight = outMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT
                dpWidth = outMetrics.widthPixels * 0.85.toFloat()
            }

            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(dpWidth.toInt(), dpHeight.toInt())
            dialog?.show()
        }
    }
}