package com.voinismartiot.voni.api.repository

import com.voinismartiot.voni.api.SmartTouchApi
import com.voinismartiot.voni.api.body.BodyForgotPassword
import com.voinismartiot.voni.api.body.BodyLogin
import com.voinismartiot.voni.api.body.BodySignUp
import com.voinismartiot.voni.api.body.BodySocialLogin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun login(bodyLogin: BodyLogin) = safeApiCall { smartTouchApi.loginUser(bodyLogin) }

    suspend fun signUp(bodySignUp: BodySignUp) =
        safeApiCall { smartTouchApi.signUpUser(bodySignUp) }

    suspend fun forgotPassword(bodyForgotPassword: BodyForgotPassword) =
        safeApiCall { smartTouchApi.forgotPassword(bodyForgotPassword) }

    suspend fun socialLogin(bodySocialLogin: BodySocialLogin) =
        safeApiCall { smartTouchApi.socialLogin(bodySocialLogin) }
}