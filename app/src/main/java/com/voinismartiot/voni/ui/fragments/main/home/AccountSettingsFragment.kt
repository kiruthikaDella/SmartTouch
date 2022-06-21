package com.voinismartiot.voni.ui.fragments.main.home

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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.material.button.MaterialButton
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyChangePassword
import com.voinismartiot.voni.api.body.BodyOwnership
import com.voinismartiot.voni.api.body.BodyUpdateUserProfile
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.interfaces.DialogEditListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.databinding.FragmentAccountSettingsBinding
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AccountSettingsFragment :
    BaseFragment<HomeViewModel, FragmentAccountSettingsBinding, HomeRepository>() {

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

            activity?.editDialog(
                "Edit name",
                binding.tvName.text.toString().trim(),
                getString(R.string.text_save),
                getString(R.string.text_cancel),
                isLimitedText = false,
                onClick = object : DialogEditListener {
                    override fun onYesClicked(string: String) {
                        when {
                            string.isEmpty() -> {
                                activity?.showToast(getString(R.string.error_text_full_name))
                            }
                            string.length < 3 -> {
                                activity?.showToast(getString(R.string.error_text_full_name_length))
                            }
                            else -> {
                                hideDialog()
                                binding.tvName.text = string
                            }
                        }
                    }

                    override fun onNoClicked() {
                        hideDialog()
                    }

                }
            )

        }

        binding.ivEditPhoneNumber.setOnClickListener {
            activity?.editDialog(
                "Edit phone number",
                binding.tvPhoneNumber.text.toString().trim(),
                getString(R.string.text_save),
                getString(R.string.text_cancel),
                getString(R.string.dialog_input_type_phone),
                onClick = object : DialogEditListener {
                    override fun onYesClicked(string: String) {
                        when {
                            string.isEmpty() -> {
                                activity?.showToast(getString(R.string.error_text_phone_number))
                            }
                            string.length < 6 -> {
                                activity?.showToast(getString(R.string.error_text_phone_number_min_length))
                            }
                            else -> {
                                hideDialog()
                                binding.tvPhoneNumber.text = string
                            }
                        }
                    }

                    override fun onNoClicked() {
                        hideDialog()
                    }

                }
            )
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

                    if (!Utils.isNetworkConnectivityAvailable()) {
                        context?.showToast(getString(R.string.text_no_internet_available))
                        return@setOnClickListener
                    }

                    activity?.let {
                        it.loadingDialog()
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
            activity?.editDialog(
                "Edit name",
                binding.tvMasterName.text.toString().trim(),
                getString(R.string.text_save),
                getString(R.string.text_cancel),
                isLimitedText = false,
                onClick = object : DialogEditListener {
                    override fun onYesClicked(string: String) {
                        when {
                            string.isEmpty() -> {
                                activity?.showToast(getString(R.string.error_text_name))
                            }
                            string.length < 3 -> {
                                activity?.showToast(getString(R.string.error_text_name_length))
                            }
                            else -> {
                                hideDialog()
                                binding.tvMasterName.text = string
                            }
                        }
                    }

                    override fun onNoClicked() {
                        hideDialog()
                    }

                }
            )
        }

        binding.ivMasterEditEmail.setOnClickListener {
            activity?.editDialog(
                "Edit email",
                binding.tvMasterEmail.text.toString().trim(),
                getString(R.string.text_save),
                getString(R.string.text_cancel),
                isLimitedText = false,
                onClick = object : DialogEditListener {
                    override fun onYesClicked(string: String) {
                        if (string.isEmpty()) {
                            activity?.showToast(getString(R.string.error_text_email))
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
                            activity?.showToast(getString(R.string.error_text_valid_email))
                        } else {
                            hideDialog()
                            binding.tvMasterEmail.text = string
                        }

                    }

                    override fun onNoClicked() {
                        hideDialog()
                    }

                }
            )
        }

        binding.btnTransferOwnership.setOnClickListener {
            val name = binding.tvMasterName.text.toString().trim()
            val email = binding.tvMasterEmail.text.toString().trim()

            if (name.isEmpty()) {
                activity?.showToast(getString(R.string.error_text_name))
            } else if (name.length < 3) {
                activity?.showToast(getString(R.string.error_text_full_name_length))
            } else if (email.isEmpty()) {
                activity?.showToast(getString(R.string.error_text_email))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                activity?.showToast(getString(R.string.error_text_valid_email))
            } else {

                if (!Utils.isNetworkConnectivityAvailable()) {
                    context?.showToast(getString(R.string.text_no_internet_available))
                    return@setOnClickListener
                }

                if (cancelOwnership) {
                    activity?.askAlert(
                        getString(R.string.dialog_title_cancel_ownership),
                        getString(R.string.text_ok),
                        getString(R.string.text_cancel),
                        object : DialogAskListener {
                            override fun onYesClicked() {
                                hideDialog()
                                activity?.loadingDialog()
                                ownershipId?.let {
                                    viewModel.cancelOwnership(it)
                                }
                            }

                            override fun onNoClicked() {
                                hideDialog()
                            }
                        })
                } else {
                    activity?.askAlert(
                        getString(R.string.dialog_title_transfer_ownership),
                        getString(R.string.text_ok),
                        getString(R.string.text_cancel),
                        object : DialogAskListener {
                            override fun onYesClicked() {
                                hideDialog()
                                activity?.loadingDialog()
                                viewModel.transferOwnership(BodyOwnership(email, name))
                            }

                            override fun onNoClicked() {
                                hideDialog()
                            }
                        })

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
            activity?.featureDetailAlert(
                "",
                getString(R.string.text_info_ownership_transfer)
            )
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

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                activity?.loadingDialog()
                viewModel.getUserProfile()
                viewModel.getOwnership()
            } else {
                Log.e(logTag, " internet is not available")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.getUserProfileResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
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
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    "getUserProfileResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.updateUserProfileResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                Log.e(logTag, " ${response.values.message} ")
                                context?.showToast(response.values.message)
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

                                        FastSave.getInstance()
                                            .saveString(Constants.USER_ID, userData.iUserId)
                                        FastSave.getInstance()
                                            .saveString(
                                                Constants.USER_FULL_NAME,
                                                userData.vFullName
                                            )
                                        FastSave.getInstance()
                                            .saveString(Constants.USERNAME, userData.vUserName)
                                        FastSave.getInstance()
                                            .saveString(Constants.USER_EMAIL, userData.vEmail)
                                        FastSave.getInstance()
                                            .saveString(
                                                Constants.USER_PHONE_NUMBER,
                                                userData.bPhoneNumber
                                            )

                                    }

                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    "getUserProfileResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.changePasswordResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " changePasswordResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.getOwnershipResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let {
                                        binding.tvMasterName.text = it.name
                                        binding.tvMasterEmail.text = it.email
//                            binding.btnUpdate.isEnabled = false
                                        if (it.isEmailVerified == 0) {
                                            cancelOwnership = true
                                            ownershipId = it.id
                                            binding.btnTransferOwnership.text =
                                                getString(R.string.text_cancel)
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
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " getOwnershipResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.transferOwnershipResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    getOwnership()
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " transferOwnershipResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.cancelOwnershipResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    getOwnership()
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " cancelOwnershipResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

            }

        }

    }

    private fun getOwnership() {
        activity?.loadingDialog()
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
                        activity?.loadingDialog()
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