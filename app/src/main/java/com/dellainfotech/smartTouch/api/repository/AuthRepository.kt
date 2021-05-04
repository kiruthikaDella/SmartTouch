package com.dellainfotech.smartTouch.api.repository

import com.dellainfotech.smartTouch.api.SmartTouchApi
import com.dellainfotech.smartTouch.api.body.BodyLogin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun login(bodyLogin: BodyLogin) = safeApiCall { smartTouchApi.loginUser(bodyLogin) }
}