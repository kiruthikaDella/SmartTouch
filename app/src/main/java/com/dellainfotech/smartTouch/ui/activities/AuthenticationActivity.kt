package com.dellainfotech.smartTouch.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.api.NetworkModule
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodySocialLogin
import com.dellainfotech.smartTouch.api.model.UserProfile
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.ui.viewmodel.AuthViewModel
import com.dellainfotech.smartTouch.ui.viewmodel.ViewModelFactory
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONObject
import java.util.*

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class AuthenticationActivity : AppCompatActivity() {

    private val logTag = this::class.java.simpleName
    private lateinit var callbackManager: CallbackManager
    private lateinit var accessToken: AccessToken
    private var profileTracker: ProfileTracker? = null
    private var accessTokenTracker: AccessTokenTracker? = null

    private lateinit var viewModel: AuthViewModel
    private val networkModel = NetworkModule.provideSmartTouchApi(NetworkModule.provideRetrofit())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        window?.statusBarColor = getColor(R.color.white)

        val factory = ViewModelFactory(AuthRepository(networkModel))
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        initFacebookCallback()

        viewModel.socialLoginResponse.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "code ${response.values.code}")
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        val userProfile: UserProfile? = response.values.data?.user_data
                        userProfile?.let { userData ->
                            FastSave.getInstance().saveString(Constants.USER_ID, userData.iUserId)
                            FastSave.getInstance()
                                .saveString(Constants.USER_FULL_NAME, userData.vFullName)
                            FastSave.getInstance()
                                .saveString(Constants.USERNAME, userData.vUserName)
                            FastSave.getInstance().saveString(Constants.USER_EMAIL, userData.vEmail)
                            FastSave.getInstance()
                                .saveString(Constants.USER_PHONE_NUMBER, userData.bPhoneNumber)
                            FastSave.getInstance().saveString(Constants.SOCIAL_ID, userData.socialId)
                            FastSave.getInstance().saveBoolean(Constants.isControlModePinned, userData.iIsPinStatus!!.toBoolean())

                            if (userData.userRole == Constants.MASTER_USER){
                                FastSave.getInstance().saveBoolean(Constants.IS_MASTER_USER,true)
                            }else{
                                FastSave.getInstance().saveBoolean(Constants.IS_MASTER_USER,false)
                            }

                            val sharedPreference =  getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
                            val editor = sharedPreference?.edit()
                            editor?.putInt(Constants.LOGGED_IN_TYPE,Constants.LOGIN_TYPE_GOOGLE)
                            editor?.apply()

                            FastSave.getInstance().saveInt(Constants.LOGIN_TYPE, Constants.LOGIN_TYPE_FACEBOOK)
                            startActivity(Intent(this, MainActivity::class.java))
                            finishAffinity()
                        }
                        FastSave.getInstance()
                            .saveString(Constants.ACCESS_TOKEN, response.values.data?.accessToken)

                    } else {
                        Toast.makeText(this, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "login error ${response.errorBody}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })
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
                                oldProfile: Profile?,
                                currentProfile: Profile?
                            ) {
                            }
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

                            val uuid: String = UUID.randomUUID().toString()

                            DialogUtil.loadingAlert(this@AuthenticationActivity)
                            viewModel.socialLogin(
                                BodySocialLogin(
                                    accessToken.userId,
                                    uuid,
                                    Constants.SOCIAL_LOGIN,
                                    email
                                )
                            )

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