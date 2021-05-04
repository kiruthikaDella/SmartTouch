package com.dellainfotech.smartTouch.api

import com.dellainfotech.smartTouch.api.body.BodyLogin
import com.dellainfotech.smartTouch.api.model.LoginResponse
import com.dellainfotech.smartTouch.common.utils.Utils
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Jignesh Dangar on 04-05-2021.
 */
interface SmartTouchApi {

    companion object {
        const val BASE_URL = "https://dev.teksun.com/smarttouch/api/v1/"

        //        Authentication APIs
        const val API_LOGIN = "auth/login"
    }

    @POST(API_LOGIN)
    suspend fun loginUser(@Body bodyLogin: BodyLogin): LoginResponse

}