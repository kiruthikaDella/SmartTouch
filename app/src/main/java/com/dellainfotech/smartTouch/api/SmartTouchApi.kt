package com.dellainfotech.smartTouch.api

import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.api.body.*
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Header
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
        const val API_SOCIAL_LOGIN = "auth/social-login"
        const val API_LOGOUT = "auth/logout"
    }

    @POST(API_LOGIN)
    suspend fun loginUser(@Body bodyLogin: BodyLogin): LoginResponse

    @POST(API_SIGN_UP)
    suspend fun signUpUser(@Body bodySignUp: BodySignUp): CommonResponse

    @POST(API_FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body bodyForgotPassword: BodyForgotPassword): CommonResponse

    @POST(API_SOCIAL_LOGIN)
    suspend fun socialLogin(@Body bodySocialLogin: BodySocialLogin): LoginResponse

    @POST(API_LOGOUT)
    suspend fun logout(@Header("access_key") access_key: String,@Body bodyLogout: BodyLogout): CommonResponse

}