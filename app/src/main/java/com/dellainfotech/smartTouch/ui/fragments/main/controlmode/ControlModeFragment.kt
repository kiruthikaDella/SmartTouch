package com.dellainfotech.smartTouch.ui.fragments.main.controlmode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
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

        if (FastSave.getInstance()
                .getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)
        ) {
            binding.ibLogout.isVisible = true
            context?.let { mContext ->
                binding.ibPin.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_pin_straight))
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
            activity?.let {
                var isPinned = FastSave.getInstance().getBoolean(
                    Constants.isControlModePinned,
                    Constants.DEFAULT_CONTROL_MODE_STATUS
                )
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
                DialogUtil.askAlert(mActivity, getString(R.string.dialog_title_logout), getString(R.string.text_yes),getString(R.string.text_no), object : DialogAskListener {
                    override fun onYesClicked() {
                        DialogUtil.hideDialog()
                        val loginType = FastSave.getInstance().getString(Constants.LOGIN_TYPE, "")
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
        viewModel.updatePinStatusResponse.postValue(null)
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentControlModeBinding = FragmentControlModeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    private fun apiCall(){

        viewModel.logoutResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        activity?.let {

                            val sharedPreference =  it.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
                            val isRemember = sharedPreference.getBoolean(Constants.IS_REMEMBER,Constants.DEFAULT_REMEMBER_STATUS)
                            val loginType = sharedPreference.getString(Constants.LOGGED_IN_TYPE, "")

                            if (loginType == Constants.LOGIN_TYPE_NORMAL){
                                if (!isRemember){
                                    val editor = sharedPreference.edit()
                                    editor.clear()
                                    editor.apply()
                                }
                            }else {
                                val editor = sharedPreference.edit()
                                editor.clear()
                                editor.apply()
                            }

                            FastSave.getInstance().clearSession()
                            startActivity(Intent(it, AuthenticationActivity::class.java))
                            it.finishAffinity()
                        }
                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "logout error ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.getControlResponse.observe(viewLifecycleOwner, { response ->
            roomList.clear()
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let { roomDataList ->
                            roomList.addAll(roomDataList)
                            activity?.let { mActivity ->
                                Log.e(logTag, "  roomList ${roomList.size}")
                                controlModeAdapter = ControlModeAdapter(mActivity, roomList)
                                binding.recyclerControlModes.adapter = controlModeAdapter
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
                    Log.e(logTag, " getControlResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.updatePinStatusResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
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
                                    binding.ibPin.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_pin_straight))
                                }

                            } else {
                                (activity as MainActivity).showBottomNavigation()
                                binding.ibLogout.isVisible = false
                                context?.let { mContext ->
                                    binding.ibPin.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_pin))
                                }
                            }
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " updatePinStatusResponse Failure $response ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

    }
}