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
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentAddUserBinding
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.UserManagementViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class AddUserFragment :
    ModelBaseFragment<UserManagementViewModel, FragmentAddUserBinding, UserManagementRepository>() {

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
                        DialogUtil.hideDialog()
                        context?.showToast(response.values.message)
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(logTag, " addSubordinateUserResponse ${response.errorBody?.string()}")
                    }
                    else -> {
                        // we will do nothing here
                    }
                }
            }

        }

        binding.btnAddUser.setOnClickListener {
            val userName = binding.edtFullName.text.toString().trim()
            val email = binding.edtEmailAddress.text.toString().trim()

            when {
                userName.isEmpty() -> {
                    binding.edtFullName.error = "Please enter name."
                    binding.edtFullName.requestFocus()
                }
                email.isEmpty() -> {
                    binding.edtEmailAddress.error = "Please enter email."
                    binding.edtEmailAddress.requestFocus()
                }
                else -> {

                    if (!isInternetConnected()){
                        context?.getString(R.string.text_no_internet_available)
                        return@setOnClickListener
                    }

                    activity?.let {
                        DialogUtil.loadingAlert(it)
                        viewModel.addSubordinateUser(BodyAddSubordinateUser(userName, email))
                    }
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