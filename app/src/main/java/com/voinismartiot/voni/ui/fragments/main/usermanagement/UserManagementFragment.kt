package com.voinismartiot.voni.ui.fragments.main.usermanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.UserManagementAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.model.SubordinateUserData
import com.voinismartiot.voni.api.repository.UserManagementRepository
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.databinding.FragmentUserManagementBinding
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.UserManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserManagementFragment :
    BaseFragment<UserManagementViewModel, FragmentUserManagementBinding, UserManagementRepository>() {

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
                activity?.askAlert(
                    getString(R.string.dialog_title_remove_subordinate_user),
                    getString(R.string.text_yes),
                    getString(R.string.text_no),
                    object : DialogAskListener {
                        override fun onYesClicked() {

                            if (!Utils.isNetworkConnectivityAvailable()) {
                                context?.showToast(getString(R.string.text_no_internet_available))
                            } else {
                                showProgressDialog()
                                userData = data
                                viewModel.deleteSubordinateUser(data.id)
                            }
                        }

                        override fun onNoClicked() = Unit

                    }
                )

            }

        })

        binding.pullToRefresh.setOnRefreshListener {
            userList.clear()
            userManagementAdapter.notifyDataSetChanged()
            viewModel.getSubordinateUser()
        }

        apiCall()
    }

    private fun apiCall() {

        NotifyManager.internetInfo.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                Log.e(logTag, " internet is not available connected $isConnected")
                showProgressDialog()
                viewModel.getSubordinateUser()
            } else {
                Log.e(logTag, " internet is not available")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getSubordinateUserResponse.collectLatest { response ->
                        userList.clear()
                        binding.pullToRefresh.isRefreshing = response is Resource.Loading
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { userData ->
                                        userList.addAll(userData)
                                    }
                                } else {
                                    context?.showToast(response.values.message)
                                }
                                userManagementAdapter.notifyDataSetChanged()
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " getSubordinateUserResponse Failure ${response.errorBody?.string()}  "
                                )
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.deleteSubordinateUserResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    userData?.let {
                                        userList.remove(it)
                                        userManagementAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " deleteSubordinateUserResponse Failure ${response.errorBody?.string()}  "
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun showProgressDialog() {
        activity?.loadingDialog()
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