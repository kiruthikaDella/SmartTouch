package com.dellainfotech.smartTouch.api.repository

import com.dellainfotech.smartTouch.api.SmartTouchApi
import com.dellainfotech.smartTouch.api.body.BodyForgotPassword
import com.dellainfotech.smartTouch.api.body.BodyLogin
import com.dellainfotech.smartTouch.api.body.BodySignUp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun login(bodyLogin: BodyLogin) = safeApiCall { smartTouchApi.loginUser(bodyLogin) }

    suspend fun signUp(bodySignUp: BodySignUp) = safeApiCall { smartTouchApi.signUpUser(bodySignUp) }

    suspend fun forgotPassword(bodyForgotPassword: BodyForgotPassword) = safeApiCall { smartTouchApi.forgotPassword(bodyForgotPassword) }
}