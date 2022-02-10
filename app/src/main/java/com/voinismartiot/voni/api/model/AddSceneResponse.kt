package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class AddSceneResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: GetSceneData? = null,
    @SerializedName("error")
    var errorData: List<ErrorSceneData>? = null
) : Parcelable, Serializable {

    override fun toString(): String {
        return "AddSceneResponse(status=$status, code=$code, message='$message', data=$data, errorData=$errorData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddSceneResponse

        if (status != other.status) return false
        if (code != other.code) return false
        if (message != other.message) return false
        if (data != other.data) return false
        if (errorData != other.errorData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + code
        result = 31 * result + message.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (errorData?.hashCode() ?: 0)
        return result
    }


}

@Parcelize
data class ErrorSceneData(
    @SerializedName("iRoomId")
    var roomId: String,
    @SerializedName("iDeviceId")
    var deviceId: String,
    @SerializedName("iDeviceSwitchId")
    var deviceSwitchId: String,
    @SerializedName("message")
    var message: String
) : Parcelable, Serializable {

    override fun toString(): String {
        return "ErrorSceneData(roomId='$roomId', deviceId='$deviceId', deviceSwitchId='$deviceSwitchId', message='$message')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ErrorSceneData

        if (roomId != other.roomId) return false
        if (deviceId != other.deviceId) return false
        if (deviceSwitchId != other.deviceSwitchId) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId.hashCode()
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + deviceSwitchId.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }


}