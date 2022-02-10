package com.voinismartiot.voni.api.repository

import com.voinismartiot.voni.api.SmartTouchApi
import com.voinismartiot.voni.api.body.BodyAddSubordinateUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManagementRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun addSubordinateUser(bodyAddSubordinateUser: BodyAddSubordinateUser) =
        safeApiCall { smartTouchApi.addSubordinateUser(getAccessKey(), bodyAddSubordinateUser) }

    suspend fun getSubordinateUser() =
        safeApiCall { smartTouchApi.getSubordinateUser(getAccessKey()) }

    suspend fun deleteSubordinateUser(subordinateUserId: String) =
        safeApiCall { smartTouchApi.deleteSubordinateUser(getAccessKey(), subordinateUserId) }
}