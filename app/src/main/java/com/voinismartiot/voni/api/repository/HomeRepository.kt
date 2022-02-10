package com.voinismartiot.voni.api.repository

import com.voinismartiot.voni.api.SmartTouchApi
import com.voinismartiot.voni.api.body.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    //
    //region Home
    //

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

    suspend fun deleteRoom(roomId: String) =
        safeApiCall { smartTouchApi.deleteRoom(getAccessKey(), roomId) }

    suspend fun getFAQ() =
        safeApiCall { smartTouchApi.faq(getAccessKey()) }

    suspend fun getUserProfile() =
        safeApiCall { smartTouchApi.getUserProfile(getAccessKey()) }

    suspend fun updateUserProfile(bodyUpdateUserProfile: BodyUpdateUserProfile) =
        safeApiCall { smartTouchApi.updateUserProfile(getAccessKey(), bodyUpdateUserProfile) }

    suspend fun changePassword(bodyChangePassword: BodyChangePassword) =
        safeApiCall { smartTouchApi.changePassword(getAccessKey(), bodyChangePassword) }

    suspend fun getPinStatus() =
        safeApiCall { smartTouchApi.getPinStatus(getAccessKey()) }

    suspend fun updatePinStatus(bodyPinStatus: BodyPinStatus) =
        safeApiCall { smartTouchApi.updatePinStatus(getAccessKey(), bodyPinStatus) }

    //
    //endregion
    //

    //
    //region Device
    //

    suspend fun addDevice(bodyAddDevice: BodyAddDevice) =
        safeApiCall { smartTouchApi.addDevice(getAccessKey(), bodyAddDevice) }

    suspend fun getDeviceData(roomId: String) =
        safeApiCall { smartTouchApi.getDeviceData(getAccessKey(), roomId) }

    suspend fun retainState(bodyRetainState: BodyRetainState) =
        safeApiCall { smartTouchApi.retainState(getAccessKey(), bodyRetainState) }

    suspend fun getDeviceCustomizationSettings(deviceId: String) =
        safeApiCall { smartTouchApi.getDeviceCustomizationSettings(getAccessKey(), deviceId) }

    suspend fun getDeviceFeaturesSettings(deviceId: String) =
        safeApiCall { smartTouchApi.getDeviceFeatureSettings(getAccessKey(), deviceId) }

    suspend fun deleteDevice(productGroup: String, roomId: String, deviceId: String) =
        safeApiCall { smartTouchApi.deleteDevice(getAccessKey(), productGroup, roomId, deviceId) }

    suspend fun updateDeviceName(bodyUpdateDeviceName: BodyUpdateDeviceName) =
        safeApiCall { smartTouchApi.updateDeviceName(getAccessKey(), bodyUpdateDeviceName) }

    suspend fun updateSwitchName(bodyUpdateSwitchName: BodyUpdateSwitchName) =
        safeApiCall { smartTouchApi.updateSwitchName(getAccessKey(), bodyUpdateSwitchName) }

    suspend fun customizationLock(bodyCustomizationLock: BodyCustomizationLock) =
        safeApiCall { smartTouchApi.customizationLock(getAccessKey(), bodyCustomizationLock) }

    suspend fun getIconList() =
        safeApiCall { smartTouchApi.getIconList(getAccessKey()) }

    suspend fun updateSwitchIcon(bodyUpdateSwitchIcon: BodyUpdateSwitchIcon) =
        safeApiCall { smartTouchApi.updateSwitchIcon(getAccessKey(), bodyUpdateSwitchIcon) }

    suspend fun getControl() =
        safeApiCall { smartTouchApi.getControlList(getAccessKey()) }

    suspend fun imageUpload(deviceId: RequestBody, image: MutableList<MultipartBody.Part>) =
        safeApiCall { smartTouchApi.imageUpload(getAccessKey(), deviceId, image) }

    suspend fun deleteImage(deviceId: String) =
        safeApiCall { smartTouchApi.deleteImage(getAccessKey(), deviceId) }

    suspend fun getScene(bodyGetScene: BodyGetScene) =
        safeApiCall { smartTouchApi.getScene(getAccessKey(), bodyGetScene) }

    suspend fun addScene(bodyAddScene: BodyAddScene) =
        safeApiCall { smartTouchApi.addScene(getAccessKey(), bodyAddScene) }

    suspend fun updateScene(bodyUpdateScene: BodyUpdateScene) =
        safeApiCall { smartTouchApi.updateScene(getAccessKey(), bodyUpdateScene) }

    suspend fun deleteScene(sceneId: String) =
        safeApiCall { smartTouchApi.deleteScene(getAccessKey(), sceneId) }

    suspend fun deleteSceneDetail(sceneId: String, sceneDetailId: String) =
        safeApiCall { smartTouchApi.deleteSceneDetail(getAccessKey(), sceneId, sceneDetailId) }

    suspend fun factoryReset(bodyFactoryReset: BodyFactoryReset) =
        safeApiCall { smartTouchApi.factoryReset(getAccessKey(), bodyFactoryReset) }

    suspend fun factoryResetAllDevice() =
        safeApiCall { smartTouchApi.factoryResetAllDevice(getAccessKey()) }

    suspend fun updateSceneStatus(sceneId: String, bodyUpdateSceneStatus: BodyUpdateSceneStatus) =
        safeApiCall {
            smartTouchApi.updateSceneStatus(
                getAccessKey(),
                sceneId,
                bodyUpdateSceneStatus
            )
        }

    suspend fun getDeviceAppliances() =
        safeApiCall { smartTouchApi.getDeviceAppliances(getAccessKey()) }

    suspend fun getDevicePreviousData(deviceId: String) =
        safeApiCall { smartTouchApi.getDevicePreviousData(getAccessKey(), deviceId) }

    //
    //endregion
    //

    //
    //region ownership transfer
    //

    suspend fun getOwnership() =
        safeApiCall { smartTouchApi.getOwnership(getAccessKey()) }

    suspend fun transferOwnership(bodyOwnership: BodyOwnership) =
        safeApiCall { smartTouchApi.transferOwnership(getAccessKey(), bodyOwnership) }

    suspend fun cancelTransferOwnership(ownershipId: String) =
        safeApiCall { smartTouchApi.deleteOwnershipTransfer(getAccessKey(), ownershipId) }

    //
    //endregion
    //

    //SmarTack
    suspend fun deviceRegister(bodyRegisterDevice: BodyRegisterDevice) =
        safeApiCall { smartTouchApi.deviceRegister(getAccessKey(), bodyRegisterDevice) }
}