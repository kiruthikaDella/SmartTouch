package com.dellainfotech.smartTouch.api.repository

import com.dellainfotech.smartTouch.api.SmartTouchApi
import com.dellainfotech.smartTouch.api.body.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun logout(bodyLogout: BodyLogout) =
        safeApiCall { smartTouchApi.logout(getAccessKey(), bodyLogout) }

    suspend fun roomType() =
        safeApiCall { smartTouchApi.roomType(getAccessKey()) }

    suspend fun getRoom() =
        safeApiCall { smartTouchApi.getRoom(getAccessKey()) }

    suspend fun addRoom(bodyAddRoom: BodyAddRoom) =
        safeApiCall { smartTouchApi.addRoom(getAccessKey(), bodyAddRoom) }

    suspend fun updateRoom(bodyUpdateRoom: BodyUpdateRoom) =
        safeApiCall { smartTouchApi.updateRoom(getAccessKey(), bodyUpdateRoom) }

    suspend fun retainState(bodyRetainState: BodyRetainState) =
        safeApiCall { smartTouchApi.retainState(getAccessKey(), bodyRetainState) }

    suspend fun getFAQ() =
        safeApiCall { smartTouchApi.faq(getAccessKey()) }

    suspend fun getUserProfile() =
        safeApiCall { smartTouchApi.getUserProfile(getAccessKey()) }

    suspend fun updateUserProfile(bodyUpdateUserProfile: BodyUpdateUserProfile) =
        safeApiCall { smartTouchApi.updateUserProfile(getAccessKey(), bodyUpdateUserProfile) }

    suspend fun changePassword(bodyChangePassword: BodyChangePassword) =
        safeApiCall { smartTouchApi.changePassword(getAccessKey(), bodyChangePassword) }


    //
    //region Device
    //

    suspend fun addDevice(bodyAddDevice: BodyAddDevice) =
        safeApiCall { smartTouchApi.addDevice(getAccessKey(), bodyAddDevice) }

    suspend fun getDeviceData(roomId: String) =
        safeApiCall { smartTouchApi.getDeviceData(getAccessKey(), roomId) }

    //
    //endregion
    //
}