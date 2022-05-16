package com.voinismartiot.voni.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.voinismartiot.voni.R
import com.voinismartiot.voni.api.NetworkModule
import com.voinismartiot.voni.api.repository.AuthRepository
import com.voinismartiot.voni.common.interfaces.FacebookLoginListener
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.common.utils.hideSoftKeyboard
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.ui.viewmodel.AuthViewModel
import com.voinismartiot.voni.ui.viewmodel.ViewModelFactory
import org.json.JSONObject

class AuthenticationActivity : AppCompatActivity() {

    private val logTag = this::class.java.simpleName
    private lateinit var callbackManager: CallbackManager
    private lateinit var accessToken: AccessToken
    private var profileTracker: ProfileTracker? = null
    private var accessTokenTracker: AccessTokenTracker? = null

    private lateinit var viewModel: AuthViewModel
    private val networkModel = NetworkModule.provideSmartTouchApi(NetworkModule.provideRetrofit())

    private var facebookLoginListener: FacebookLoginListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        window?.statusBarColor = getColor(R.color.white)

        val factory = ViewModelFactory(AuthRepository(networkModel))
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        initFacebookCallback()

    }

    fun setFacebookLoginListener(listener: FacebookLoginListener) {
        this.facebookLoginListener = listener
    }

    fun performFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            arrayListOf("public_profile", "email")
        )
    }

    //Initialization object of Callback manager and register callback of LoginManager when login success or failed this callbacks are called
    private fun initFacebookCallback() {

        Log.e(logTag, " initFacebookCallback ")

        try {
            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                    override fun onCancel() {
                        showToast("Login Cancel")
                        Log.e(logTag, "Success Facebook Login onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        showToast("${error?.message}")
                        Log.e(logTag, "FacebookException $error")
                    }

                    override fun onSuccess(result: LoginResult) {
                        accessToken = result.accessToken

                        accessTokenTracker = object : AccessTokenTracker() {
                            override fun onCurrentAccessTokenChanged(
                                accessToken: AccessToken?,
                                accessToken1: AccessToken?
                            ) = Unit
                        }
                        accessTokenTracker?.startTracking()

                        profileTracker = object : ProfileTracker() {
                            override fun onCurrentProfileChanged(
                                oldProfile: Profile?,
                                currentProfile: Profile?
                            ) = Unit
                        }
                        profileTracker?.startTracking()

                        val request =
                            GraphRequest.newMeRequest(result.accessToken) { obj, response ->

                                Log.e(logTag, " GraphRequest obj $obj response $response")

                                val email = try {
                                    obj?.getString("email") ?: ""
                                } catch (e: java.lang.Exception) {
                                    ""
                                }

                                facebookLoginListener?.performLogin(accessToken.userId, email)

                            }

                        val parameter = Bundle()
                        parameter.putString(
                            "fields",
                            "id, first_name, last_name,email,gender,birthday"
                        )
                        request.parameters = parameter
                        request.executeAsync()

                       /* val profile = Profile.getCurrentProfile()

                        profile?.let {
                            Log.e(
                                logTag,
                                "Facebook userId = ${accessToken.userId} firstname = ${it.firstName} lastName = ${it.lastName} email = $email"
                            )

                            facebookLoginListener?.performLogin(accessToken.userId, email)


                        } ?: run {
                            performFacebookLogin()
                        }*/
                    }

                })


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSoftKeyboard()
    }

    override fun onDestroy() {
        hideDialog()
        super.onDestroy()
    }
}