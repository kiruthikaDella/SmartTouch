package com.dellainfotech.smartTouch.ui.fragments.main.controlmode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.controlmodeadapter.ControlModeAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.body.BodyPinStatus
import com.dellainfotech.smartTouch.api.model.ControlModeRoomData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.isControlModePin
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.common.utils.showToast
import com.dellainfotech.smartTouch.databinding.FragmentControlModeBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.activities.MainActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */

class ControlModeFragment :
    ModelBaseFragment<HomeViewModel, FragmentControlModeBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var controlModeAdapter: ControlModeAdapter
    private var roomList = arrayListOf<ControlModeRoomData>()
    private var mGoogleSingInClient: GoogleSignInClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isControlModePin()) {
            binding.ibLogout.isVisible = true
            context?.let { mContext ->
                binding.ibPin.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_pin_straight
                    )
                )
            }
        }

        initGoogleSignInClient()

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getControl()
            } else {
                Log.e(logTag, " internet is not available")
            }
        })

        binding.ibPin.setOnClickListener {

            if (!isInternetConnected()){
                context?.showToast(getString(R.string.text_no_internet_available))
                return@setOnClickListener
            }

            activity?.let {
                var isPinned = isControlModePin()
                val msg: String = if (isPinned) {
                    isPinned = false
                    getString(R.string.dialog_title_unpin_control_mode)
                } else {
                    isPinned = true
                    getString(R.string.dialog_title_pin_control_mode)
                }
                DialogUtil.askAlert(
                    it,
                    msg,
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            DialogUtil.loadingAlert(it)
                            viewModel.updatePinStatus(BodyPinStatus(isPinned.toInt()))
                        }

                        override fun onNoClicked() {

                        }
                    }
                )
            }
        }

        binding.ibLogout.setOnClickListener {

            activity?.let { mActivity ->
                DialogUtil.askAlert(
                    mActivity,
                    getString(R.string.dialog_title_logout),
                    getString(R.string.text_yes),
                    getString(R.string.text_no),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            DialogUtil.hideDialog()
                            val loginType =
                                FastSave.getInstance().getString(Constants.LOGIN_TYPE, "")
                            if (loginType == Constants.LOGIN_TYPE_GOOGLE) {
                                mGoogleSingInClient?.signOut()
                            } else if (loginType == Constants.LOGIN_TYPE_FACEBOOK) {
                                LoginManager.getInstance().logOut()
                            }

                            DialogUtil.loadingAlert(mActivity)

                            viewModel.logout(
                                BodyLogout(
                                    FastSave.getInstance().getString(Constants.MOBILE_UUID, null)
                                )
                            )
                        }

                        override fun onNoClicked() {
                            DialogUtil.hideDialog()
                        }

                    })
            }
        }


        apiCall()
    }

    //Initialization object of GoogleSignInClient
    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSingInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onStop() {
        super.onStop()
        DialogUtil.hideDialog()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentControlModeBinding = FragmentControlModeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun apiCall() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.logoutResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    activity?.let {

                                        val sharedPreference = it.getSharedPreferences(
                                            Constants.SHARED_PREF,
                                            Context.MODE_PRIVATE
                                        )
                                        val isRemember = sharedPreference.getBoolean(
                                            Constants.IS_REMEMBER,
                                            Constants.DEFAULT_REMEMBER_STATUS
                                        )
                                        val loginType =
                                            sharedPreference.getString(Constants.LOGGED_IN_TYPE, "")

                                        if (loginType == Constants.LOGIN_TYPE_NORMAL) {
                                            if (!isRemember) {
                                                val editor = sharedPreference.edit()
                                                editor.clear()
                                                editor.apply()
                                            }
                                        } else {
                                            val editor = sharedPreference.edit()
                                            editor.clear()
                                            editor.apply()
                                        }

                                        FastSave.getInstance().clearSession()
                                        startActivity(
                                            Intent(
                                                it,
                                                AuthenticationActivity::class.java
                                            )
                                        )
                                        it.finishAffinity()
                                    }
                                } else {
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, "logout error ${response.errorBody?.string()}")
                            }
                            else -> {
                                // We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.updatePinStatusResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let {
                                        FastSave.getInstance().saveBoolean(
                                            Constants.isControlModePinned,
                                            it.isPinStatus.toBoolean()
                                        )

                                        if (it.isPinStatus.toBoolean()) {
                                            (activity as MainActivity).hideBottomNavigation()
                                            binding.ibLogout.isVisible = true
                                            context?.let { mContext ->
                                                binding.ibPin.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        mContext,
                                                        R.drawable.ic_pin_straight
                                                    )
                                                )
                                            }

                                        } else {
                                            (activity as MainActivity).showBottomNavigation()
                                            binding.ibLogout.isVisible = false
                                            context?.let { mContext ->
                                                binding.ibPin.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        mContext,
                                                        R.drawable.ic_pin
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, " updatePinStatusResponse Failure $response ")
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {

                    viewModel.getControlResponse.collectLatest { response ->
                        roomList.clear()
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()

                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { roomDataList ->
                                        roomList.addAll(roomDataList)
                                        activity?.let { mActivity ->
                                            controlModeAdapter = ControlModeAdapter(mActivity, roomList)
                                            binding.recyclerControlModes.adapter = controlModeAdapter
                                        }

                                    }
                                } else {
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, " getControlResponse Failure ${response.errorBody?.string()} ")
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }

                }
            }

        }




    }
}