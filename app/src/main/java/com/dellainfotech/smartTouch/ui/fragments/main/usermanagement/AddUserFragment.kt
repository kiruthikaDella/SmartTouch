package com.dellainfotech.smartTouch.ui.fragments.main.usermanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddSubordinateUser
import com.dellainfotech.smartTouch.api.repository.UserManagementRepository
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentAddUserBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.UserManagementViewModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class AddUserFragment : ModelBaseFragment<UserManagementViewModel, FragmentAddUserBinding, UserManagementRepository>() {

    private val logTag = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.addSubordinateUserResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    binding.edtFullName.setText("")
                    binding.edtEmailAddress.setText("")
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                    Log.e(logTag, " addSubordinateUserResponse ${response.errorBody?.string()}")
                }
                else -> {
                    // we will do nothing here
                }
            }
        })

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
                    activity?.let {
                        DialogUtil.loadingAlert(it)
                        viewModel.addSubordinateUser(BodyAddSubordinateUser(userName, email))
                    }
                }
            }
        }
    }

    override fun getViewModel(): Class<UserManagementViewModel> = UserManagementViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddUserBinding = FragmentAddUserBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserManagementRepository = UserManagementRepository(networkModel)

    override fun onStop() {
        super.onStop()
        viewModel.addSubordinateUserResponse.postValue(null)
    }
}