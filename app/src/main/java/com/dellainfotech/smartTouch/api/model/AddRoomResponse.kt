package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 11-05-2021.
 */

data class AddRoomResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<GetRoomData>? = null
)

data class AddRoomData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iRoomTypeId")
    var roomTypeId: String,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vRoomName")
    var roomName: String
)
