package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyRetainState(
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("tiRetainState") var retainState: Int
)