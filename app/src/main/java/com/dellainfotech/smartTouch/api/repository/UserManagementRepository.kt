package com.dellainfotech.smartTouch.api.repository

import com.dellainfotech.smartTouch.api.SmartTouchApi
import com.dellainfotech.smartTouch.api.body.BodyAddRoom
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.body.BodySubordinateUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManagementRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun addSubordinateUser(bodySubordinateUser: BodySubordinateUser) =
        safeApiCall { smartTouchApi.addSubordinateUser(getAccessKey(), bodySubordinateUser) }

    suspend fun getSubordinateUser() =
        safeApiCall { smartTouchApi.getSubordinateUser(getAccessKey()) }
}