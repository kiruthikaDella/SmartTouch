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
            arrayListOf("public_profile")
        )
    }

    //Initialization object of Callback manager and register callback of LoginManager when login success or failed this callbacks are called
    private fun initFacebookCallback() {

        Log.e(logTag, " initFacebookCallback ")

        try {
            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        Log.i(logTag, "Success Facebook Login")
                        accessToken = result?.accessToken!!

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

                        var email = ""

                        val request =
                            GraphRequest.newMeRequest(result.accessToken) { _, response ->

                                response?.let {
                                    val json: JSONObject? = it.jsonObject
                                    email = try {
                                        json?.getString("email") ?: ""
                                    } catch (e: java.lang.Exception) {
                                        ""
                                    }
                                }
                            }

                        val parameter = Bundle()
                        parameter.putString(
                            "fields",
                            "id, first_name, last_name,email,gender,birthday"
                        )
                        request.parameters = parameter
                        request.executeAsync()

                        val profile = Profile.getCurrentProfile()

                        profile?.let {
                            Log.d(
                                logTag,
                                "Facebook userId = ${accessToken.userId} firstname = ${it.firstName} lastName = ${it.lastName}"
                            )

                            facebookLoginListener?.performLogin(accessToken.userId, email)


                        } ?: run {
                            performFacebookLogin()
                        }

                    }

                    override fun onCancel() {
                        showToast("Login Cancel")
                        Log.i(logTag, "Success Facebook Login onCancel")
                    }

                    override fun onError(error: FacebookException?) {
                        showToast("${error?.message}")
                        Log.i(logTag, "Success Facebook Login onError")
                        Log.e(logTag, "FacebookException $error")
                    }

                })
        } catch (e: Exception) {
            Log.i(logTag, "Success Facebook Login catch " + e.printStackTrace())
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(logTag, " onActivityResult ")
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}