package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: Data? = null
)

data class Data(
    @SerializedName("user_data")
    var user_data: UserProfile? = null,
    @SerializedName("access_token")
    var accessToken: String
)
