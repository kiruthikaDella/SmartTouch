package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class ControlModeResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<ControlModeRoomData>? = null
)

data class ControlModeRoomData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vRoomName")
    var roomName: String,
    @SerializedName("deviceData")
    var deviceData: List<GetDeviceData>? = null
)

