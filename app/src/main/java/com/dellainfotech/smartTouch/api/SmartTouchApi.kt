package com.dellainfotech.smartTouch.api

import com.dellainfotech.smartTouch.api.body.*
import com.dellainfotech.smartTouch.api.model.*
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by Jignesh Dangar on 04-05-2021.
 */
interface SmartTouchApi {

    companion object {
        const val BASE_URL = "https://dev.teksun.com/smarttouch/api/v1/"

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
        const val API_FAQ = "faq/view"
        const val API_GET_USER_PROFILE = "user/profile"
        const val API_UPDATE_USER_PROFILE = "user/profile"

        // Contact Us APIs
        const val API_FEEDBACK = "feedback/add"

        // UserManagement APIs
        const val API_ADD_SUBORDINATE_USER = "user/subordinateuser"
        const val API_GET_SUBORDINATE_USER = "user/subordinateuser"
        const val API_DELETE_SUBORDINATE_USER = "user/subordinateuser/{id}"
    }

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

    @GET(API_FAQ)
    suspend fun faq(@Header("access_key") access_key: String): FAQResponse

    @GET(API_GET_USER_PROFILE)
    suspend fun getUserProfile(@Header("access_key") access_key: String): GetProfileResponse

    @PUT(API_UPDATE_USER_PROFILE)
    suspend fun updateUserProfile(@Header("access_key") access_key: String, @Body bodyUpdateUserProfile: BodyUpdateUserProfile): GetProfileResponse

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
    //region ContactUs APIs
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
}