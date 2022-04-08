package com.voinismartiot.voni.ui.fragments.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.iid.FirebaseInstanceId
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyLogin
import com.voinismartiot.voni.api.body.BodySocialLogin
import com.voinismartiot.voni.api.model.UserProfile
import com.voinismartiot.voni.api.repository.AuthRepository
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.interfaces.FacebookLoginListener
import com.voinismartiot.voni.common.utils.*
import com.voinismartiot.voni.common.utils.Utils.isNetworkConnectivityAvailable
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.Utils.toEditable
import com.voinismartiot.voni.databinding.FragmentLoginBinding
import com.voinismartiot.voni.ui.activities.AuthenticationActivity
import com.voinismartiot.voni.ui.activities.MainActivity
import com.voinismartiot.voni.ui.fragments.BaseFragment
import com.voinismartiot.voni.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("ClickableViewAccessibility")
class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {

    private val logTag = this::class.java.simpleName
    private var isPasswordVisible = false

    //Google SignIn
    private var mGoogleSingInClient: GoogleSignInClient? = null

    private var loginType = Constants.LOGIN_TYPE_NORMAL
    private var deviceId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)

        Log.e(logTag, " android id $deviceId")

        initGoogleSignInClient()

        messagingServices()

        context?.let {
            FacebookSdk.sdkInitialize(it)
            Utils.generateSSHKey(it)

            val sharedPreference =
                it.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)

            val isRememberMeChecked = sharedPreference.getBoolean(
                Constants.IS_REMEMBER,
                Constants.DEFAULT_REMEMBER_STATUS
            )
            binding.checkboxRemember.isChecked = isRememberMeChecked

            val loginType = sharedPreference.getString(Constants.LOGGED_IN_TYPE, "")

            if (isRememberMeChecked && loginType == Constants.LOGIN_TYPE_NORMAL) {
                val email = sharedPreference.getString(Constants.LOGGED_IN_EMAIL, null)
                val password = sharedPreference.getString(Constants.LOGGED_IN_PASSWORD, null)

                email?.let {
                    binding.edtEmail.text = email.toEditable()
                    binding.edtPassword.text = password?.toEditable()
                }
            }

            binding.checkboxRemember.setOnCheckedChangeListener { _, isChecked ->
                val editor = sharedPreference?.edit()
                editor?.putBoolean(Constants.IS_REMEMBER, isChecked)
                editor?.apply()
            }
        }

        clickEvents()
        apiCall()
    }

    private fun clickEvents() {

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }

        binding.btnLogin.setOnClickListener {
            if (isNetworkConnectivityAvailable()) {
                validateUserInformation()
            } else {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            hideDialog()
                            findNavController().navigateUp()
                        }

                    }
                )
            }
        }

        binding.linearFacebook.setOnClickListener {
            if (isNetworkConnectivityAvailable()) {
                (activity as AuthenticationActivity).performFacebookLogin()
            } else {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available)
                )
            }
        }

        binding.ivHidePassword.setOnClickListener {
            if (isPasswordVisible) {
                isPasswordVisible = false
                context?.let {
                    binding.ivHidePassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_password_hidden
                        )
                    )
                    binding.edtPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
            } else {
                isPasswordVisible = true
                context?.let {
                    binding.ivHidePassword.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_password_visible
                        )
                    )
                    binding.edtPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
            }
        }

        (activity as AuthenticationActivity).setFacebookLoginListener(object :
            FacebookLoginListener {
            override fun performLogin(userId: String, email: String) {
                Log.e(logTag, " socialLoginAPI ")
                val uuid: String = UUID.randomUUID().toString()

                loginType = Constants.LOGIN_TYPE_FACEBOOK

                activity?.let {
                    it.loadingDialog()
                    viewModel.socialLogin(
                        BodySocialLogin(
                            userId,
                            deviceId,
                            Constants.SOCIAL_LOGIN,
                            Constants.LOGIN_TYPE_FACEBOOK,
                            email,
                            Utils.getFCMToken()
                        )
                    )
                }
            }

        })

        activityLauncher()
    }

    private fun activityLauncher() {

        val googleSignInResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    if (task.isSuccessful) {
                        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                        account?.let {

                            Log.d(logTag, "Success Login User Id ${it.id} email ${it.email}")

//                            val name = it.displayName?.split(" ")
//                            val firstName = name?.get(0)
//                            val lastName = name?.get(1)
                            val email = it.email ?: ""
                            Log.d(logTag, "account ${it.displayName} ")

                            val uuid: String = UUID.randomUUID().toString()
                            FastSave.getInstance().saveString(Constants.MOBILE_UUID, uuid)

                            activity?.loadingDialog()

                            loginType = Constants.LOGIN_TYPE_GOOGLE

                            viewModel.socialLogin(
                                BodySocialLogin(
                                    it.id.toString(),
                                    deviceId,
                                    Constants.SOCIAL_LOGIN,
                                    Constants.LOGIN_TYPE_GOOGLE,
                                    email,
                                    Utils.getFCMToken()
                                )
                            )
                        }
                    } else {
                        Log.e(logTag, "Google sign in cancelled")
                    }
                } catch (e: Exception) {
                    Log.e(logTag, "${e.printStackTrace()}")
                }

            }
        }

        binding.linearGoogle.setOnClickListener {
            if (isNetworkConnectivityAvailable()) {

                val intent = mGoogleSingInClient?.signInIntent
                googleSignInResultLauncher.launch(intent)

            } else {
                activity?.deviceOfflineAlert(
                    getString(R.string.text_no_internet_available)
                )
            }
        }
    }

    private fun apiCall() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.loginResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                                    val userProfile: UserProfile? = response.values.data?.user_data
                                    userProfile?.let { userData ->
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

                                        if (userData.userRole == Constants.MASTER_USER) {
                                            FastSave.getInstance()
                                                .saveBoolean(Constants.IS_MASTER_USER, true)
                                        } else {
                                            FastSave.getInstance()
                                                .saveBoolean(Constants.IS_MASTER_USER, false)
                                        }

                                        FastSave.getInstance()
                                            .saveString(Constants.SOCIAL_ID, userData.socialId)
                                        FastSave.getInstance().saveBoolean(
                                            Constants.isControlModePinned,
                                            userData.iIsPinStatus!!.toBoolean()
                                        )
                                        FastSave.getInstance()
                                            .saveString(
                                                Constants.LOGIN_TYPE,
                                                loginType
                                            )

                                        if (binding.checkboxRemember.isChecked) {
                                            val sharedPreference = activity?.getSharedPreferences(
                                                Constants.SHARED_PREF,
                                                Context.MODE_PRIVATE
                                            )
                                            val editor = sharedPreference?.edit()
                                            editor?.putString(
                                                Constants.LOGGED_IN_EMAIL,
                                                binding.edtEmail.text.toString()
                                            )
                                            editor?.putString(
                                                Constants.LOGGED_IN_PASSWORD,
                                                binding.edtPassword.text.toString()
                                            )
                                            editor?.putString(
                                                Constants.LOGGED_IN_TYPE,
                                                loginType
                                            )
                                            editor?.apply()
                                        }
                                        activity?.let {
                                            startActivity(Intent(it, MainActivity::class.java))
                                            it.finishAffinity()
                                        }
                                    }
                                    FastSave.getInstance()
                                        .saveString(
                                            Constants.ACCESS_TOKEN,
                                            response.values.data?.accessToken
                                        )

                                } else {
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, "login error ${response.errorBody?.string()}")
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.socialLoginResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideDialog()

                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                                    val userProfile: UserProfile? = response.values.data?.user_data
                                    userProfile?.let { userData ->
                                        FastSave.getInstance()
                                            .saveString(Constants.USER_ID, userData.iUserId)
                                        FastSave.getInstance().saveString(
                                            Constants.USER_FULL_NAME,
                                            userData.vFullName
                                        )
                                        FastSave.getInstance()
                                            .saveString(Constants.USERNAME, userData.vUserName)
                                        FastSave.getInstance()
                                            .saveString(Constants.USER_EMAIL, userData.vEmail)
                                        FastSave.getInstance().saveString(
                                            Constants.USER_PHONE_NUMBER,
                                            userData.bPhoneNumber
                                        )
                                        FastSave.getInstance()
                                            .saveString(Constants.SOCIAL_ID, userData.socialId)
                                        FastSave.getInstance().saveBoolean(
                                            Constants.isControlModePinned,
                                            userData.iIsPinStatus!!.toBoolean()
                                        )

                                        if (userData.userRole == Constants.MASTER_USER) {
                                            FastSave.getInstance()
                                                .saveBoolean(Constants.IS_MASTER_USER, true)
                                        } else {
                                            FastSave.getInstance()
                                                .saveBoolean(Constants.IS_MASTER_USER, false)
                                        }

                                        val sharedPreference = activity?.getSharedPreferences(
                                            Constants.SHARED_PREF,
                                            Context.MODE_PRIVATE
                                        )
                                        val editor = sharedPreference?.edit()
                                        editor?.putString(
                                            Constants.LOGGED_IN_TYPE,
                                            loginType
                                        )
                                        editor?.apply()

                                        activity?.let {
                                            FastSave.getInstance()
                                                .saveString(
                                                    Constants.LOGIN_TYPE,
                                                    loginType
                                                )
                                            startActivity(Intent(it, MainActivity::class.java))
                                            it.finishAffinity()
                                        }
                                    }
                                    FastSave.getInstance()
                                        .saveString(
                                            Constants.ACCESS_TOKEN,
                                            response.values.data?.accessToken
                                        )

                                } else {
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, "login error ${response.errorBody?.string()}")
                            }
                            else -> Unit
                        }
                    }
                }

            }

        }

    }

    private fun validateUserInformation() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val uuid: String = UUID.randomUUID().toString()
        FastSave.getInstance().saveString(Constants.MOBILE_UUID, uuid)

        if (email.isEmpty()) {
            binding.edtEmail.error = getString(R.string.error_text_email)
            binding.edtEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = getString(R.string.error_text_valid_email)
            binding.edtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edtPassword.error = getString(R.string.error_text_password)
            binding.edtPassword.requestFocus()
        } else {
            Log.e(logTag, "Valid")
            activity?.loadingDialog()
            viewModel.login(
                BodyLogin(
                    email, password, deviceId, Utils.getFCMToken()
                )
            )
        }
    }

    //Initialization object of GoogleSignInClient
    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSingInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun getViewModel(): Class<AuthViewModel> = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository = AuthRepository(networkModel)

    private fun messagingServices() {
        /* FirebaseInstallations.getInstance().id.addOnCompleteListener { task: Task<String?> ->
             if (task.isSuccessful) {
                 val token = task.result
                 Log.i("$logTag FirebaseMessagingService token ---->>", "$token")
                 FastSave.getInstance().saveString(Constants.FCM_TOKEN, token)
             }
         }*/

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val strToken = it.token
            FastSave.getInstance().saveString(Constants.FCM_TOKEN, strToken)
        }
    }

}