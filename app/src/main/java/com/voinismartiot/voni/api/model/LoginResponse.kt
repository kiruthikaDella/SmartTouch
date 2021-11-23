package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: UserData? = null
) {
    override fun toString(): String {
        return "LoginResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginResponse

        if (status != other.status) return false
        if (code != other.code) return false
        if (message != other.message) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + code
        result = 31 * result + message.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }


}

data class UserData(
    @SerializedName("user_data")
    var user_data: UserProfile? = null,
    @SerializedName("access_token")
    var accessToken: String
) {
    override fun toString(): String {
        return "UserData(user_data=$user_data, accessToken='$accessToken')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserData

        if (user_data != other.user_data) return false
        if (accessToken != other.accessToken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_data?.hashCode() ?: 0
        result = 31 * result + accessToken.hashCode()
        return result
    }


}
