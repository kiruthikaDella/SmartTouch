package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.annotation.SuppressLint
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
import android.view.*
import android.widget.EditText
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
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
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
                    binding.edtName.text.toString().trim(),
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
                                    binding.edtName.text = string.toEditable()
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
                    binding.edtPhoneNumber.text.toString().trim(),
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
                                    binding.edtPhoneNumber.text = string.toEditable()
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
            val fullName = binding.edtName.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()

            when {
                fullName.isEmpty() -> {
                    binding.edtName.error = getString(R.string.error_text_full_name)
                }
                fullName.length < 3 -> {
                    binding.edtName.error = getString(R.string.error_text_full_name_length)
                }
                phoneNumber.isEmpty() -> {
                    binding.edtPhoneNumber.error = getString(R.string.error_text_phone_number)
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
                    binding.edtMasterName.text.toString().trim(),
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
                                    binding.edtMasterName.text = string.toEditable()
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
                    binding.edtMasterEmail.text.toString().trim(),
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
                                binding.edtMasterEmail.text = string.toEditable()
                            }

                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
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
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtMasterEmail.error = getString(R.string.error_text_valid_email)
            } else {
                if (cancelOwnership) {

                    activity?.let {
                        DialogUtil.askAlert(it,getString(R.string.dialog_title_cancel_ownership),getString(R.string.text_ok),getString(R.string.text_cancel), object : DialogAskListener{
                            override fun onYesClicked() {
                                DialogUtil.hideDialog()
                                DialogUtil.loadingAlert(it)
                                ownershipId?.let {
                                    viewModel.cancelOwnership(it)
                                }
                            }

                            override fun onNoClicked() {
                                DialogUtil.hideDialog()
                            }
                        })
                    }
                } else {
                    activity?.let {
                        DialogUtil.askAlert(it,getString(R.string.dialog_title_transfer_ownership),getString(R.string.text_ok),getString(R.string.text_cancel), object : DialogAskListener{
                            override fun onYesClicked() {
                                DialogUtil.hideDialog()
                                DialogUtil.loadingAlert(it)
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
            binding.edtPassword.visibility = View.INVISIBLE
            binding.ivEditPassword.visibility = View.INVISIBLE
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
                            binding.edtMasterName.text = it.name.toEditable()
                            binding.edtMasterEmail.text = it.email.toEditable()
//                            binding.btnUpdate.isEnabled = false
                            if (it.isEmailVerified == 0) {
                                cancelOwnership = true
                                ownershipId = it.id
                                binding.btnUpdate.text = getString(R.string.text_cancel)
                                binding.edtMasterName.clearFocus()
                                binding.edtMasterEmail.clearFocus()
                                binding.ivMasterEditName.isEnabled = false
                                binding.ivMasterEditEmail.isEnabled = false
                            }
                        }
                    } else {
                        binding.edtMasterName.text = "".toEditable()
                        binding.edtMasterEmail.text = "".toEditable()
                        cancelOwnership = false
                        ownershipId = null
                        binding.btnUpdate.text = getString(R.string.text_transfer_ownership)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun dialogUpdatePassword() {
        activity?.let { myActivity ->
            dialog = Dialog(myActivity)
            dialog?.setContentView(R.layout.dialog_password)
            dialog?.setCancelable(true)

            var isCurrentPasswordVisible = false
            var isPasswordVisible = false
            var isConfirmPasswordVisible = false

            val edtCurrentPassword = dialog?.findViewById(R.id.edt_current_password) as EditText
            val edtPassword = dialog?.findViewById(R.id.edt_password) as EditText
            val edtConfirmPassword = dialog?.findViewById(R.id.edt_confirm_password) as EditText
            val btnSave = dialog?.findViewById(R.id.btn_save) as MaterialButton
            val btnCancel = dialog?.findViewById(R.id.btn_cancel) as MaterialButton


            edtCurrentPassword.setOnTouchListener { _, event ->
                val drawableEnd = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtCurrentPassword.right - edtCurrentPassword.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isCurrentPasswordVisible) {
                            isCurrentPasswordVisible = false
                            context?.let {
                                edtCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(it, R.drawable.ic_password_visible),
                                    null
                                )
                                edtCurrentPassword.transformationMethod =
                                    HideReturnsTransformationMethod.getInstance()
                            }
                        } else {
                            isCurrentPasswordVisible = true
                            context?.let {
                                edtCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(it, R.drawable.ic_password_hidden),
                                    null
                                )
                                edtCurrentPassword.transformationMethod =
                                    PasswordTransformationMethod.getInstance()
                            }
                        }

                    }
                }
                false
            }

            edtPassword.setOnTouchListener { _, event ->
                val drawableEnd = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtPassword.right - edtPassword.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isPasswordVisible) {
                            isPasswordVisible = false
                            context?.let {
                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(it, R.drawable.ic_password_visible),
                                    null
                                )
                                edtPassword.transformationMethod =
                                    HideReturnsTransformationMethod.getInstance()
                            }
                        } else {
                            isPasswordVisible = true
                            context?.let {
                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(it, R.drawable.ic_password_hidden),
                                    null
                                )
                                edtPassword.transformationMethod =
                                    PasswordTransformationMethod.getInstance()
                            }
                        }

                    }
                }
                false
            }

            edtConfirmPassword.setOnTouchListener { _, event ->
                val drawableEnd = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtConfirmPassword.right - edtConfirmPassword.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isConfirmPasswordVisible) {
                            isConfirmPasswordVisible = false
                            context?.let {
                                edtConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(it, R.drawable.ic_password_visible),
                                    null
                                )
                                edtConfirmPassword.transformationMethod =
                                    HideReturnsTransformationMethod.getInstance()
                            }
                        } else {
                            isConfirmPasswordVisible = true
                            context?.let {
                                edtConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(it, R.drawable.ic_password_hidden),
                                    null
                                )
                                edtConfirmPassword.transformationMethod =
                                    PasswordTransformationMethod.getInstance()
                            }
                        }

                    }
                }
                false
            }

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }

            btnSave.setOnClickListener {
                val currentPassword = edtCurrentPassword.text.toString().trim()
                val newPassword = edtPassword.text.toString().trim()
                val confirmPassword = edtConfirmPassword.text.toString().trim()

                when {
                    currentPassword.isEmpty() -> {
                        edtCurrentPassword.error = getString(R.string.error_text_current_password)
                    }
                    newPassword.isEmpty() -> {
                        edtPassword.error = getString(R.string.error_text_password)
                    }
                    newPassword.length < Constants.PASSWORD_LENGTH -> {
                        edtPassword.error = getString(R.string.error_text_password_length)
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