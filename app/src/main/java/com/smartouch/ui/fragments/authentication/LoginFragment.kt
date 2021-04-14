package com.smartouch.ui.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.smartouch.R
import com.smartouch.common.utils.Utils
import com.smartouch.common.utils.Utils.isNetworkConnectivityAvailable
import com.smartouch.common.utils.Utils.showAlertDialog
import com.smartouch.databinding.FragmentLoginBinding
import com.smartouch.ui.activities.AuthenticationActivity
import com.smartouch.ui.activities.MainActivity

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val logTag = this::class.java.simpleName

    //Google SignIn
    private val GOOGLE_SIGN_IN_REQUEST = 1
    private var mGoogleSingInClient: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        binding.btnLogin.setOnClickListener {
//            validateUserInformation()
            context?.let {
                startActivity(Intent(it,MainActivity::class.java))
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
    }

    private fun validateUserInformation() {
        if (binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.error = getString(R.string.error_text_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()) {
            binding.edtEmail.error = getString(R.string.error_text_valid_email)
        } else if (binding.edtPassword.text.toString().isEmpty()) {
            binding.edtPassword.error = getString(R.string.error_text_password)
        } else {
            Log.e(logTag, "Valid")
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
}