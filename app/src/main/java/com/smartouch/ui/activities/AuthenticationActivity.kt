package com.smartouch.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.smartouch.R
import org.json.JSONObject

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class AuthenticationActivity : AppCompatActivity() {

    private val  logTag = this::class.java.simpleName
    private lateinit var callbackManager: CallbackManager
    private lateinit var accessToken: AccessToken
    private var profileTracker: ProfileTracker? = null
    private var accessTokenTracker: AccessTokenTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        initFacebookCallback()
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
                            ) {
                            }
                        }
                        accessTokenTracker?.startTracking()

                        profileTracker = object : ProfileTracker() {
                            override fun onCurrentProfileChanged(
                                profile: Profile?,
                                profile1: Profile?
                            ) {
                            }
                        }
                        profileTracker?.startTracking()

                        var email: String? = null

                        val request =
                            GraphRequest.newMeRequest(result?.accessToken) { _, response ->

                                response?.let {
                                    val json: JSONObject = it.jsonObject
                                    email = try {
                                        json.getString("email") ?: null
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
                            Log.d(logTag, "Facebook userId = ${accessToken.userId} firstname = ${it.firstName} lastName = ${it.lastName}")
                        } ?: run {
                            performFacebookLogin()
                        }

                    }

                    override fun onCancel() {
                        Toast.makeText(
                            this@AuthenticationActivity,
                            "Login Cancel",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(logTag, "Success Facebook Login onCancel")
                    }

                    override fun onError(error: FacebookException?) {
                        Toast.makeText(
                            this@AuthenticationActivity,
                            "${error?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(logTag, "Success Facebook Login onError")
                    }

                })
        } catch (e: Exception) {
            Log.i(logTag, "Success Facebook Login catch " + e.printStackTrace())
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("AuthAct", " onActivityResult ")
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}