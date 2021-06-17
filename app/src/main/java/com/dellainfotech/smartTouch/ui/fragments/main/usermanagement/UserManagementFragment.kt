package com.dellainfotech.smartTouch.ui.fragments.main.usermanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.UserManagementAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.model.SubordinateUserData
import com.dellainfotech.smartTouch.api.repository.UserManagementRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentUserManagementBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.UserManagementViewModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class UserManagementFragment :
    ModelBaseFragment<UserManagementViewModel, FragmentUserManagementBinding, UserManagementRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var userManagementAdapter: UserManagementAdapter
    private var userList = arrayListOf<SubordinateUserData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivRegisterUser.setOnClickListener {
            findNavController().navigate(UserManagementFragmentDirections.actionUserManagementFragmentToAddUserFragment())
        }

        userManagementAdapter = UserManagementAdapter(userList)
        binding.recyclerRegisteredUser.adapter = userManagementAdapter

        apiCall()
    }

    private fun apiCall() {

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                showProgressDialog()
                viewModel.getSubordinateUser()
            } else {
                Log.e(logTag, " internet is not available")
            }
        })

        viewModel.getSubordinateUserResponse.observe(viewLifecycleOwner, { response ->
            userList.clear()
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { userData ->
                            userList.addAll(userData)
                            userManagementAdapter.notifyDataSetChanged()
                            userManagementAdapter.setOnRemoveClickListener(object :
                                AdapterItemClickListener<SubordinateUserData> {
                                override fun onItemClick(data: SubordinateUserData) {
                                    activity?.let {
                                        DialogUtil.askAlert(
                                            it,
                                            getString(R.string.dialog_title_remove_subordinate_user),
                                            getString(R.string.text_yes),
                                            getString(R.string.text_no),
                                            object : DialogAskListener {
                                                override fun onYesClicked() {
                                                    showProgressDialog()
                                                    Log.e(
                                                        logTag,
                                                        " deleteSubordinateUser id ${data.id}"
                                                    )
                                                    viewModel.deleteSubordinateUser(data.id)
                                                }

                                                override fun onNoClicked() {
                                                }

                                            }
                                        )
                                    }

                                }

                            })
                        }
                    } else {
                        userManagementAdapter.notifyDataSetChanged()
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " getSubordinateUserResponse Failure ${response.errorBody?.string()}  "
                    )
                }
                else -> {
                    // we will do nothing here
                }
            }
        })

        viewModel.deleteSubordinateUserResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        showProgressDialog()
                        viewModel.getSubordinateUser()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " deleteSubordinateUserResponse Failure ${response.errorBody?.string()}  "
                    )
                }
                else -> {
                    // we will do nothing here
                }
            }
        })
    }

    private fun showProgressDialog() {
        activity?.let {
            DialogUtil.loadingAlert(it)
        }
    }

    override fun getViewModel(): Class<UserManagementViewModel> =
        UserManagementViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserManagementBinding =
        FragmentUserManagementBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserManagementRepository =
        UserManagementRepository(networkModel)

}