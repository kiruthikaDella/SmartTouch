package com.dellainfotech.smartTouch.api

import com.dellainfotech.smartTouch.api.body.BodyForgotPassword
import com.dellainfotech.smartTouch.api.body.BodyLogin
import com.dellainfotech.smartTouch.api.body.BodySignUp
import com.dellainfotech.smartTouch.api.model.ForgotPasswordResponse
import com.dellainfotech.smartTouch.api.model.LoginResponse
import com.dellainfotech.smartTouch.api.model.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Jignesh Dangar on 04-05-2021.
 */
interface SmartTouchApi {

    companion object {
        const val BASE_URL = "https://dev.teksun.com/smarttouch/api/v1/"

        //        Authentication APIs
        const val API_LOGIN = "auth/login"
        const val API_SIGN_UP = "auth/signup"
        const val API_FORGOT_PASSWORD = "auth/forgot-password"
    }

    @POST(API_LOGIN)
    suspend fun loginUser(@Body bodyLogin: BodyLogin): LoginResponse

    @POST(API_SIGN_UP)
    suspend fun signUpUser(@Body bodySignUp: BodySignUp): SignUpResponse

    @POST(API_FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body bodyForgotPassword: BodyForgotPassword): ForgotPasswordResponse

}