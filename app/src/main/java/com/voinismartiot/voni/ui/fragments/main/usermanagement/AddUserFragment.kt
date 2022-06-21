package com.voinismartiot.voni.ui.fragments.main.usermanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyAddSubordinateUser
import com.voinismartiot.voni.api.repository.UserManagementRepository
import com.voinismartiot.voni.common.utils.Utils
import com.voinismartiot.voni.common.utils.Utils.isValidEmail
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.common.utils.loadingDialog
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentAddUserBinding
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.UserManagementViewModel
import kotlinx.coroutines.flow.collectLatest

class AddUserFragment :
    BaseFragment<UserManagementViewModel, FragmentAddUserBinding, UserManagementRepository>() {

    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addSubordinateUserResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.edtFullName.setText("")
                        binding.edtEmailAddress.setText("")
                        hideDialog()
                        context?.showToast(response.values.message)
                    }
                    is Resource.Failure -> {
                        hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, " addSubordinateUserResponse ${response.errorBody?.string()}")
                    }
                    else -> Unit
                }
            }

        }

        binding.btnAddUser.setOnClickListener {
            val userName = binding.edtFullName.text.toString().trim()
            val email = binding.edtEmailAddress.text.toString().trim()

            when {
                userName.isEmpty() -> {
                    binding.edtFullName.error = getString(R.string.error_text_name)
                    binding.edtFullName.requestFocus()
                }
                email.isEmpty() -> {
                    binding.edtEmailAddress.error = getString(R.string.error_text_email)
                    binding.edtEmailAddress.requestFocus()
                }
                !email.isValidEmail() -> {
                    binding.edtEmailAddress.error = getString(R.string.error_text_valid_email)
                    binding.edtEmailAddress.requestFocus()
                }
                else -> {

                    if (!Utils.isNetworkConnectivityAvailable()) {
                        context?.getString(R.string.text_no_internet_available)
                        return@setOnClickListener
                    }

                    activity?.loadingDialog()
                    viewModel.addSubordinateUser(BodyAddSubordinateUser(userName, email))

                }
            }
        }
    }

    override fun getViewModel(): Class<UserManagementViewModel> =
        UserManagementViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddUserBinding = FragmentAddUserBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserManagementRepository =
        UserManagementRepository(networkModel)

}