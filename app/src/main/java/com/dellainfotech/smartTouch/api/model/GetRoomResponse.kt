package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

data class GetRoomResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<GetRoomData>? = null
)

data class GetRoomData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iRoomTypeId")
    var roomTypeId: String,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vRoomName")
    var roomName: String,
    @SerializedName("vRoomType")
    var roomType: String
)
