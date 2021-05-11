package com.dellainfotech.smartTouch.api.repository

import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.api.SmartTouchApi
import com.dellainfotech.smartTouch.api.body.BodyAddRoom
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.common.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    private fun getAccessKey(): String {
        return FastSave.getInstance().getString(Constants.ACCESS_TOKEN, null)
    }

    suspend fun logout(bodyLogout: BodyLogout) =
        safeApiCall { smartTouchApi.logout(getAccessKey(), bodyLogout) }

    suspend fun roomType() =
        safeApiCall { smartTouchApi.roomType(getAccessKey()) }

    suspend fun getRoom() =
        safeApiCall { smartTouchApi.getRoom(getAccessKey()) }

    suspend fun addRoom(bodyAddRoom: BodyAddRoom) =
        safeApiCall { smartTouchApi.addRoom(getAccessKey(), bodyAddRoom) }
}