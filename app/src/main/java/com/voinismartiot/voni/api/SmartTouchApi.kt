package com.voinismartiot.voni.api

import com.voinismartiot.voni.api.body.*
import com.voinismartiot.voni.api.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface SmartTouchApi {

    //
    //region Urls
    //

    companion object {
        const val BASE_URL = "https://dev.teksun.com/smarttouch_new/api/v1/"

        // Authentication APIs
        const val API_LOGIN = "auth/login"
        const val API_SIGN_UP = "auth/signup"
        const val API_FORGOT_PASSWORD = "auth/forgot-password"
        const val API_SOCIAL_LOGIN = "auth/social-login"

        // Home APIs
        const val API_LOGOUT = "auth/logout"
        const val API_GET_ROOM_TYPE = "room/room-type"
        const val API_GET_ROOM = "room/room"
        const val API_ADD_ROOM = "room/room"
        const val API_UPDATE_ROOM = "room/room"
        const val API_DELETE_ROOM = "room/room/{roomId}"
        const val API_FAQ = "faq/view"
        const val API_GET_USER_PROFILE = "user/profile"
        const val API_UPDATE_USER_PROFILE = "user/profile"
        const val API_CHANGE_PASSWORD = "user/change-password"

        //Device
        const val API_ADD_DEVICE = "device/add"
        const val API_GET_DEVICE_DATA = "device/device-data/{id}"
        const val API_GET_DEVICE_CUSTOMIZATION_SETTINGS = "device/device-customization-setting/{id}"
        const val API_GET_DEVICE_FEATURES_SETTINGS = "device/device-feature-setting/{id}"
        const val API_DELETE_DEVICE = "device/device/{vProductGroup}/{room_id}/{device_id}"
        const val API_UPDATE_DEVICE_NAME = "device/device-name"
        const val API_UPDATE_CUSTOMIZATION_LOCK = "device/customization-lock"
        const val API_UPDATE_SWITCH_NAME = "device/switch"
        const val API_UPDATE_SWITCH_ICON = "device/switch-icon"
        const val API_GET_ICON_LIST = "device/icon-list"
        const val API_GET_CONTROL_MODE = "device/control"
        const val API_IMAGE_UPLOAD = "device/image-upload"
        const val API_DELETE_IMAGE = "device/image-delete/{device_id}"
        const val API_GET_SCENE = "device/get-scene"
        const val API_ADD_SCENE = "device/scene"
        const val API_UPDATE_SCENE = "device/scene"
        const val API_DELETE_SCENE = "device/scene/{scene_id}"
        const val API_DELETE_SCENE_DETAIL = "device/scene-details/{scene_id}/{scene_detail_id}"
        const val API_RETAIN_STATE = "Device/retain-state"
        const val API_FACTORY_RESET = "device/factory-reset"
        const val API_FACTORY_RESET_ALL_DEVICE = "device/factory-reset-all-devices"
        const val API_UPDATE_SCENE_STATUS = "device/scene-unable-disable/{sceneId}"
        const val API_GET_DEVICE_APPLIANCES = "device/device-appliances"
        const val API_GET_DEVICE_PREVIOUS_DATA = "device/device-previous-data/{id}"

        // Contact Us APIs
        const val API_FEEDBACK = "feedback/add"

        // UserManagement APIs
        const val API_ADD_SUBORDINATE_USER = "user/subordinateuser"
        const val API_GET_SUBORDINATE_USER = "user/subordinateuser"
        const val API_DELETE_SUBORDINATE_USER = "user/subordinateuser/{id}"
        const val API_GET_PIN_STATUS = "user/pin-status"
        const val API_POST_PIN_STATUS = "user/pin-status"

        //Ownership transfer
        const val API_GET_OWNER_TRANSFER = "owner/owner-transfer"
        const val API_POST_OWNER_TRANSFER = "owner/owner-transfer"
        const val API_CANCEL_OWNER_TRANSFER = "owner/owner-transfer-cancel/{id}"

        //Device register [SmarTack and SmarTap]
        const val API_DEVICE_REGISTER = "device-tap-tack/add"
    }

    //
    //endregion
    //

    //
    //region Authentication APIs
    //

    @POST(API_LOGIN)
    suspend fun loginUser(@Body bodyLogin: BodyLogin): LoginResponse

    @POST(API_SIGN_UP)
    suspend fun signUpUser(@Body bodySignUp: BodySignUp): CommonResponse

    @POST(API_FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body bodyForgotPassword: BodyForgotPassword): CommonResponse

    @POST(API_SOCIAL_LOGIN)
    suspend fun socialLogin(@Body bodySocialLogin: BodySocialLogin): LoginResponse

    @POST(API_LOGOUT)
    suspend fun logout(
        @Header("access_key") access_key: String,
        @Body bodyLogout: BodyLogout
    ): CommonResponse

    //
    //endregion
    //

    //
    //region Home APIs
    //

    @GET(API_GET_ROOM_TYPE)
    suspend fun roomType(@Header("access_key") access_key: String): RoomTypeResponse

    @GET(API_GET_ROOM)
    suspend fun getRoom(@Header("access_key") access_key: String): GetRoomResponse

    @POST(API_ADD_ROOM)
    suspend fun addRoom(
        @Header("access_key") access_key: String,
        @Body bodyAddRoom: BodyAddRoom
    ): AddRoomResponse

    @PUT(API_UPDATE_ROOM)
    suspend fun updateRoom(
        @Header("access_key") access_key: String,
        @Body bodyUpdateRoom: BodyUpdateRoom
    ): UpdateRoomResponse

    @DELETE(API_DELETE_ROOM)
    suspend fun deleteRoom(
        @Header("access_key") access_key: String,
        @Path("roomId") roomId: String
    ): CommonResponse

    @PUT(API_RETAIN_STATE)
    suspend fun retainState(
        @Header("access_key") access_key: String,
        @Body bodyRetainState: BodyRetainState
    ): CommonResponse

    @GET(API_FAQ)
    suspend fun faq(@Header("access_key") access_key: String): FAQResponse

    @GET(API_GET_USER_PROFILE)
    suspend fun getUserProfile(@Header("access_key") access_key: String): GetProfileResponse

    @PUT(API_UPDATE_USER_PROFILE)
    suspend fun updateUserProfile(
        @Header("access_key") access_key: String,
        @Body bodyUpdateUserProfile: BodyUpdateUserProfile
    ): GetProfileResponse

    @PUT(API_CHANGE_PASSWORD)
    suspend fun changePassword(
        @Header("access_key") access_key: String,
        @Body bodyChangePassword: BodyChangePassword
    ): CommonResponse

    @GET(API_GET_PIN_STATUS)
    suspend fun getPinStatus(
        @Header("access_key") access_key: String
    ): PinResponse

    @POST(API_POST_PIN_STATUS)
    suspend fun updatePinStatus(
        @Header("access_key") access_key: String,
        @Body bodyPinStatus: BodyPinStatus
    ): PinResponse

    //
    //endregion
    //

    //
    //region ContactUs APIs
    //

    @POST(API_FEEDBACK)
    suspend fun addFeedback(
        @Header("access_key") access_key: String,
        @Body bodyFeedback: BodyFeedback
    ): CommonResponse

    //
    //endregion
    //

    //
    //region UserManagement APIs
    //

    @POST(API_ADD_SUBORDINATE_USER)
    suspend fun addSubordinateUser(
        @Header("access_key") access_key: String,
        @Body bodyAddSubordinateUser: BodyAddSubordinateUser
    ): CommonResponse

    @GET(API_GET_SUBORDINATE_USER)
    suspend fun getSubordinateUser(
        @Header("access_key") access_key: String
    ): SubordinateUserResponse

    @DELETE(API_DELETE_SUBORDINATE_USER)
    suspend fun deleteSubordinateUser(
        @Header("access_key") access_key: String,
        @Path("id") subordinateUserId: String
    ): CommonResponse

    //
    //endregion
    //

    //
    //region Device APIs
    //

    @POST(API_ADD_DEVICE)
    suspend fun addDevice(
        @Header("access_key") access_key: String,
        @Body bodyAddDevice: BodyAddDevice
    ): AddDeviceResponse

    @GET(API_GET_DEVICE_DATA)
    suspend fun getDeviceData(
        @Header("access_key") access_key: String,
        @Path("id") roomId: String
    ): GetDeviceResponse

    @GET(API_GET_DEVICE_CUSTOMIZATION_SETTINGS)
    suspend fun getDeviceCustomizationSettings(
        @Header("access_key") access_key: String,
        @Path("id") deviceId: String
    ): DeviceCustomizationResponse

    @GET(API_GET_DEVICE_FEATURES_SETTINGS)
    suspend fun getDeviceFeatureSettings(
        @Header("access_key") access_key: String,
        @Path("id") deviceId: String
    ): DeviceFeatureResponse

    @DELETE(API_DELETE_DEVICE)
    suspend fun deleteDevice(
        @Header("access_key") access_key: String,
        @Path("vProductGroup") productGroup: String,
        @Path("room_id") roomId: String,
        @Path("device_id") deviceId: String
    ): CommonResponse


    @PUT(API_UPDATE_DEVICE_NAME)
    suspend fun updateDeviceName(
        @Header("access_key") access_key: String,
        @Body bodyUpdateDeviceName: BodyUpdateDeviceName
    ): AddDeviceResponse

    @PUT(API_UPDATE_SWITCH_NAME)
    suspend fun updateSwitchName(
        @Header("access_key") access_key: String,
        @Body bodyUpdateSwitchName: BodyUpdateSwitchName
    ): UpdateSwitchNameResponse

    @GET(API_GET_ICON_LIST)
    suspend fun getIconList(
        @Header("access_key") access_key: String
    ): IconListResponse

    @PUT(API_UPDATE_CUSTOMIZATION_LOCK)
    suspend fun customizationLock(
        @Header("access_key") access_key: String,
        @Body bodyCustomizationLock: BodyCustomizationLock
    ): CommonResponse

    @PUT(API_UPDATE_SWITCH_ICON)
    suspend fun updateSwitchIcon(
        @Header("access_key") access_key: String,
        @Body bodyUpdateSwitchIcon: BodyUpdateSwitchIcon
    ): UpdateSwitchIconResponse

    @GET(API_GET_CONTROL_MODE)
    suspend fun getControlList(
        @Header("access_key") access_key: String
    ): ControlModeResponse

    @Multipart
    @POST(API_IMAGE_UPLOAD)
    suspend fun imageUpload(
        @Header("access_key") access_key: String,
        @Part("iDeviceId") deviceId: RequestBody,
        @Part image: List<MultipartBody.Part>
    ): ImageUploadResponse

    @DELETE(API_DELETE_IMAGE)
    suspend fun deleteImage(
        @Header("access_key") access_key: String,
        @Path("device_id") deviceId: String
    ): CommonResponse

    @POST(API_GET_SCENE)
    suspend fun getScene(
        @Header("access_key") access_key: String,
        @Body bodyGetScene: BodyGetScene
    ): GetSceneResponse

    @POST(API_ADD_SCENE)
    suspend fun addScene(
        @Header("access_key") access_key: String,
        @Body bodyAddScene: BodyAddScene
    ): AddSceneResponse

    @PUT(API_UPDATE_SCENE)
    suspend fun updateScene(
        @Header("access_key") access_key: String,
        @Body bodyUpdateScene: BodyUpdateScene
    ): AddSceneResponse

    @DELETE(API_DELETE_SCENE)
    suspend fun deleteScene(
        @Header("access_key") access_key: String,
        @Path("scene_id") sceneId: String
    ): CommonResponse

    @DELETE(API_DELETE_SCENE_DETAIL)
    suspend fun deleteSceneDetail(
        @Header("access_key") access_key: String,
        @Path("scene_id") sceneId: String,
        @Path("scene_detail_id") sceneDetailId: String
    ): CommonResponse

    @PUT(API_UPDATE_SCENE_STATUS)
    suspend fun updateSceneStatus(
        @Header("access_key") access_key: String,
        @Path("sceneId") sceneId: String,
        @Body bodyUpdateSceneStatus: BodyUpdateSceneStatus
    ): CommonResponse

    @POST(API_FACTORY_RESET)
    suspend fun factoryReset(
        @Header("access_key") access_key: String,
        @Body bodyFactoryReset: BodyFactoryReset
    ): CommonResponse

    @POST(API_FACTORY_RESET_ALL_DEVICE)
    suspend fun factoryResetAllDevice(
        @Header("access_key") access_key: String
    ): CommonResponse

    @GET(API_GET_DEVICE_APPLIANCES)
    suspend fun getDeviceAppliances(
        @Header("access_key") access_key: String
    ): GetDeviceAppliancesResponse

    @GET(API_GET_DEVICE_PREVIOUS_DATA)
    suspend fun getDevicePreviousData(
        @Header("access_key") access_key: String,
        @Path("id") deviceId: String
    ): GetDevicePreviousDataResponse

    //
    //endregion
    //


    //
    //region ownership transfer
    //

    @GET(API_GET_OWNER_TRANSFER)
    suspend fun getOwnership(
        @Header("access_key") access_key: String
    ): OwnershipResponse

    @POST(API_POST_OWNER_TRANSFER)
    suspend fun transferOwnership(
        @Header("access_key") access_key: String,
        @Body bodyOwnership: BodyOwnership
    ): OwnershipResponse

    @DELETE(API_CANCEL_OWNER_TRANSFER)
    suspend fun deleteOwnershipTransfer(
        @Header("access_key") access_key: String,
        @Path("id") ownershipTransferId: String
    ): CommonResponse

    //
    //endregion
    //

    //SmarTack and SmarTap
    @POST(API_DEVICE_REGISTER)
    suspend fun deviceRegister(
        @Header("access_key") access_key: String,
        @Body bodyRegisterDevice: BodyRegisterDevice
    ): CommonResponse
}