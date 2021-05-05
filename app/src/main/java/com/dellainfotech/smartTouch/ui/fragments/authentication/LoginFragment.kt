package com.dellainfotech.smartTouch.ui.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyLogin
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils
import com.dellainfotech.smartTouch.common.utils.Utils.isNetworkConnectivityAvailable
import com.dellainfotech.smartTouch.common.utils.Utils.showAlertDialog
import com.dellainfotech.smartTouch.databinding.FragmentLoginBinding
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.activities.MainActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import java.util.*

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class LoginFragment : ModelBaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {

    private val logTag = this::class.java.simpleName

    //Google SignIn
    private val GOOGLE_SIGN_IN_REQUEST = 1
    private var mGoogleSingInClient: GoogleSignInClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoogleSignInClient()

        context?.let {
            Utils.generateSSHKey(it)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }

        binding.edtEmail.setText("archit.ghetiya@teksun.com")
        binding.edtPassword.setText("12345")

        binding.btnLogin.setOnClickListener {
//            validateUserInformation()
            context?.let {
                startActivity(Intent(it, MainActivity::class.java))
            }
        }

        binding.linearGoogle.setOnClickListener {
            if (isNetworkConnectivityAvailable()) {
                val intent = mGoogleSingInClient?.signInIntent
                startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST)
            } else {
                context?.let {
                    showAlertDialog(
                        it,
                        getString(R.string.text_no_internet_available),
                        "",
                        getString(R.string.text_ok),
                        null
                    )
                }
            }
        }

        binding.linearFacebook.setOnClickListener {
            if (isNetworkConnectivityAvailable()) {
                (activity as AuthenticationActivity)?.performFacebookLogin()
            } else {
                context?.let {
                    showAlertDialog(
                        it,
                        getString(R.string.text_no_internet_available),
                        "",
                        getString(R.string.text_ok),
                        null
                    )
                }
            }
        }

        viewModel.loginResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag,"code ${response.values.code}")
                    if (response.values.status) {
                        activity?.let {
                            startActivity(Intent(it, MainActivity::class.java))
                            it.finishAffinity()
                        }
                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "login error")
                }
            }
        })
    }

    private fun validateUserInformation() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val uuid: String = UUID.randomUUID().toString()

        if (email.isEmpty()) {
            binding.edtEmail.error = getString(R.string.error_text_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = getString(R.string.error_text_valid_email)
        } else if (password.isEmpty()) {
            binding.edtPassword.error = getString(R.string.error_text_password)
        } else {
            Log.e(logTag, "Valid")
            activity?.let {
                DialogUtil.loadingAlert(it, isCancelable = false)
            }
            viewModel.login(BodyLogin(email, password, uuid))
        }
    }

    //Initialization object of GoogleSignInClient
    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSingInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                    account?.let {

                        Log.d(logTag, "Success Login User Id ${it.id} ")

                        val name = it.displayName?.split(" ")
                        val firstName = name?.get(0)
                        val lastName = name?.get(1)
                        Log.d(logTag, "account ${it.displayName} ")
                    }
                } else {
                    Log.e(logTag, "Google sign in cancelled")
                }
            } catch (e: Exception) {
                Log.e(logTag, "${e.printStackTrace()}")
            }
        }
    }

    override fun getViewModel(): Class<AuthViewModel> = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository = AuthRepository(networkModel)
}