package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ControlModeResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<ControlModeRoomData>? = null
) : Parcelable, Serializable {

    override fun toString(): String {
        return "ControlModeResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ControlModeResponse

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

@Parcelize
data class ControlModeRoomData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vRoomName")
    var roomName: String,
    @SerializedName("deviceData")
    var deviceData: ArrayList<GetDeviceData>? = null
) : Parcelable, Serializable {

    override fun toString(): String {
        return "ControlModeRoomData(id='$id', userId='$userId', roomName='$roomName', deviceData=$deviceData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ControlModeRoomData

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (roomName != other.roomName) return false
        if (deviceData != other.deviceData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + roomName.hashCode()
        result = 31 * result + (deviceData?.hashCode() ?: 0)
        return result
    }


}

