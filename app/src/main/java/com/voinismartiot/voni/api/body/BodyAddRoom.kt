package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyAddRoom(
    @SerializedName("iRoomTypeId") var roomTypeId: String,
    @SerializedName("vRoomName") var roomName: String
)