package com.dellainfotech.smartTouch.ui.fragments.main.usermanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentUserManagementBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.UserManagementViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class UserManagementFragment :
    ModelBaseFragment<UserManagementViewModel, FragmentUserManagementBinding, UserManagementRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var userManagementAdapter: UserManagementAdapter
    private var userList = arrayListOf<SubordinateUserData>()
    private var userData: SubordinateUserData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivRegisterUser.setOnClickListener {
            findNavController().navigate(UserManagementFragmentDirections.actionUserManagementFragmentToAddUserFragment())
        }

        userManagementAdapter = UserManagementAdapter(userList)
        binding.recyclerRegisteredUser.adapter = userManagementAdapter
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

                                if (!isInternetConnected()){
                                    context?.showToast(getString(R.string.text_no_internet_available))
                                }else {
                                    showProgressDialog()
                                    userData = data
                                    viewModel.deleteSubordinateUser(data.id)
                                }
                            }

                            override fun onNoClicked() {
                            }

                        }
                    )
                }

            }

        })

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

        lifecycleScope.launchWhenStarted {

            viewModel.getSubordinateUserResponse.collectLatest { response ->
                userList.clear()
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            response.values.data?.let { userData ->
                                userList.addAll(userData)
                                userManagementAdapter.notifyDataSetChanged()
                            }
                        } else {
                            userManagementAdapter.notifyDataSetChanged()
                            context?.showToast(response.values.message)
                        }
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(
                            logTag,
                            " getSubordinateUserResponse Failure ${response.errorBody?.string()}  "
                        )
                    }
                    else -> {
                        // we will do nothing here
                    }
                }
            }

            viewModel.deleteSubordinateUserResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        context?.showToast(response.values.message)
                        if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                            userData?.let {
                                userList.remove(it)
                                userManagementAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    is Resource.Failure -> {
                        DialogUtil.hideDialog()
                        context?.showToast(getString(R.string.error_something_went_wrong))
                        Log.e(
                            logTag,
                            " deleteSubordinateUserResponse Failure ${response.errorBody?.string()}  "
                        )
                    }
                    else -> {
                        // we will do nothing here
                    }
                }
            }

        }

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