package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 11-05-2021.
 */
data class RoomTypeResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<RoomTypeData>? = null
)

data class RoomTypeData(
    @SerializedName("_id")
    var roomTypeId: String,
    @SerializedName("vName")
    var roomName: String
)
