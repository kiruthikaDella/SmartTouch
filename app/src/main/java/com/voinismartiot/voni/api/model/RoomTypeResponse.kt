package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

data class RoomTypeResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<RoomTypeData>? = null
) {
    override fun toString(): String {
        return "RoomTypeResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomTypeResponse

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

data class RoomTypeData(
    @SerializedName("_id")
    var roomTypeId: String,
    @SerializedName("vName")
    var roomName: String
) {
    override fun toString(): String {
        return "RoomTypeData(roomTypeId='$roomTypeId', roomName='$roomName')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomTypeData

        if (roomTypeId != other.roomTypeId) return false
        if (roomName != other.roomName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomTypeId.hashCode()
        result = 31 * result + roomName.hashCode()
        return result
    }


}
