package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyGetScene(
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("iDeviceId") var deviceId: String
) {

    override fun toString(): String {
        return "BodyGetScene(roomId='$roomId', deviceId='$deviceId')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodyGetScene

        if (roomId != other.roomId) return false
        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId.hashCode()
        result = 31 * result + deviceId.hashCode()
        return result
    }


}
