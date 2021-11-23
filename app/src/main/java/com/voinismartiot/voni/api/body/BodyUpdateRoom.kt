package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateRoom(
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("vRoomName") var roomName: String
)