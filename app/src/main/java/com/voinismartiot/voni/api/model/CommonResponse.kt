package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String
) {

    override fun toString(): String {
        return "CommonResponse(status=$status, code=$code, message='$message')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommonResponse

        if (status != other.status) return false
        if (code != other.code) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + code
        result = 31 * result + message.hashCode()
        return result
    }


}